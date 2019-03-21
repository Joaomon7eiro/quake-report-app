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

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String BASE_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?";
    private static final int LOADER_ID = 1;
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

        performHttpRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.filter:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
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

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            mInfoText.setText(R.string.verify_connection);
            mInfoText.setVisibility(View.VISIBLE);
            mTryAgainButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_min_magnitude_default)
        );

        Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("format", "geojson")
                .appendQueryParameter("orderby", orderBy)
                .appendQueryParameter("minmag", minMagnitude)
                .appendQueryParameter("limit", "10")
                .build();

        mUrl = builtURI.toString();

        return new EarthquakeLoader(this, mUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        mLoadingProgressBar.setVisibility(View.GONE);
        mAdapter.clear();
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        mAdapter.clear();
    }
}
