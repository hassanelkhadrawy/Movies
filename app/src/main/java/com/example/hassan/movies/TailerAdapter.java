package com.example.hassan.movies;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TailerAdapter extends RecyclerView.Adapter<TailerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView trailerShow_btn;

        public ViewHolder(View itemView) {
            super(itemView);
            trailerShow_btn = itemView.findViewById(R.id.trailer_btn);
        }


    }

    Context context;
    ArrayList<String> keyList;
    private String YoutubeUrl="https://www.youtube.com/watch?v=";
    private String URL;

    public TailerAdapter(Context context, ArrayList<String> keyList) {
        this.context = context;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.tailer_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.trailerShow_btn.append(" "+position);
        holder.trailerShow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL = YoutubeUrl + keyList.get(position);
                onYoutubeClicked(URL);


            }
        });


    }

    @Override
    public int getItemCount() {
        return keyList.size();
    }

    private void onYoutubeClicked(String TrailerUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {

            context.getPackageManager().getPackageInfo("com.google.android.youtube", 0);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse(TrailerUrl));
        } catch (PackageManager.NameNotFoundException e) {
            intent.setData(Uri.parse(TrailerUrl));
        } finally {
            context.startActivity(intent);
        }
    }


}
