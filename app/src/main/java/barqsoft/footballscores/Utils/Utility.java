package barqsoft.footballscores.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import java.text.SimpleDateFormat;

import barqsoft.footballscores.Database.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Sync.FootballScoresSyncAdapter;

/**
 * Created by paskalstoyanov on 21/01/16.
 */
public class Utility {

    final String LOG_TAG = Utility.class.getSimpleName();



    // A helper function to grab the name of day.
    public static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if (julianDay == currentJulianDay + 1) {
            return context.getString(R.string.tomorrow);
        } else if (julianDay == currentJulianDay - 1) {
            return context.getString(R.string.yesterday);
        } else {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    public static String getMatchDayCount(String match_day, Context context)
    {
        Context mContext = context;
        Cursor data = null;
        String[] fragmentdate = new String[1];
        Uri footballScoresToday = DatabaseContract.scores_table.buildScoreWithDate();
        fragmentdate[0] = match_day;
        data = context.getContentResolver().query(footballScoresToday,
                null,
                null,
                fragmentdate,
                null);

        if(data != null)
        {
            data.close();
            String dayMatchCount = String.valueOf(data.getCount()) + " matches";
            return dayMatchCount;
        }
        data.close();

        return "Not available";
    }

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return
     */
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     *
     * @param c Context used to get the SharedPreferences
     * @return the location status integer type
     */
    @SuppressWarnings("ResourceType")
    static public @FootballScoresSyncAdapter.ScoresStatus
    int getScoresStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_football_data_status_key), FootballScoresSyncAdapter.SCORES_STATUS_UNKNOWN);
    }


}
