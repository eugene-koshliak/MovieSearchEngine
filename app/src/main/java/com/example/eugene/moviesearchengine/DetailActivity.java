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
import android.widget.ImageView;
import android.widget.TextView;

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

import static com.example.eugene.moviesearchengine.SearchActivity.LOG_TAG;


public class DetailActivity extends Activity {

    public static final String MOVIE_TITLE = "title";
    private static String detailOmdbRequestUrl = "http://www.omdbapi.com/?y=&plot=full&t=";
    private static String movieTitle;
    private static Movies movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        movieTitle = intent.getStringExtra("title").replace(" ", "+");

        DetailMovieAsyncTask task = new DetailMovieAsyncTask();
        task.execute();
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
                Intent intent = new Intent(DetailActivity.this, FavoritesActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private URL createUrl() {
        URL url;
        try {
            url = new URL(detailOmdbRequestUrl + movieTitle);
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
            System.out.println(urlConnection.getResponseCode());
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

            String imageUrl = jsonRootObject.getString("Poster");
            poster = imageDownload(imageUrl);

            String title = jsonRootObject.getString("Title");
            String year = jsonRootObject.getString("Year");
            String type = jsonRootObject.getString("Type");
            String director = jsonRootObject.getString("Director");
            String country = jsonRootObject.getString("Country");
            String imdbRating = jsonRootObject.getString("imdbRating");
            String imdbVotes = jsonRootObject.getString("imdbVotes");
            String writer = jsonRootObject.getString("Writer");
            String actors = jsonRootObject.getString("Actors");

            return new Movies(title, year, poster, director, writer, actors, country, imdbRating, type, imdbVotes);
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

    private class DetailMovieAsyncTask extends AsyncTask<URL, Void, Void> {

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

            movie = extractDataFromJson(jsonResponse);
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            super.onPostExecute(results);

            ImageView posterView = (ImageView) findViewById(R.id.detail_poster);
            posterView.setImageBitmap(movie.getPoster());

            TextView titleView = (TextView) findViewById(R.id.detail_title);
            String movieTitle = "Title: " + movie.getTitle();
            titleView.setText(movieTitle);

            TextView yearView = (TextView) findViewById(R.id.detail_year);
            String movieYear = "Year: " + movie.getYear();
            yearView.setText(movieYear);

            TextView typeView = (TextView) findViewById(R.id.detail_type);
            String movieType = "Type: " + movie.getType();
            typeView.setText(movieType);

            TextView directorView = (TextView) findViewById(R.id.detail_director);
            String movieDirector = "Director: " + movie.getDirector();
            directorView.setText(movieDirector);

            TextView countryView = (TextView) findViewById(R.id.detail_country);
            String movieCountry = "Country: " + movie.getCountry();
            countryView.setText(movieCountry);

            TextView imdbRatingView = (TextView) findViewById(R.id.detail_imdbRating);
            String movieImdbRating = "IMDB Rating: " + movie.getImdbRating();
            imdbRatingView.setText(movieImdbRating);

            TextView imdbVotesView = (TextView) findViewById(R.id.detail_imdbVotes);
            String movieImdbVotes = "IMDB Votes: " + movie.getImdbVotes();
            imdbVotesView.setText(movieImdbVotes);

            TextView writerView = (TextView) findViewById(R.id.detail_writer);
            String movieWriter = "Writer: " + movie.getWriter();
            writerView.setText(movieWriter);

            TextView actorsView = (TextView) findViewById(R.id.detail_actors);
            String movieActors = "Actors: " + movie.getActors();
            actorsView.setText(movieActors);
        }
    }

}
