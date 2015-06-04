/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cristiano.com.tvseriestoday;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cristiano.com.tvseriestoday.data.TvSeriesContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String TVSERIES_SHARE_HASHTAG = " #NextTvSeries";

    private ShareActionProvider mShareActionProvider;
    private String mTvSeriesDetail;
    private Uri mUri;
    private String mDate;
    private String mTitleCalendar;
    private static final int DETAIL_LOADER = 0;

    private static final String[] TVSERIES_COLUMNS = {

            TvSeriesContract.TvSeriesEntry._ID,
            TvSeriesContract.TvSeriesEntry.COLUMN_POSTER,
            TvSeriesContract.TvSeriesEntry.COLUMN_SHOWNAME,
            TvSeriesContract.TvSeriesEntry.COLUMN_SID,
            TvSeriesContract.TvSeriesEntry.COLUMN_TITLE,
            TvSeriesContract.TvSeriesEntry.COLUMN_DATE,
            TvSeriesContract.TvSeriesEntry.COLUMN_PLOT,
            TvSeriesContract.TvSeriesEntry.COLUMN_ACTORS,
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

    private ImageView mIconView;
    private TextView mShownameView;
    private TextView mEpisodeView;
    private TextView mTileView;
    private TextView mPlotView;
    private TextView mActorsView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.poster_icon);
        mShownameView = (TextView) rootView.findViewById(R.id.detail_showname_textview);
        mEpisodeView = (TextView) rootView.findViewById(R.id.detail_episode_textview);
        mTileView = (TextView) rootView.findViewById(R.id.detail_title_textview);
        mPlotView = (TextView) rootView.findViewById(R.id.detail_plot_textview);
        mActorsView = (TextView) rootView.findViewById(R.id.detail_actors_textview);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.detailfragment, menu);


        MenuItem menuItem = menu.findItem(R.id.action_share);


        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);


        if (mTvSeriesDetail != null) {
            mShareActionProvider.setShareIntent(createShareTvSeriesIntent());
        }
    }

    private Intent createShareTvSeriesIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, TVSERIES_SHARE_HASHTAG + mTvSeriesDetail);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {

            return new CursorLoader(
                    getActivity(),
                    mUri,
                    TVSERIES_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {


            new ImageLoadTask(data.getString(COLUMN_POSTER), mIconView).execute();
            String showname = data.getString(COLUMN_SHOWNAME);
            mShownameView.setText(showname);

            String episode = data.getString(COLUMN_EPISODE);
            mEpisodeView.setText(episode);
            String title = data.getString(COLUMN_TITLE);
            mTileView.setText(title);
            String plot = "Plot: " + data.getString(COLUMN_PLOT);
            mPlotView.setText(plot);
            String actors = "Actors: " + data.getString(COLUMN_ACTORS);
            mActorsView.setText(actors);
            mDate = Utility.formatDateString(data.getString(COLUMN_DATE));

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareTvSeriesIntent());
            }

            mTvSeriesDetail = " See this episode"+showname + " " + episode +"@" + mDate;
            mTitleCalendar = showname + " " + episode;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    public void shareOnCalendar() throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(mDate));
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", false);
        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
        intent.putExtra("title", mTitleCalendar);
        startActivity(intent);
    }

}