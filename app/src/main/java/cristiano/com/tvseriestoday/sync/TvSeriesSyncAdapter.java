package cristiano.com.tvseriestoday.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import cristiano.com.tvseriestoday.R;
import cristiano.com.tvseriestoday.data.TvSeriesContract;
import cristiano.com.tvseriestoday.pojo.TvSeriesDTO;

/**
 * Created by Lavoro on 04/04/2015.
 */
public class TvSeriesSyncAdapter  extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = TvSeriesSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    public TvSeriesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
       Log.v(LOG_TAG,"onPerformSync start");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String data = sdf.format(new Date());
        Context context = getContext();


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String numberOfDaysKey = context.getString(R.string.pref_nDays_key);
        String nDays = prefs.getString(numberOfDaysKey,context.getString(R.string.pref_nDays_default));



        Uri builtUri = Uri.parse("https://api-v2launch.trakt.tv/calendars/all/shows/"+data+"/"+nDays+"").buildUpon()
                .appendQueryParameter("extended", "full") .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());


            // Create the request to Trakt, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("trakt-api-version", "2");
            urlConnection.setRequestProperty("trakt-api-key", "88515599db34ee417fa0a1c8a1cda9bcdbba896ab8f9c25f96e950e7e8be5b26");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonStr = buffer.toString();
            List<TvSeriesDTO> series =  getTvSeriesDataFromJSON(jsonStr);

            // Insert the new tvseries information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(series.size());

            for(int i = 0; i < series.size(); i++) {
                TvSeriesDTO dto = series.get(i);
                getTvSeriesDetailDataFromDTO(dto);

                ContentValues seriesValues = new ContentValues();

                seriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_DATE,dto.getDate());
                seriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_SHOWNAME,dto.getShowname());
                seriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_TITLE,dto.getTitle());
                seriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_ACTORS,dto.getActors());
                seriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_EPISODE,dto.getEpisode());
                seriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_PLOT,dto.getPlot());
                seriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_POSTER,dto.getPoster());
                seriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_SID,dto.getSid());

                cVVector.add(seriesValues);
            }
            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {

                // delete old data so we don't build up an endless history
                getContext().getContentResolver().delete(TvSeriesContract.TvSeriesEntry.CONTENT_URI,
                        null,
                        null);

                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(TvSeriesContract.TvSeriesEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");



        } catch (Exception e) {
            Log.e(LOG_TAG,"Errore nel ritrovamento dei dati");
        }
    }

    private void getTvSeriesDetailDataFromDTO(TvSeriesDTO dto){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String fullplotKey = getContext().getString(R.string.pref_enable_fullplot_key);
        boolean fullplot = prefs.getBoolean(fullplotKey,
                Boolean.parseBoolean(getContext().getString(R.string.pref_enable_fullplot_default)));
        String fullPlotString;
        if(fullplot)
            fullPlotString = "full";
        else{
            fullPlotString ="short";
        }


        Uri builtUri = Uri.parse("http://www.omdbapi.com/?").buildUpon()
                .appendQueryParameter("i", dto.getSid())
                .appendQueryParameter("plot", fullPlotString)
                .appendQueryParameter("r","json")
                 .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());


            // Create the request to OMDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonStr = buffer.toString();
            getTvSeriesDetailDataFromJson(dto,jsonStr);

        }
        catch(Exception e){
            Log.e(LOG_TAG,"Errore nel ritrovamento dei dati su OMDB per ID=" + dto.getSid());
        }
        }

    private void getTvSeriesDetailDataFromJson (TvSeriesDTO dto,String json) throws JSONException {
        final String OMDB_PLOT = "Plot";
        final String OMDB_ACTORS = "Actors";
        final String OMDB_POSTER = "Poster";

        JSONObject obj = new JSONObject(json);

        dto.setActors(obj.getString(OMDB_ACTORS));
        dto.setPlot(obj.getString(OMDB_PLOT));
        dto.setPoster(obj.getString(OMDB_POSTER));


    }


    private List<TvSeriesDTO> getTvSeriesDataFromJSON(String json) throws JSONException {

        final String TRK_FIRSTAIRED = "first_aired";
        final String TRK_EPISODE= "episode";
        final String TRK_SEASON =  "season";
        final String TRK_NUMBER =   "number";
        final String TRK_TITLE =   "title";
        final String TRK_IDS =      "ids";
        final String TRK_IDIMDB =   "imdb";
        final String TRK_SHOW =  "show";


        List<TvSeriesDTO> records = new ArrayList<TvSeriesDTO>();
        //Variables needed
        String showname;
        String title;
        String episode;
        String sid;
        String date;
        JSONArray tvSeriesJsonArray = new JSONArray(json);
        int counter = 0;
        Vector<ContentValues> cVVector = new Vector<ContentValues>(tvSeriesJsonArray.length());

        for(int i = 0; i < tvSeriesJsonArray.length(); i++) {
            JSONObject tvSerie = (JSONObject)tvSeriesJsonArray.get(i);

            date = tvSerie.getString(TRK_FIRSTAIRED);

            JSONObject show = tvSerie.getJSONObject(TRK_SHOW);
            showname = show.getString(TRK_TITLE);

            JSONObject episodeObj = tvSerie.getJSONObject(TRK_EPISODE);
            title = episodeObj.getString(TRK_TITLE);
            episode = episodeObj.getString(TRK_SEASON) + " x " + episodeObj.getString(TRK_NUMBER);

            JSONObject idsObj = episodeObj.getJSONObject(TRK_IDS);
            sid = idsObj.getString(TRK_IDIMDB);

            TvSeriesDTO dto = new TvSeriesDTO();

            if(!sid.equals("null") &&!sid.equals("")){
                dto.setDate(date);
                dto.setEpisode(episode);
                dto.setShowname(showname);
                dto.setTitle(title);
                dto.setSid(sid);
                records.add(dto);
            }


        }

        return records;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        TvSeriesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
