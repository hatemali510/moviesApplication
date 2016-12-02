package com.example.hatem.movieapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hatem on 12/10/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private  Context mContext;
    private   ArrayList<Movie> Movies;
    public ImageAdapter(Context context, ArrayList<Movie> movies) {
        this.Movies=movies;
        this.mContext=context;
    }

    public int getCount() {
        if (Movies == null || Movies.size() == 0) {
            return -1;
        }

        return Movies.size();
    }
    public void setAdapterData(ArrayList<Movie> movies){
        this.Movies=movies;
        notifyDataSetChanged();
    }
    public Movie getItem(int position) {
        if (Movies == null || Movies.size() == 0) {
            return null;
        }

        return Movies.get(position);
    }
    public long getItemId(int postion){
        return 0;
    }

    
    public View getView(int position, View convertView, ViewGroup parent){
        ImageView image;
        if (convertView==null){
            image=new ImageView(mContext);
            image.setAdjustViewBounds(true);
        }
        else {
            image=(ImageView) convertView;
        }

        Picasso.with(mContext).load(Movies.get(position).getPosterPath()).into(image);
        return image;

    }

}
