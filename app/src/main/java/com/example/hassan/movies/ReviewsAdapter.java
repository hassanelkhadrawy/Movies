package com.example.hassan.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hassan.movies.model.FavoriteItems;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.AdapterClass> {


    public class AdapterClass extends RecyclerView.ViewHolder {
        TextView Author, Content, URL;

        public AdapterClass(View itemView) {
            super(itemView);
            Author = itemView.findViewById(R.id.author);
            Content = itemView.findViewById(R.id.content);
            URL = itemView.findViewById(R.id.url);
        }
    }

    Context context;
    ArrayList<FavoriteItems> ReviewsList;

    public ReviewsAdapter(Context context, ArrayList<FavoriteItems> reviewsList) {
        this.context = context;
        ReviewsList = reviewsList;
    }

    @NonNull
    @Override
    public AdapterClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reviews_item, null);

        return new AdapterClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClass holder, int position) {
        holder.Author.append(ReviewsList.get(position).Author);
        holder.Content.append(ReviewsList.get(position).Content);
        holder.URL.append(ReviewsList.get(position).URL);

    }

    @Override
    public int getItemCount() {
        return ReviewsList.size();
    }


}
