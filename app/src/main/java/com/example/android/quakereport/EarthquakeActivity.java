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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        HttpRequestTask httpRequestTask = new HttpRequestTask(earthquakeListView);
        httpRequestTask.execute();
    }

    private class HttpRequestTask extends AsyncTask<String, Void, ArrayList<Earthquake>>{
        WeakReference<ListView> mListView;

        public HttpRequestTask(ListView listView) {
            mListView = new WeakReference<>(listView);
        }

        @Override
        protected ArrayList<Earthquake> doInBackground(String... strings) {
            ArrayList<Earthquake> earthquakes = null;
            // Create a fake list of earthquake locations.
            try {
                 earthquakes = QueryUtils.extractEarthquakes();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return earthquakes;
        }

        @Override
        protected void onPostExecute(ArrayList<Earthquake> earthquakeArrayList) {
            // Create a new {@link ArrayAdapter} of earthquakes
            final EarthquakeArrayAdapter adapter = new EarthquakeArrayAdapter(getApplication(),
                    earthquakeArrayList);
            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            mListView.get().setAdapter(adapter);

            mListView.get().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Earthquake earthquake = (Earthquake) adapter.getItem(i);
                    Uri url = Uri.parse(earthquake.getUrlLink());

                    Intent intent = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(intent);
                }
            });
        }
    }
}
