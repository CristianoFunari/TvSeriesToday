package cristiano.com.tvseriestoday.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(TvSeriesDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the tvseries table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(TvSeriesContract.TvSeriesEntry.TABLE_NAME);


        mContext.deleteDatabase(TvSeriesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new TvSeriesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and tvseries entry tables
        assertTrue("Error: Your database was created without both the location entry and tvseries entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + TvSeriesContract.TvSeriesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(TvSeriesContract.TvSeriesEntry._ID);
        locationColumnHashSet.add(TvSeriesContract.TvSeriesEntry.COLUMN_DATE);
        locationColumnHashSet.add(TvSeriesContract.TvSeriesEntry.COLUMN_EPISODE);
        locationColumnHashSet.add(TvSeriesContract.TvSeriesEntry.COLUMN_ACTORS);
        locationColumnHashSet.add(TvSeriesContract.TvSeriesEntry.COLUMN_SHOWNAME);
        locationColumnHashSet.add(TvSeriesContract.TvSeriesEntry.COLUMN_TITLE);
        locationColumnHashSet.add(TvSeriesContract.TvSeriesEntry.COLUMN_SID);
        locationColumnHashSet.add(TvSeriesContract.TvSeriesEntry.COLUMN_PLOT);
        locationColumnHashSet.add(TvSeriesContract.TvSeriesEntry.COLUMN_POSTER);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }



    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createtvseriesValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testtvseriesTable() {
        // First insert the location, and then use the locationRowId to insert
        // the tvseries. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        long locationRowId = 122;

        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        TvSeriesDbHelper dbHelper = new TvSeriesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (tvseries): Create tvseries values
        ContentValues tvseriesValues = TestUtilities.createtvseriesValues(locationRowId);

        // Third Step (tvseries): Insert ContentValues into database and get a row ID back
        long tvseriesRowId = db.insert(TvSeriesContract.TvSeriesEntry.TABLE_NAME, null, tvseriesValues);
        assertTrue(tvseriesRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor tvseriesCursor = db.query(
                TvSeriesContract.TvSeriesEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from location query", tvseriesCursor.moveToFirst() );

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb tvseriesEntry failed to validate",
                tvseriesCursor, tvseriesValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from tvseries query",
                tvseriesCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        tvseriesCursor.close();
        dbHelper.close();
    }




}
