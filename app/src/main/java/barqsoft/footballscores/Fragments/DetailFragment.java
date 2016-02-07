package barqsoft.footballscores.Fragments;

/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import barqsoft.footballscores.Database.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utils.Utilies;

/**
 * Created by paskalstoyanov on 22/01/16.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private String mScores;
    private String mMatch_Id;
    private String[] fragment_match_id = new String[1];

    private static final int DETAIL_LOADER = 0;

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;


    private TextView textView;
    RequestQueue requestQueue;

    // Details score_list_item

    private TextView homeName;
    private TextView awayName;
    private ImageView homeCrest;
    private ImageView awayCrest;
    private TextView scoreText;
    private TextView dataDateTextview;
    private TextView leagueTextview;
    private Button mShareButton;

    // Details additional pane
    private TextView matchdayTextview;
    private TextView totalMatches_count;
    private TextView homeTeam_name_add_pane;
    private TextView homeTeam_wins_add_pane;
    private TextView awayTeam_name_add_pane;
    private TextView awayTeam_wins_add_pane;
    private TextView draws_count;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    public void setFragmentDate(String date) {
        fragment_match_id[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mMatch_Id = intent.getStringExtra(Intent.EXTRA_TEXT);
            setFragmentDate(mMatch_Id);
        }


        View rootView = inflater.inflate(R.layout.fragment_detail_start, container, false);

        // Details score list item
        homeName = (TextView) rootView.findViewById(R.id.home_name);
        awayName = (TextView) rootView.findViewById(R.id.away_name);
        homeCrest = (ImageView) rootView.findViewById(R.id.home_crest);
        awayCrest = (ImageView) rootView.findViewById(R.id.away_crest);
        scoreText = (TextView) rootView.findViewById(R.id.score_textview);
        dataDateTextview = (TextView) rootView.findViewById(R.id.data_date_textview);
        leagueTextview = (TextView) rootView.findViewById(R.id.league_textview);
        matchdayTextview = (TextView) rootView.findViewById(R.id.matchday_textview);
        // Share button
        mShareButton = (Button) rootView.findViewById(R.id.share_button);
        mShareButton.setVisibility(View.VISIBLE);


        rootView.findViewById(R.id.scores_list_card).setFocusable(false);



        // Details additional pane
        totalMatches_count = (TextView) rootView.findViewById(R.id.detail_total_matches_count_textview);
        homeTeam_name_add_pane = (TextView) rootView.findViewById(R.id.home_name_label_textview);
        awayTeam_name_add_pane = (TextView) rootView.findViewById(R.id.away_name_label_textview);
        homeTeam_wins_add_pane = (TextView) rootView.findViewById(R.id.detail_home_wins_textview);
        awayTeam_wins_add_pane = (TextView) rootView.findViewById(R.id.detail_away_wins_textview);
        draws_count = (TextView) rootView.findViewById(R.id.detail_draw_count_textview);

        requestQueue = Volley.newRequestQueue(getActivity());


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mMatch_Id != null || mMatch_Id.length() > 0) {

            //Log.v(LOG_TAG, mUri.toString());
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(getActivity(),
                    DatabaseContract.scores_table.buildScoreWithId(),
                    null,
                    null,
                    fragment_match_id,
                    null
            );


        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) {
            return;
        }

        // Detail Score List Item
        final String dataHomeName = data.getString(COL_HOME);
        final String dataAwayName = data.getString(COL_AWAY);
        final String score = Utilies.getScores(data.getInt(COL_HOME_GOALS), data.getInt(COL_AWAY_GOALS));
        String dataDate = data.getString(COL_MATCHTIME);
        String league = Utilies.getLeague(data.getInt(COL_LEAGUE));
        String matchday = Utilies.getMatchDay(data.getInt(COL_MATCHDAY),
                data.getInt(COL_LEAGUE));

        homeName.setText(dataHomeName);
        awayName.setText(dataAwayName);
        homeCrest.setImageResource(Utilies.getTeamCrestByTeamName(
                data.getString(COL_HOME)));
        awayCrest.setImageResource(Utilies.getTeamCrestByTeamName(
                data.getString(COL_AWAY)));
        scoreText.setText(score);

        dataDateTextview.setText(dataDate);
        leagueTextview.setText(league);
        matchdayTextview.setText(matchday);

        if (data.getInt(COL_HOME_GOALS) == -1 )
        {
            scoreText.setContentDescription(getString(R.string.a11y_score_waiting));
        }
        else
        {
            scoreText.setContentDescription(getString(R.string.a11y_score,score));
        }
        homeCrest.setContentDescription(null);
        awayCrest.setContentDescription(null);
        leagueTextview.setContentDescription(getString(R.string.a11y_league, league));


        mShareButton.setContentDescription(getContext().getString(R.string.share_text));

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //add Share Action
                getContext().startActivity(createShareForecastIntent(dataHomeName + " "
                        + score +" "+ dataAwayName + " "));
            }
        });


        // Detail additional pane
        homeTeam_name_add_pane.setText(dataHomeName);
        awayTeam_name_add_pane.setText(dataAwayName);


        CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.GET,
                "http://api.football-data.org/v1/fixtures/" + data.getString(COL_ID),
                new JSONObject(),
                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            JSONObject jsonObject = response.getJSONObject("head2head");
                            String count = jsonObject.getString("count");
                            totalMatches_count.setText(count);
                            String homeWins = jsonObject.getString("homeTeamWins");
                            homeTeam_wins_add_pane.setText(homeWins + " wins");
                            String awayWins = jsonObject.getString("awayTeamWins");
                            awayTeam_wins_add_pane.setText(awayWins + " wins");
                            String draws = jsonObject.getString("draws");
                            draws_count.setText(draws);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class CustomJsonObjectRequest extends JsonObjectRequest {
        public CustomJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map getHeaders() throws AuthFailureError {
            Map headers = new HashMap();
            headers.put("X-Auth-Token", getString(R.string.api_key));
            return headers;
        }

    }

    // Share Match Intent
    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + getString(R.string.football_scores_hashtag));
        return shareIntent;
    }
}
