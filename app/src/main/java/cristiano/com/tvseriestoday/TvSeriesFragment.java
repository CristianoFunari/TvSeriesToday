package cristiano.com.tvseriestoday;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.ParseException;

import cristiano.com.tvseriestoday.data.TvSeriesContract;
import cristiano.com.tvseriestoday.sync.TvSeriesSyncAdapter;

/**
 * Created by Lavoro on 07/04/2015.
 */
public class TvSeriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final String[] TVSERIES_COLUMNS = {

            TvSeriesContract.TvSeriesEntry._ID ,
            TvSeriesContract.TvSeriesEntry.COLUMN_POSTER ,
            TvSeriesContract.TvSeriesEntry.COLUMN_SHOWNAME ,
            TvSeriesContract.TvSeriesEntry.COLUMN_SID ,
            TvSeriesContract.TvSeriesEntry.COLUMN_TITLE ,
            TvSeriesContract.TvSeriesEntry.COLUMN_DATE ,
            TvSeriesContract.TvSeriesEntry.COLUMN_PLOT ,
            TvSeriesContract.TvSeriesEntry.COLUMN_ACTORS ,
            TvSeriesContract.TvSeriesEntry.COLUMN_EPISODE

    };
    static final int _ID = 0;
    static final int COLUMN_POSTER = 1;
    static final int COLUMN_SHOWNAME = 2;
    static final int COLUMN_SID = 3;
    static final int COLUMN_TITLE = 4;
    static final int COLUMN_DATE = 5;
    static final int COLUMN_PLOT = 6;
    static final int COLUMN_ACTORS = 7;
    static final int COLUMN_EPISODE = 8;

    private final String LOG_TAG = TvSeriesFragment.class.getSimpleName();
    private static final int TVSERIES_LOADER = 0;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    TvSeriesAdapter mTvSeriesAdapter;
    private ListView mListView;

    public interface Callback {

        public void onItemSelected(Uri dateUri);
    }

    public TvSeriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    void sync( ) {
        TvSeriesSyncAdapter.syncImmediately(getActivity());
        getLoaderManager().restartLoader(TVSERIES_LOADER, null, this);
    }




    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTvSeriesAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTvSeriesAdapter.swapCursor(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mTvSeriesAdapter = new TvSeriesAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview);
        mListView.setAdapter(mTvSeriesAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    try {

                        ((Callback) getActivity())
                                .onItemSelected(TvSeriesContract.TvSeriesEntry.buildTvSeriesIDLong(
                                        cursor.getLong(_ID)
                                ));
                    } catch (Exception e) {
                        Log.e("LOG_TAG","Errore nel caricamento del cursore");
                    }
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        String sortOrder = TvSeriesContract.TvSeriesEntry.COLUMN_DATE + " ASC";

        Uri weatherForLocationUri = null;
        try {
            weatherForLocationUri = TvSeriesContract.TvSeriesEntry.buildTvSeriesWithStartDate("2015-04-08 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                TVSERIES_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TVSERIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


}
