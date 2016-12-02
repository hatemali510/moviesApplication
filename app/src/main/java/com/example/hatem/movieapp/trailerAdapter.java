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
 * Created by hatem on 21/11/2016.
 */
public class trailerAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> trailers;
    private int layoutResourceId;
    trailerAdapter(Context context, ArrayList<Movie> m, int layoutResourceId){
        this.mContext=context;
        this.trailers=m;
        this.layoutResourceId=layoutResourceId;
    }
    @Override
    public int getCount() {
        if(trailers.size()==0)
        return -1;
        else{
            return trailers.size();
        }
    }
    public void setAdapterData(ArrayList<Movie> trailers){
        this.trailers=trailers;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        trailerAdapter.ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new trailerAdapter.ViewHolder();
            holder.trailerTextView = (TextView) row.findViewById(R.id.textView);
            row.setTag(holder);
        } else {
            holder = (trailerAdapter.ViewHolder) row.getTag();
        }

        Movie movie =new Movie();
              movie=  trailers.get(position);
        holder.trailerTextView.setText(Html.fromHtml(movie.getTrailer_name()));
        return row;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (trailers == null || trailers.size() == 0) {
            return 0;
        }
        return trailers.get(position);
    }
    static class ViewHolder {
        TextView trailerTextView;

    }
}
