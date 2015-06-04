package cristiano.com.tvseriestoday.data;

import android.content.ContentValues;
import android.net.Uri;
import android.test.AndroidTestCase;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cristiano.com.tvseriestoday.pojo.TvSeriesDTO;

/**
 * Created by Lavoro on 04/04/2015.
 */
public class TestSync extends AndroidTestCase {

    public static final String LOG_TAG = TestSync.class.getSimpleName();

    public void testTraktConnection (){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        Uri builtUri = Uri.parse("https://api-v2launch.trakt.tv/calendars/all/shows/2015-04-01/7").buildUpon()
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
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(TvSeriesContract.TvSeriesEntry.CONTENT_URI, cvArray);

                // delete old data so we don't build up an endless history
                getContext().getContentResolver().delete(TvSeriesContract.TvSeriesEntry.CONTENT_URI,
                        TvSeriesContract.TvSeriesEntry.COLUMN_DATE + " <= ?",
                        new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});

                notify();
            }

            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");



        } catch (Exception e) {
            Log.e(LOG_TAG, "Errore nel ritrovamento dei dati");
        }
    }

    private void getTvSeriesDetailDataFromDTO(TvSeriesDTO dto){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        Uri builtUri = Uri.parse("http://www.omdbapi.com/?").buildUpon()
                .appendQueryParameter("i", dto.getSid())
                .appendQueryParameter("plot", "full")
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
            Log.e(LOG_TAG,"Errore nel ritrovamento dei dati");
        }
    }

    private void getTvSeriesDetailDataFromJson (TvSeriesDTO dto,String json) throws JSONException {
        final String OMDB_PLOT = "Plot";
        final String OMDB_ACTORS = "Actors" ;
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


}

