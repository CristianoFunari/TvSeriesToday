package cristiano.com.tvseriestoday.data;
import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class TvSeriesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TvSeriesDbHelper mOpenHelper;

    static final int TVSERIES = 100;
    static final int TVSERIES_IDANDSID = 101;
    static final int TVSERIES_WITHDATE= 200;

    private static final SQLiteQueryBuilder tvSeriesQueryBuilder;

    static{
        tvSeriesQueryBuilder = new SQLiteQueryBuilder();
        tvSeriesQueryBuilder.setTables(
                TvSeriesContract.TvSeriesEntry.TABLE_NAME);
    }

    //tvseries.date = ?
    private static final String sDaySelection =
            TvSeriesContract.TvSeriesEntry.TABLE_NAME +
                    "." + TvSeriesContract.TvSeriesEntry.COLUMN_DATE + " = ? ";

    //tvseries._id = ?
    private static final String sIdSelection =
            TvSeriesContract.TvSeriesEntry.TABLE_NAME +
                    "." + TvSeriesContract.TvSeriesEntry._ID + " = ? ";


    private Cursor getTtvSeriesByDate(
            Uri uri, String[] projection, String sortOrder) {

        String date = TvSeriesContract.TvSeriesEntry.getDateFromUri(uri);

        Cursor c = tvSeriesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sDaySelection,
                new String[]{date},
                null,
                null,
                sortOrder
        );
        return  c;
    }
    private Cursor getTtvSeriesById(
            Uri uri, String[] projection, String sortOrder) {

        String id = TvSeriesContract.TvSeriesEntry.getIdFromUri(uri);

        Cursor c = tvSeriesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sIdSelection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
        return  c;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TvSeriesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, TvSeriesContract.PATH_TVSERIES, TVSERIES);
        matcher.addURI(authority, TvSeriesContract.PATH_TVSERIES + "/*/#", TVSERIES_WITHDATE);
        matcher.addURI(authority, TvSeriesContract.PATH_TVSERIES + "/*", TVSERIES_IDANDSID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new TvSeriesDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case TVSERIES_WITHDATE:
                return TvSeriesContract.TvSeriesEntry.CONTENT_ITEM_TYPE;
            case TVSERIES:
                return TvSeriesContract.TvSeriesEntry.CONTENT_TYPE;
            case TVSERIES_IDANDSID:
                return TvSeriesContract.TvSeriesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case TVSERIES_WITHDATE:
            {
                retCursor = getTtvSeriesByDate(uri, projection, sortOrder);
                break;
            }

            case TVSERIES_IDANDSID: {
                retCursor = getTtvSeriesById(uri, projection, sortOrder);
            }
            break;

            case TVSERIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TvSeriesContract.TvSeriesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }



            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TVSERIES: {
                normalizeDate(values);
                long _id = db.insert(TvSeriesContract.TvSeriesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TvSeriesContract.TvSeriesEntry.buildTvseriesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case TVSERIES:
                rowsDeleted = db.delete(
                        TvSeriesContract.TvSeriesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private void normalizeDate(ContentValues values) {

        if (values.containsKey(TvSeriesContract.TvSeriesEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(TvSeriesContract.TvSeriesEntry.COLUMN_DATE);
            values.put(TvSeriesContract.TvSeriesEntry.COLUMN_DATE, TvSeriesContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TVSERIES:
                normalizeDate(values);
                rowsUpdated = db.update(TvSeriesContract.TvSeriesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TVSERIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(TvSeriesContract.TvSeriesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}