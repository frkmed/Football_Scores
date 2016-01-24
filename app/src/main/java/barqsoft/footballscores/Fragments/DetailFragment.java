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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        textView = (TextView) rootView.findViewById(R.id.detail_text);

        requestQueue = Volley.newRequestQueue(getActivity());



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
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

        Log.v(LOG_TAG, data.getString(COL_ID) + " Loaded");
        Log.v(LOG_TAG, data.getString(COL_HOME)+ " Loaded");
        Log.v(LOG_TAG, data.getString(COL_AWAY)+ " Loaded");
        Log.v(LOG_TAG, data.getString(COL_DATE)+ " Loaded");
        Log.v(LOG_TAG, data.getString(COL_DATE)+ " Loaded");


        CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.GET,
                "http://api.football-data.org/v1/fixtures/" + data.getString(COL_ID),
                new JSONObject(),
                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            JSONObject jsonObject = response.getJSONObject("head2head");
                            String count = jsonObject.getString("count");
                            String homeWins = jsonObject.getString("homeTeamWins");
                            String awayWins = jsonObject.getString("awayTeamWins");
                            String draws = jsonObject.getString("draws");
                            Log.v(LOG_TAG, "count " + count + " Home Wins:" + homeWins
                                    + " Away Wins" + awayWins + " Draws:" + draws);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }




                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class CustomJsonObjectRequest extends JsonObjectRequest
    {
        public CustomJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener listener, Response.ErrorListener errorListener)
        {
            super(method, url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map getHeaders() throws AuthFailureError {
            Map headers = new HashMap();
            headers.put("X-Auth-Token", getString(R.string.Football_api_key));
            return headers;
        }

    }
}
