package cristiano.com.tvseriestoday.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Lavoro on 04/04/2015.
 */
public class TvSeriesContract {

    public static final String CONTENT_AUTHORITY = "cristiano.com.tvseriestoday";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_TVSERIES = "tvseries";

    /* Inner class that defines the table contents of the location table */
    public static final class TvSeriesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TVSERIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TVSERIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TVSERIES;

        // Table name
        public static final String TABLE_NAME = "tvseries";

        public static final String COLUMN_SHOWNAME = "showname";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_EPISODE = "episode";
        public static final String COLUMN_SID = "sid";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_ACTORS = "actors";

        public static Uri buildTvseriesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


        public static Uri buildTvSeriesWithStartDate(
                String startDateS) throws ParseException {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            long startDate = sdf.parse(startDateS).getTime();
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_DATE, startDateS).build();
        }

        public static Uri buildTvSeriesIDLong(
                long id){
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id)).build();
        }
    }

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
}
