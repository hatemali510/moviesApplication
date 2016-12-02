package com.example.hatem.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieDetailsFragment extends Fragment {
    String Title="";
    String Overview="";
    String Poster="";
    String Vote="";
    String Release_date="";
    String pop="";
    String votes="";
    int movie_id=-1;
    ListView lv;
    ListView lv2;
    int fav;
    ArrayList<Movie> trailers;
    ArrayList<Movie> reviews;

    private trailerAdapter data_trailerAdapter;
    private reviewAdapter data_reviewsAdapter;
    HttpURLConnection urlConnection=null;
    BufferedReader reader=null;
    String JasonString=null;
    View rootview;
    DBhelper db=new DBhelper(getActivity());


    public MovieDetailsFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        rootview=inflater.inflate(R.layout.movie_details, container, false);
        Bundle intent=getArguments();
        Title=intent.getString("title");
        Overview=intent.getString("overview");
        Vote=intent.getString("vote_average");
        Release_date=intent.getString("release_date");
        Poster=intent.getString("poster");
        pop=intent.getString("pop");
        votes=intent.getString("votes");
        movie_id=intent.getInt("id",0);
        TextView title=(TextView) rootview.findViewById(R.id.title);
        TextView overview=(TextView) rootview.findViewById(R.id.movie_desc);
        TextView release_date=(TextView) rootview.findViewById(R.id.movie_release_date);
        TextView Votes=(TextView)rootview.findViewById(R.id.movie_rating);
        ImageView poster=(ImageView) rootview.findViewById(R.id.movie_poster);
        overview.setText(Overview);
        release_date.setText(Release_date);
        Votes.setText(votes);
        title.setText(Title);
        Picasso.with(getActivity()).load(Poster).into(poster);
        String tr_url ="http://api.themoviedb.org/3/movie/"+movie_id+"/videos?api_key=7996bb4e8c234c50dea7a26a5c6657d8";
        String re_url="http://api.themoviedb.org/3/movie/"+movie_id+"/reviews?api_key=7996bb4e8c234c50dea7a26a5c6657d8";
        //load trailers
        trailers=new ArrayList<>();
        TrailerAsync trailerAsync=new TrailerAsync(tr_url);
        trailerAsync.execute();
        lv=(ListView)rootview.findViewById(R.id.trailer_list);
        data_trailerAdapter =new trailerAdapter(getActivity(),trailers,R.layout.textlist);
        data_trailerAdapter.setAdapterData(trailers);
        lv.setAdapter(data_trailerAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+trailers.get(position).getTrailer_k())));
            }
        });
        // load trailers finished
        lv.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                disabletouchofscroll(v,event);
                return true;
            }
        });

        //load reviews

        reviews=new ArrayList<>();
        RevAsync reviewsAsync=new RevAsync(re_url);
        reviewsAsync.execute();
        lv2=(ListView) rootview.findViewById(R.id.review_list);
        data_reviewsAdapter=new reviewAdapter(getActivity(),reviews,R.layout.reviewlistitem);
        data_reviewsAdapter.setAdapterData(reviews);
        lv2.setAdapter(data_reviewsAdapter);
        //load reviews finished
        lv2.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                disabletouchofscroll(v,event);
                return true;
            }
        });
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean idHasBeenGenerated = prefs.getBoolean("idgenerated"+movie_id, false);
        final SharedPreferences.Editor editor=prefs.edit();
        final Button fav_button=(Button) rootview.findViewById(R.id.favorite);
        final Button unfav_button=(Button) rootview.findViewById(R.id.favorite2);
        if(!idHasBeenGenerated) {
            fav_button.setVisibility(View.VISIBLE);
            unfav_button.setVisibility(View.INVISIBLE);
            //put in shared
        }
        else {
            unfav_button.setVisibility(View.VISIBLE);
            fav_button.setVisibility(View.INVISIBLE);
            // remove from shared
        }
        fav_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editor.putString(""+movie_id,""+movie_id);
                editor.putBoolean("idgenerated"+movie_id, true);
                editor.commit();
                unfav_button.setVisibility(View.VISIBLE);
                fav_button.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "add",
                        Toast.LENGTH_SHORT).show();
            }
        });
        unfav_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                editor.remove(""+movie_id);
                editor.putBoolean("idgenerated"+movie_id, false);
                editor.commit();
                fav_button.setVisibility(View.VISIBLE);
                unfav_button.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "remove",
                        Toast.LENGTH_SHORT).show();
            }
        });


     return rootview;
    }

    public class TrailerAsync extends AsyncTask<String,Void,ArrayList> {
        private  String base_url;
        //constractur
        public TrailerAsync( String url) {this.base_url=url;}
        @Override
        protected ArrayList doInBackground(String... params) {
            try {
                reader = new BufferedReader(new InputStreamReader(buildconnection(base_url)));
                JSONArray Trailers = generate_Jason(reader).optJSONArray("results");
                for (int i = 0; i < Trailers.length(); i++) {
                    JSONObject Trailer = Trailers.optJSONObject(i);
                    String name=Trailer.getString("name");
                    String key=Trailer.getString("key");
                    Movie movie=new Movie();
                    movie.setTrailer_name(name);
                    movie.setTrailer_k(key);
                    trailers.add(movie);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }
            return trailers;
        }
        protected void onPostExecute(ArrayList t) {
            if(t.size()>0){
                data_trailerAdapter.setAdapterData(t);
            }
            else {
                Toast.makeText(getActivity(),"no trailers",Toast.LENGTH_SHORT).show();
            }
        }

    }
   public class RevAsync extends AsyncTask<String,Void,ArrayList> {
        //private final String LOG_TAG = TrailerAsync.class.getSimpleName();
        private  String base_url;
        //constractur
        public RevAsync( String url) {this.base_url=url;}
        @Override
        protected ArrayList doInBackground(String... params) {
            try {
                reader = new BufferedReader(new InputStreamReader(buildconnection(base_url)));
                JSONArray Reviews = generate_Jason(reader).optJSONArray("results");
                for (int i = 0; i < Reviews.length(); i++) {
                    JSONObject review = Reviews.optJSONObject(i);
                    Movie movie=new Movie();
                    movie.setReview_author(review.getString("author"));
                    movie.setReview_content(review.getString("content"));
                    reviews.add(movie);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }

            return reviews;
        }
        protected void onPostExecute(ArrayList r) {
            if(r.size()>0){
                data_reviewsAdapter.setAdapterData(r);

            }
            else {
                Toast.makeText(getActivity(),"no reviews for this movie ",Toast.LENGTH_SHORT).show();
            }
        }

    }
    public InputStream buildconnection(String s ){
        Uri builtUri = Uri.parse(s).buildUpon().build();
        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            return inputStream;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public JSONObject generate_Jason(BufferedReader r) {
        StringBuffer buffer = new StringBuffer();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            JasonString = buffer.toString();
            JSONObject response = new JSONObject(JasonString);
            return response;
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
    public void disabletouchofscroll(View v, MotionEvent event){
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                v.getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                v.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        v.onTouchEvent(event);
    }




}


