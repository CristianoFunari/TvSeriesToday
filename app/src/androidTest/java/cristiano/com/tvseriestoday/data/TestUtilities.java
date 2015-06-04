package cristiano.com.tvseriestoday.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your tvseriesContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_LOCATION = "99705";
    static final String TEST_DATE = "2015-04-08T08:30:00.000Z";  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default tvseries values for your database tests.
     */
    static ContentValues createtvseriesValues(long locationRowId) {
        ContentValues tvseriesValues = new ContentValues();

        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_DATE, TEST_DATE);
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_EPISODE, "1x12");
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_PLOT, "DJSJHDSIDHSKDSKD");
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_SHOWNAME, "Ciccio alle crociate");
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_TITLE, "Episodio 12");
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_SID, 1245);
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_POSTER,"hsjdgsaidfd");
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_ACTORS,"dsjhdhsdbsj");
        return tvseriesValues;
    }
    /*
           Students: You can uncomment this helper function once you have finished creating the
           LocationEntry part of the tvseriesContract.
        */
    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues tvseriesValues = new ContentValues();

        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_DATE, TEST_DATE);
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_EPISODE, "1x12");

        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_SHOWNAME, "Ciccio alle crociate");
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_TITLE, "Episodio 12");
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_SID, 1245);
        tvseriesValues.put(TvSeriesContract.TvSeriesEntry.COLUMN_DATE,1419120000L);
        return tvseriesValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the tvseriesContract as well as the tvseriesDbHelper.
     */
    static long insertNorthPoleLocationValues(Context context) {
        // insert our test records into the database
        TvSeriesDbHelper dbHelper = new TvSeriesDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(TvSeriesContract.TvSeriesEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

}
