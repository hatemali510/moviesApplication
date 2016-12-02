package com.example.hatem.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by hatem on 25/11/2016.
 */
public class DBhelper extends SQLiteOpenHelper {
    public static final String db_name="movie_app";
    public static final String table_name="movies";

    public DBhelper(Context context) {
        super(context, db_name , null, 1);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table movies " +
                        "(id integer primary key, title text, overview text, release text, v_av text, poster text, pop text, votes text,  type int)"
        );
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS movies");
        onCreate(db);
    }
    public void upgrade(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS movies");
        onCreate(db);

    }

    public boolean insertmovie (int id,String title, String overview, String release, String vote_avg,String poster,String pop,String votes,int type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("title", title);
        contentValues.put("overview", overview);
        contentValues.put("release", release);
        contentValues.put("v_av", vote_avg);
        contentValues.put("poster", poster);
        contentValues.put("pop", pop);
        contentValues.put("votes", votes);
        contentValues.put("type", type);
        db.insert(table_name, null, contentValues);
        return true;
    }

    public ArrayList<Movie> getAllmovies(int type) {
        ArrayList<Movie> movie_array = new ArrayList<Movie>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from movies where type="+ type+"", null );

        while(res.moveToNext()){
            Movie movie=new Movie();
            movie.setId(res.getInt(res.getColumnIndex("id")));
            movie.setTrailer_name(res.getString(res.getColumnIndex("title")));
            movie.setOverview(res.getString(res.getColumnIndex("overview")));
            movie.setRelease_date(res.getString(res.getColumnIndex("release")));
            movie.setVote_average(res.getString(res.getColumnIndex("v_av")));
            movie.setPosterPath(res.getString(res.getColumnIndex("poster")));
            movie.setPopularity(res.getString(res.getColumnIndex("pop")));
            movie.setVotes(res.getString(res.getColumnIndex("votes")));
            movie.setType(res.getInt(res.getColumnIndex("type")));
            movie_array.add(movie);
        }
        return movie_array;
    }
    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from movies where id="+id+"", null );
        return res;
    }
}
