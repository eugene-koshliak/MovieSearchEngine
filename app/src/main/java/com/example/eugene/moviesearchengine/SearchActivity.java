package com.example.eugene.moviesearchengine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class SearchActivity extends Activity {

    // Tag for the log messages
    public static final String LOG_TAG = SearchActivity.class.getSimpleName();
    private static String searchOmdbRequestUrl = "http://www.omdbapi.com/?s=";
    private static String movieTitle;
    private static ArrayList<Movies> movies;
    private ListView moviesListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final Button findButton = (Button) findViewById(R.id.find_button);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText searchText = (EditText) findViewById(R.id.search_text);
                movieTitle = searchText.getText().toString().replace(" ", "+");
                moviesListView = (ListView) findViewById(R.id.movies_list);

                MoviesAsyncTask task = new MoviesAsyncTask();
                task.execute();

                moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                            long id) {
                        TextView selectedItemTitleView = (TextView) itemClicked.findViewById(R.id.movie_title);
                        String selectedItemTitle = selectedItemTitleView.getText().toString();

                        Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                        intent.putExtra(DetailActivity.MOVIE_TITLE, selectedItemTitle);
                        intent.setType("text/plain");
                        startActivity(intent);
                    }
                });

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.favorites_menu_item:
                Intent intent = new Intent(SearchActivity.this, FavoritesActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private URL createUrl() {
        URL url;
        try {
            url = new URL(searchOmdbRequestUrl + movieTitle);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON result", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonResponse;
    }

    /*
    Convert the InputStream into a String which contains the
    whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /*
    Return an Movies objects by parsing out information
    about the movies from the input moviesJSON string.
     */
    private Movies extractDataFromJson(String moviesJSON) {
        Bitmap poster;

        if(TextUtils.isEmpty(moviesJSON)) {
            return null;
        }

        try {
            JSONObject jsonRootObject = new JSONObject(moviesJSON);
            JSONArray jsonArraySearch = jsonRootObject.getJSONArray("Search");

            for(int i=0; i<jsonArraySearch.length(); i++) {
                JSONObject jsonObject = jsonArraySearch.getJSONObject(i);

                String posterUrl = jsonObject.getString("Poster");
                poster = imageDownload(posterUrl);

                String title = jsonObject.getString("Title");
                String type = jsonObject.getString("Type");
                String year = jsonObject.getString("Year");

                movies.add(new Movies(poster, title, type, year));
            }

            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }

    private Bitmap imageDownload(String url) {

        Bitmap mIcon11 = null;

        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    private class MoviesAsyncTask extends AsyncTask<URL, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            movies = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl();

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the movie JSON result", e);
            }

            extractDataFromJson(jsonResponse);
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);

            MoviesAdapter adapter = new MoviesAdapter(SearchActivity.this, movies);
            adapter.notifyDataSetChanged();
            moviesListView.setAdapter(adapter);
        }
    }
}
