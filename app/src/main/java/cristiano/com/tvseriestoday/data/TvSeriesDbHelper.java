package cristiano.com.tvseriestoday.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for tvseries data.
 */
public class TvSeriesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "tvseries.db";

    public TvSeriesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + TvSeriesContract.TvSeriesEntry.TABLE_NAME + " (" +
                TvSeriesContract.TvSeriesEntry._ID + " INTEGER PRIMARY KEY," +
                TvSeriesContract.TvSeriesEntry.COLUMN_EPISODE + " TEXT NOT NULL, " +
                TvSeriesContract.TvSeriesEntry.COLUMN_POSTER + " TEXT, " +
                TvSeriesContract.TvSeriesEntry.COLUMN_SHOWNAME + " TEXT NOT NULL, " +
                TvSeriesContract.TvSeriesEntry.COLUMN_SID + " TEXT, " +
                TvSeriesContract.TvSeriesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                TvSeriesContract.TvSeriesEntry.COLUMN_DATE + "     NOT NULL, " +
                TvSeriesContract.TvSeriesEntry.COLUMN_PLOT + " TEXT , " +
                TvSeriesContract.TvSeriesEntry.COLUMN_ACTORS + " TEXT " +
                " );";


        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TvSeriesContract.TvSeriesEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
