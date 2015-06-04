package cristiano.com.tvseriestoday;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Lavoro on 07/04/2015.
 */
public class TvSeriesAdapter extends CursorAdapter{


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }


    public static class ViewHolder {

        public final TextView dateView;

        public final TextView shownameView;
        public final TextView episodeView;

        public ViewHolder(View view) {

            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            shownameView = (TextView) view.findViewById(R.id.list_item_showname_textview);
            episodeView = (TextView) view.findViewById(R.id.list_item_episode_textview);
        }
    }

    public TvSeriesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();


        String date = cursor.getString(TvSeriesFragment.COLUMN_DATE);


        viewHolder.dateView.setText(Utility.formatDateString(date));

        String description = cursor.getString(TvSeriesFragment.COLUMN_EPISODE);

        viewHolder.episodeView.setText(description);


        String showname = cursor.getString(TvSeriesFragment.COLUMN_SHOWNAME);

        viewHolder.shownameView.setText(showname);

    }
}
