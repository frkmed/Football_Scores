package barqsoft.footballscores.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.Database.DatabaseContract;
import barqsoft.footballscores.ItemChoiceManager;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utils.Utilies;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class RecyclerScoresAdapter extends RecyclerView.Adapter<RecyclerScoresAdapter.ScoresAdapterViewHolder> {
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
    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    private static final int VIEW_TYPE_EXPANDED = 0;
    private static final int VIEW_TYPE_COLAPSED = 1;
    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseExpandedLayout = true;

    final private ScoresAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private ItemChoiceManager mICM;


    //public RecyclerScoresAdapter(Context context,Cursor cursor,int flags)
    //{
    //    super(context,cursor,flags);
    //}


    private Cursor mCursor;
    final private Context mContext;

    /**
     * Cache of the children views for a forecast list item.
     */
    public class ScoresAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final ImageView mHome_Crest;
        public final TextView mHome_nameText;

        public final TextView mScoreText;
        public final TextView mDateText;

        public final ImageView mAway_Crest;
        public final TextView mAway_nameText;


        public final TextView mLeague_Text;
        public final TextView mMatchday_Text;

        public final Button mShare_Button;


        public ScoresAdapterViewHolder(View view) {
            super(view);
            mHome_Crest = (ImageView) view.findViewById(R.id.home_crest);
            mHome_nameText = (TextView) view.findViewById(R.id.home_name);

            mScoreText = (TextView) view.findViewById(R.id.score_textview);
            mDateText = (TextView) view.findViewById(R.id.data_date_textview);

            mAway_Crest = (ImageView) view.findViewById(R.id.away_crest);
            mAway_nameText = (TextView) view.findViewById(R.id.away_name);

            mLeague_Text = (TextView) view.findViewById(R.id.league_textview);
            mMatchday_Text = (TextView) view.findViewById(R.id.matchday_textview);

            mShare_Button = (Button) view.findViewById(R.id.share_button);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v)
        {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int match_Id_Index = mCursor.getColumnIndex(DatabaseContract.scores_table.MATCH_ID);
            mClickHandler.onClick(mCursor.getLong(match_Id_Index), this);
            mICM.onClick(this);
        }
    }

    public static interface ScoresAdapterOnClickHandler {
        void onClick(Long match_Id, ScoresAdapterViewHolder vh);
    }



    public RecyclerScoresAdapter(Context context, ScoresAdapterOnClickHandler dh, View emptyView, int choiceMode)
    {
        mContext = context;
        mClickHandler = dh;
        mEmptyView = emptyView;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
    }

    /*
        This takes advantage of the fact that the viewGroup passed to onCreateViewHolder is the
        RecyclerView that will be used to contain the view, so that it can get the current
        ItemSelectionManager from the view.
        One could implement this pattern without modifying RecyclerView by taking advantage
        of the view tag to store the ItemChoiceManager.
     */
    @Override
    public ScoresAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            int layoutId = R.layout.scores_list_item;
            switch (viewType) {
                case VIEW_TYPE_COLAPSED: {
                    layoutId = R.layout.scores_list_item;
                    break;
                }
            }
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            //view.setFocusable(true);
            return new ScoresAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerViewSelection");
        }
    }


    @Override
    public void onBindViewHolder(ScoresAdapterViewHolder scoresAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        final String HomeTeam = mCursor.getString(COL_HOME);
        final String AwayTeam = mCursor.getString(COL_AWAY);

        String dateText = mCursor.getString(COL_MATCHTIME);
        final String scoreText = Utilies.getScores(mCursor.getInt(COL_HOME_GOALS), mCursor.getInt(COL_AWAY_GOALS));

        double match_id = mCursor.getDouble(COL_ID);

        scoresAdapterViewHolder.mHome_Crest.setImageResource(Utilies.getTeamCrestByTeamName(
                mCursor.getString(COL_HOME)));
        scoresAdapterViewHolder.mHome_nameText.setText(HomeTeam);

        scoresAdapterViewHolder.mScoreText.setText(scoreText);
        scoresAdapterViewHolder.mDateText.setText(dateText);

        scoresAdapterViewHolder.mAway_Crest.setImageResource(Utilies.getTeamCrestByTeamName(
                mCursor.getString(COL_AWAY)));
        scoresAdapterViewHolder.mAway_nameText.setText(AwayTeam);


        String Match_day = Utilies.getMatchDay(mCursor.getInt(COL_MATCHDAY),
                mCursor.getInt(COL_LEAGUE));
        scoresAdapterViewHolder.mMatchday_Text.setText(Match_day);

        String League = Utilies.getLeague(mCursor.getInt(COL_LEAGUE));
        scoresAdapterViewHolder.mLeague_Text.setText(League);

        if (mCursor.getInt(COL_HOME_GOALS) == -1 )
        {
            scoresAdapterViewHolder.mScoreText.setContentDescription(mContext.getString(R.string.a11y_score_waiting));
        }
        else
        {
            scoresAdapterViewHolder.mScoreText.setContentDescription(mContext.getString(R.string.a11y_score,scoreText));
        }
        scoresAdapterViewHolder.mHome_Crest.setContentDescription(null);
        scoresAdapterViewHolder.mAway_Crest.setContentDescription(null);
        scoresAdapterViewHolder.mLeague_Text.setContentDescription(mContext.getString(R.string.a11y_league, League));

        mICM.onBindViewHolder(scoresAdapterViewHolder, position);


    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseExpandedLayout = useTodayLayout;
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }


    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseExpandedLayout) ? VIEW_TYPE_EXPANDED : VIEW_TYPE_COLAPSED;
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof ScoresAdapterViewHolder ) {
            ScoresAdapterViewHolder vfh = (ScoresAdapterViewHolder) viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }


    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }




}
