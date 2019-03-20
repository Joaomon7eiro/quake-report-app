/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?";
    String mUrl;
    EarthquakeArrayAdapter mAdapter;
    ProgressBar mLoadingProgressBar;
    TextView mInfoText;
    Button mTryAgainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mInfoText = (TextView) findViewById(R.id.info_text);

        mTryAgainButton = (Button) findViewById(R.id.try_again);
        mTryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performHttpRequest();
            }
        });

        mAdapter = new EarthquakeArrayAdapter(this, new ArrayList<Earthquake>());
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Earthquake earthquake = (Earthquake) mAdapter.getItem(i);
                Uri url = Uri.parse(earthquake.getUrlLink());

                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                startActivity(intent);
            }
        });

        Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("format", "geojson")
                .appendQueryParameter("eventtype", "earthquake")
                .appendQueryParameter("orderby", "time")
                .appendQueryParameter("minmag", "4")
                .appendQueryParameter("limit", "10")
                .build();

        mUrl = builtURI.toString();

        performHttpRequest();
    }

    private void performHttpRequest() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            mInfoText.setVisibility(View.GONE);
            mTryAgainButton.setVisibility(View.GONE);
            EarthquakeAsyncTask earthquakeAsyncTask = new EarthquakeAsyncTask(mLoadingProgressBar, mAdapter);
            earthquakeAsyncTask.execute(mUrl);
        } else {
            mInfoText.setText(R.string.verify_connection);
            mInfoText.setVisibility(View.VISIBLE);
            mTryAgainButton.setVisibility(View.VISIBLE);
        }
    }

    private class EarthquakeAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {
        private WeakReference<ProgressBar> mProgressBar;
        private WeakReference<EarthquakeArrayAdapter> mEarthquakeAdapter;

        public EarthquakeAsyncTask(ProgressBar progressBar, EarthquakeArrayAdapter adapter) {
            mProgressBar = new WeakReference<>(progressBar);
            mEarthquakeAdapter = new WeakReference<>(adapter);
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.get().setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Earthquake> doInBackground(String... urls) {
            if (urls[0] == null || urls.length < 1) {
                return null;
            }
            // Create a list of earthquake locations.
            ArrayList<Earthquake> earthquakesResults = QueryUtils.extractEarthquakes(urls[0]);
            return earthquakesResults;
        }

        @Override
        protected void onPostExecute(ArrayList<Earthquake> earthquakeArrayList) {
            mProgressBar.get().setVisibility(View.GONE);
            // Clear the adapter of previous earthquake data
            mEarthquakeAdapter.get().clear();
            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (earthquakeArrayList != null && !earthquakeArrayList.isEmpty()) {
                mEarthquakeAdapter.get().addAll(earthquakeArrayList);
            }
        }
    }
}
