package com.example.hassan.movies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hassan.movies.model.MoviesItems;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by 7aSSan on 23/6/2018.
 */

public class adabter extends RecyclerView.Adapter<adabter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;






        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.movies_img);


        }
    }




    Context context;
    ArrayList<MoviesItems> listitem;
    //Database database;

    public adabter(Context context, ArrayList<MoviesItems> list) {
        this.context = context;
        this.listitem = list;
    }

    @Override
    public adabter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycle_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final adabter.ViewHolder holder, final int position) {
     Picasso.with(context).load("https://image.tmdb.org/t/p/w600_and_h900_bestv2"+listitem.get(position).movie_poster+"").into(holder.imageView);
     holder.imageView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent i=new Intent(context,MovieDetails.class);
             i.putExtra("movie_id",listitem.get(position).id);
             i.putExtra("movie_poster",listitem.get(position).movie_poster);
             i.putExtra("overview",listitem.get(position).overview);
             i.putExtra("original_title",listitem.get(position).original_title);
             i.putExtra("release_date",listitem.get(position).release_date);
             i.putExtra("vote_average",listitem.get(position).vote_average);



             context.startActivity(i);

         }
     });
    }


    @Override
    public int getItemCount() {

        return listitem.size();
    }



}
