package barqsoft.footballscores.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FootballScoresSyncService extends Service {
    public static final String LOG_TAG = FootballScoresSyncService.class.getName();
    private static final Object sSyncAdapterLock = new Object();
    private static FootballScoresSyncAdapter sFootballScoresSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate - FootballScoresSyncService");
        synchronized (sSyncAdapterLock) {
            if (sFootballScoresSyncAdapter == null) {
                sFootballScoresSyncAdapter = new FootballScoresSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sFootballScoresSyncAdapter.getSyncAdapterBinder();
    }
}