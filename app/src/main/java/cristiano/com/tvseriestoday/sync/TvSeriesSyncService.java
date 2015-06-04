package cristiano.com.tvseriestoday.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TvSeriesSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static TvSeriesSyncAdapter sTvSeriesSyncAdapter = null;

    @Override
    public void onCreate() {

        synchronized (sSyncAdapterLock) {
            if (sTvSeriesSyncAdapter == null) {
                sTvSeriesSyncAdapter = new TvSeriesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sTvSeriesSyncAdapter.getSyncAdapterBinder();
    }
}