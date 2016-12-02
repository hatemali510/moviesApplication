package com.example.hatem.movieapp;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hatem on 11/10/2016.
 */




public class Movie  {
    private String mOriginalTitle;
    private String mPosterPath;
    private String review_content;
    private String review_author;
    private int fav;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;
    public String getReview_content() {
        return review_content;
    }

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public void setReview_content(String review_content) {
        this.review_content = review_content;
    }

    public String getReview_author() {
        return review_author;
    }

    public void setReview_author(String review_author) {
        this.review_author = review_author;
    }

    int id;


    public String getTrailer_k() {
        return trailer_k;
    }

    public void setTrailer_k(String trailer_k) {
        this.trailer_k = trailer_k;
    }

    public String getTrailer_name() {
        return trailer_name;
    }

    public void setTrailer_name(String trailer_name) {
        this.trailer_name = trailer_name;
    }


    private String trailer_name;
    private String trailer_k;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    private String overview;
    private String vote_average;
    private String popularity;
    private String votes;

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }



    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    private String release_date;


    public Movie() {
    }
    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }
    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }
    public String getOriginalTitle() {
        return mOriginalTitle;
    }
    public String getPosterPath() {
        return  mPosterPath;
    }
}