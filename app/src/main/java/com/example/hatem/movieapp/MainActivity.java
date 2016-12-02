package com.example.hatem.movieapp;

// we need to recall the oncreate function to update the movie according to sort option in setting menu
// accorrding to the question on stackoverflow and this answer in this link
//http://stackoverflow.com/questions/7150014/how-to-restart-the-oncreate-function


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements detailsListener {
    boolean isTwoPane = false;
    HttpURLConnection urlConnection = null;
    DBhelper db = new DBhelper(this);
    BufferedReader reader = null;
    String JasonString = null;
    private GridView gridview;
    private ImageAdapter movie_adapter;
    private ArrayList<Movie> movie_grid_data;
    String title = "";
    int size = 0;
    String overview = "";
    String poster = "";
    String vote_average = "";
    String release_date = "";
    String popularity = "";
    String votes = "";
    int id = 0;
    ArrayList<Movie> new_data;
    String most_popular = "https://api.themoviedb.org/3/discover/movie?api_key=7996bb4e8c234c50dea7a26a5c6657d8";
    String highest_rated = "http://api.themoviedb.org/3/movie/top_rated?api_key=7996bb4e8c234c50dea7a26a5c6657d8";
    String base_url = most_popular;
    MovieAsync movieTask;
    Bundle b=new Bundle();
    private detailsListener mlistenrer;

    void setMlistenrer(detailsListener m) {
        this.mlistenrer = m;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movie_adapter = new ImageAdapter(this, movie_grid_data);
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(movie_adapter);
        setMlistenrer(this);
        if (null != findViewById(R.id.activitydeatils)) {
            isTwoPane = true;
        }
        loadActivity(base_url);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie;
                movie = movie_grid_data.get(position);

                mlistenrer.setMoviedetails(movie);

            }
        });


    }

    @Override
    public void setMoviedetails(Movie movie) {

        if (!isTwoPane) {
            Intent i = new Intent(MainActivity.this, MovieDetails.class);
            i.putExtra("title", movie.getOriginalTitle()).
                    putExtra("vote_average", movie.getVote_average()).
                    putExtra("overview", movie.getOverview()).
                    putExtra("poster", movie.getPosterPath()).
                    putExtra("release_date", movie.getRelease_date())
                    .putExtra("votes", movie.getVotes())
                    .putExtra("pop", movie.getPopularity())
                    .putExtra("id", movie.getId());
            startActivity(i);

        } else {
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            Bundle bundle = new Bundle();

            bundle.putString("title", movie.getOriginalTitle());
            bundle.putString("poster", movie.getPosterPath());
            bundle.putString("release_date", movie.getRelease_date());
            bundle.putString("vote_average", movie.getVote_average());
            bundle.putString("overview", movie.getOverview());
            bundle.putString("votes", movie.getVotes());
            bundle.putString("pop", movie.getPopularity());
            bundle.putInt("id", movie.getId());

            movieDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.activitydeatils, movieDetailsFragment, "").commit();
        }

    }

    private void loadActivity(String url) {

        movie_grid_data = new ArrayList<>();
        b.putString("url",url);
        if (url.equals("favorite")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Map<String, ?> allEntries = prefs.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String s=entry.getValue().toString();
                if (s.matches("[0-9]+")) {
                    int id=Integer.parseInt(s);
                    Log.d("ids",""+id);
                    Cursor res=db.getData(id);
                    res.moveToFirst();
                    movie_grid_data.add(fromCurtoMovie(res));
                    Log.d("size",""+movie_grid_data.size());

                }
            }
           movie_adapter = new ImageAdapter(this, movie_grid_data);
           gridview.setAdapter(movie_adapter);
            if (movie_grid_data.size() == 0) {
                Toast.makeText(getApplication().getBaseContext(), "No favorites has found",
                        Toast.LENGTH_LONG).show();
            }
        }
        if (isNetworkAvailable()) {
            movieTask = new MovieAsync(url);
            movieTask.execute();

        } else {
            if (url.equals(highest_rated)) {
                movie_grid_data = db.getAllmovies(2);
                movie_adapter = new ImageAdapter(this, movie_grid_data);
                gridview.setAdapter(movie_adapter);

            }
            if (url.equals(most_popular)) {
                movie_grid_data = db.getAllmovies(1);
                movie_adapter = new ImageAdapter(this, movie_grid_data);
                gridview.setAdapter(movie_adapter);

            }
        }


    }


    public class MovieAsync extends AsyncTask<String, Void, ArrayList> {
        private String base_url;

        public MovieAsync(String url) {
            this.base_url = url;
        }

        @Override

        protected ArrayList doInBackground(String... params) {

            try {
                JSONArray movies = create_conn(base_url).optJSONArray("results");
                for (int i = 0; i < movies.length(); i++) {
                    JSONObject movie = movies.optJSONObject(i);
                    Movie item = new Movie();
                    id = movie.getInt("id");
                    title = movie.getString("original_title");
                    overview = movie.getString("overview");
                    release_date = movie.getString("release_date");
                    vote_average = movie.getString("vote_average");
                    poster = "http://image.tmdb.org/t/p/w185/" + movie.getString("poster_path");
                    popularity = movie.getString("popularity");
                    votes = movie.getString("vote_count");
                    item.setPosterPath(poster);
                    item.setOriginalTitle(title);
                    item.setOverview(overview);
                    item.setRelease_date(release_date);
                    item.setVote_average(vote_average);
                    item.setPopularity(popularity);
                    item.setVotes(votes);
                    item.setId(id);
                    movie_grid_data.add(item);
                    int type = 0;
                    if (base_url.equals(most_popular)) {
                        type = 1;
                    }
                    if (base_url.equals(highest_rated)) {
                        type = 2;
                    }
                    Log.d("type", "" + type);
                        db.insertmovie(id, title, overview, release_date, vote_average, poster, popularity, votes, type);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return movie_grid_data;

        }

        protected void onPostExecute(ArrayList movies) {
            if (movies.size() > 0) {
                movie_adapter.setAdapterData(movies);
                Log.e("size", "size is =" + movies.size());

            } else {
                Toast.makeText(getApplication().getBaseContext(), "no data found or no internet connection has found ",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main2, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most:
                base_url = most_popular;
                loadActivity(base_url);
                return true;
            case R.id.high:
                base_url = highest_rated;
                loadActivity(base_url);
                return true;
            case R.id.fav:
                base_url = "favorite";
                loadActivity(base_url);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private JSONObject create_conn(String uRl) {
        JSONObject response = null;
        InputStream inputStream = null;
        Uri builtUri = Uri.parse(uRl).buildUpon().build();
        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }


        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
            return null;
        }

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }

            JasonString = buffer.toString();
            //append the movies objects from jason to movies array return type of the asynchtask

            // Parsing the feed results and get the list
            response = new JSONObject(JasonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response.length() > 0) {
            return response;
        } else {
            return null;
        }
    }
    //"(id integer primary key, title text, overview text, release text, v_av text, poster text, pop text, votes text, fav integer, type int)"
    public Movie fromCurtoMovie(Cursor cur){
        Movie movie=new Movie();
        movie.setId(cur.getInt(cur.getColumnIndex("id")));
        movie.setOriginalTitle(cur.getString(cur.getColumnIndex("title")));
        movie.setOverview(cur.getString(cur.getColumnIndex("overview")));
        movie.setRelease_date(cur.getString(cur.getColumnIndex("release")));
        movie.setVote_average(cur.getString(cur.getColumnIndex("v_av")));
        movie.setPosterPath(cur.getString(cur.getColumnIndex("poster")));
        movie.setPopularity(cur.getString(cur.getColumnIndex("pop")));
        movie.setVotes(cur.getString(cur.getColumnIndex("votes")));
        movie.setType(cur.getInt(cur.getColumnIndex("type")));
        return movie;
    }

}