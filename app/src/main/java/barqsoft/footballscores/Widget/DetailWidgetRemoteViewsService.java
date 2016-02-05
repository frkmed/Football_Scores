package barqsoft.footballscores.Widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.Activities.DetailActivity;
import barqsoft.footballscores.Database.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utils.Utilies;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget.
 * Created by paskalstoyanov on 01/02/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();

    // these indices must match the projection
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_THE_ID = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                String[] fragmentdate = new String[1];
                SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                Date mDate = new Date(System.currentTimeMillis());
                fragmentdate[0] = mformat.format(mDate);

                Uri footballScoresToday = DatabaseContract.scores_table.buildScoreWithDate();
                data = getContentResolver().query(footballScoresToday,
                        null,
                        null,
                        fragmentdate,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);
                String matchId = data.getString(COL_ID);
                int home_crest = Utilies.getTeamCrestByTeamName(data.getString(COL_HOME));
                int away_crest = Utilies.getTeamCrestByTeamName(data.getString(COL_AWAY));

                String description;


                String HomeTeam = data.getString(COL_HOME);
                String AwayTeam = data.getString(COL_AWAY);

                views.setTextViewText(R.id.home_name, HomeTeam);
                views.setTextViewText(R.id.away_name, AwayTeam);

                //String formattedDate = Utility.getFriendlyDayString(
                //        DetailWidgetRemoteViewsService.this, dateInMillis, false);
                int homeGoals = data.getInt(COL_HOME_GOALS);
                int awayGoals = data.getInt(COL_AWAY_GOALS);
                String formattedScore =
                        Utilies.getScores(homeGoals, awayGoals);
                String dateText = data.getString(COL_MATCHTIME);
                views.setTextViewText(R.id.score_textview,formattedScore);
                if (homeGoals == -1)
                {
                    description = getString(R.string.a11y_score_waiting);
                }
                else
                {
                    description = getString(R.string.a11y_score, formattedScore);
                }
                views.setTextViewText(R.id.data_date_textview, dateText);

                views.setImageViewResource(R.id.home_crest, home_crest);
                views.setImageViewResource(R.id.away_crest, away_crest);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, description);
                }


                final Intent fillInIntent = new Intent(getApplicationContext(),DetailActivity.class);
                fillInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                fillInIntent.putExtra(Intent.EXTRA_TEXT, matchId);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.home_crest, null);
                views.setContentDescription(R.id.away_crest, null);
                views.setContentDescription(R.id.score_textview, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(COL_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
