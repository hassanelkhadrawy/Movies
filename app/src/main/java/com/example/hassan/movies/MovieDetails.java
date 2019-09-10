package com.example.hassan.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.hassan.movies.model.FavoriteItems;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetails extends AppCompatActivity {
    TextView overview, original_title, release_date, vote_average;
    ImageView movie_poster;
    ArrayList<String> tailerList = new ArrayList<>();
    ArrayList<FavoriteItems> list = new ArrayList<>();
    RecyclerView recyclerViewTailer;
    ImageView imageButton;
    boolean isFavourite;
    String movie_id, movie_poster_txt, original_title_txt,
            release_date_txt, vote_average_txt, overview_txt, TRIALER_URL;

    private String IMAGE_URL = "https://image.tmdb.org/t/p/w600_and_h900_bestv2";
    private String MAIN_URL = "https://api.themoviedb.org/3/movie/";
    private String API_KEY = "";

    private String TYPE = "videos";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        overview = findViewById(R.id.movie_overview);
        original_title = findViewById(R.id.movie_name);
        release_date = findViewById(R.id.movie_date);
        vote_average = findViewById(R.id.movie_vote_avarage);
        movie_poster = findViewById(R.id.movie_image);
        recyclerViewTailer = findViewById(R.id.recycleTailer);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageButton = findViewById(R.id.favourite_image);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        movie_id = b.getString("movie_id");
        movie_poster_txt = b.getString("movie_poster");
        overview_txt = b.getString("overview");
        original_title_txt = b.getString("original_title");
        release_date_txt = b.getString("release_date");
        vote_average_txt = b.getString("vote_average");
        check();


        TRIALER_URL = MAIN_URL + movie_id + "/" + TYPE + "?" + API_KEY + "";

        new JsonUtils().execute();

        Picasso.with(this).load(IMAGE_URL + movie_poster_txt).into(movie_poster);
        original_title.setText(original_title_txt);
        overview.append(overview_txt);
        release_date.setText(release_date_txt);
        vote_average.append(vote_average_txt);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isFavourite = readState();
                if (isFavourite) {
                    imageButton.setBackgroundResource(R.drawable.ic_star_black_24dp);
                    isFavourite = false;
                    insert();
                    Toast.makeText(MovieDetails.this, "movie saved to favorites", Toast.LENGTH_SHORT).show();
                    saveState(isFavourite);


                } else {
                    imageButton.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
                    isFavourite = true;
                    delete();
                    Toast.makeText(MovieDetails.this, "movie deleted from favourites", Toast.LENGTH_SHORT).show();
                    saveState(isFavourite);


                }

            }
        });


    }

    public void onReviewsclick(View view) {

        Intent intent = new Intent(MovieDetails.this, ReviewsActivity.class);
        intent.putExtra("Movie_ID", movie_id);
        startActivity(intent);
    }

    class JsonUtils extends AsyncTask<Void, Void, String> {
        String urlJson = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            urlJson = TRIALER_URL;

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(urlJson);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream is = new BufferedInputStream(con.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {

                    sb.append(line + "\n");

                }
                is.close();
                bufferedReader.close();
                con.disconnect();
                return sb.toString().trim();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                if (s == null) {

                    Toast.makeText(MovieDetails.this, "Please,Check Internet Connection..", Toast.LENGTH_LONG).show();
                } else {
                    loadIntoListView(s);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        private void loadIntoListView(String json) throws JSONException {
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                tailerList.add(obj.getString("key"));


            }
            TailerAdapter tailerAdapter = new TailerAdapter(MovieDetails.this, tailerList);
            recyclerViewTailer.setLayoutManager(new LinearLayoutManager(MovieDetails.this));
            recyclerViewTailer.setAdapter(tailerAdapter);


        }
    }


    void insert() {

        ContentValues values = new ContentValues();

        values.put(MoviesProvider._ID, movie_id);
        values.put(MoviesProvider.NAME, original_title_txt);

        Uri uri = getContentResolver().insert(
                MoviesProvider.CONTENT_URI, values);


    }

    public void getdata() {
        String URL = "content://com.example.hassan.movies.MoviesProvider";
        Uri movies = Uri.parse(URL);
        Cursor c = managedQuery(movies, null, null, null, "name");

        if (c.moveToFirst()) {
            do {

                String id = c.getString(c.getColumnIndex(MoviesProvider._ID));
                String name = c.getString(c.getColumnIndex(MoviesProvider.NAME));
                list.add(new FavoriteItems(id, name));

            } while (c.moveToNext());
        }

    }

    void delete() {
        int _id = Integer.parseInt(movie_id);
        getContentResolver().delete(MoviesProvider.CONTENT_URI, "_id=?", new String[]{String.valueOf(_id)});
    }

    private void saveState(boolean isFavourite) {
        SharedPreferences aSharedPreferences = this.getSharedPreferences(
                "Favourite", Context.MODE_PRIVATE);
        SharedPreferences.Editor aSharedPreferencesEdit = aSharedPreferences
                .edit();
        aSharedPreferencesEdit.putBoolean("State", isFavourite);
        aSharedPreferencesEdit.commit();
    }

    private boolean readState() {
        SharedPreferences aSharedPreferences = this.getSharedPreferences(
                "Favourite", Context.MODE_PRIVATE);
        return aSharedPreferences.getBoolean("State", true);
    }

    void check() {

        getdata();

        for (int i = 0; i < list.size(); i++) {

            if (movie_id.equals(list.get(i).id)) {
                imageButton.setBackgroundResource(R.drawable.ic_star_black_24dp);
                isFavourite = false;
                saveState(isFavourite);

                break;
            } else if (i == list.size() - 1) {
                imageButton.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
                isFavourite = true;
                saveState(isFavourite);
            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
