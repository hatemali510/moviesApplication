package com.example.hatem.movieapp;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hatem on 23/11/2016.
 */
public class reviewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> reviews;
    private int layoutResourceId;
    reviewAdapter(Context context, ArrayList<Movie> m, int layoutResourceId){
        this.mContext=context;
        this.reviews=m;
        this.layoutResourceId=layoutResourceId;
    }
    @Override
    public int getCount() {
        if(reviews.size()==0)
            return -1;
        else{
            return reviews.size();
        }
    }
    public void setAdapterData(ArrayList<Movie> reviews){
        this.reviews=reviews;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        reviewAdapter.ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new reviewAdapter.ViewHolder();
            holder.reviewTextViewauthor = (TextView) row.findViewById(R.id.author);
            holder.reviewTextViewcontent=(TextView) row.findViewById(R.id.content);
            row.setTag(holder);
        } else {
            holder = (reviewAdapter.ViewHolder) row.getTag();
        }
        Movie movie = new Movie();
                movie=reviews.get(position);
        holder.reviewTextViewauthor.setText(Html.fromHtml(movie.getReview_author()));
        holder.reviewTextViewcontent.setText(Html.fromHtml(movie.getReview_content()));
        return row;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (reviews == null || reviews.size() == 0) {
            return 0;
        }
        return reviews.get(position);
    }
    static class ViewHolder {
        TextView reviewTextViewauthor;
        TextView reviewTextViewcontent;

    }
}
