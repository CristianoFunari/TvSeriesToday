package cristiano.com.tvseriestoday.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class TvSeriesAuthenticatorService extends Service {

    private TvSeriesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {

        mAuthenticator = new TvSeriesAuthenticator(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
