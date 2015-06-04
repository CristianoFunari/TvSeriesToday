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
package cristiano.com.tvseriestoday.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.text.ParseException;

/*
    Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
    that at least the basic functionality has been implemented correctly.

    Students: Uncomment the tests in this class as you implement the functionality in your
    ContentProvider to make sure that you've implemented things reasonably correctly.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                TvSeriesContract.TvSeriesEntry.CONTENT_URI,
                null,
                null
        );


        Cursor cursor = mContext.getContentResolver().query(
                TvSeriesContract.TvSeriesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from tvseries table during delete", 0, cursor.getCount());
        cursor.close();

    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the tvseriesProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // tvseriesProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                TvSeriesProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: tvseriesProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + TvSeriesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, TvSeriesContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: tvseriesProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() throws ParseException {
        // content://com.example.android.sunshine.app/tvseries/
        String type = mContext.getContentResolver().getType(TvSeriesContract.TvSeriesEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/tvseries
        assertEquals("Error: the tvseriesEntry CONTENT_URI should return tvseriesEntry.CONTENT_TYPE",
                TvSeriesContract.TvSeriesEntry.CONTENT_TYPE, type);


        String testDate = "2015-04-08T08:30:00.000Z"; // December 21st, 2014
        // content://com.example.android.sunshine.app/tvseries/94074/20140612
        type = mContext.getContentResolver().getType(
                TvSeriesContract.TvSeriesEntry.buildTvSeriesWithStartDate(testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/tvseries/1419120000
        assertEquals("Error: the tvseriesEntry CONTENT_URI with location and date should return tvseriesEntry.CONTENT_ITEM_TYPE",
                TvSeriesContract.TvSeriesEntry.CONTENT_TYPE, type);


    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic tvseries query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasictvseriesQuery() {
        // insert our test records into the database
        TvSeriesDbHelper dbHelper = new TvSeriesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);

        // Fantastic.  Now that we have a location, add some tvseries!
        ContentValues tvseriesValues = TestUtilities.createtvseriesValues(locationRowId);

        long tvseriesRowId = db.insert(TvSeriesContract.TvSeriesEntry.TABLE_NAME, null, tvseriesValues);
        assertTrue("Unable to Insert tvseriesEntry into the Database", tvseriesRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor tvseriesCursor = mContext.getContentResolver().query(
                TvSeriesContract.TvSeriesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasictvseriesQuery", tvseriesCursor, tvseriesValues);
    }



}
