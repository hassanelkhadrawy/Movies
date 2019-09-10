package com.example.hassan.movies;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.hassan.movies.model.FavoriteItems;
import com.example.hassan.movies.model.MoviesItems;

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

public class ReviewsActivity extends AppCompatActivity {
    RecyclerView recyc_Views;
    ArrayList<FavoriteItems> ReviewsList = new ArrayList<>();
    String REVIWS_Author, REVIWS_Content, REVIWS_URL, MainReviwsURL, movie_id;
    private String MAIN_URL = "https://api.themoviedb.org/3/movie/";
    private String API_KEY = "";
    private String TYPE = "reviews";

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        recyc_Views = findViewById(R.id.recycle_Views);
        progressDialog = new ProgressDialog(this, R.style.AppTheme);
        savedInstanceState = getIntent().getExtras();
        movie_id = savedInstanceState.getString("Movie_ID");

        MainReviwsURL = MAIN_URL + movie_id + "/" + TYPE + "?" + API_KEY + "";
        new JsonUtils().execute();

    }


    class JsonUtils extends AsyncTask<Void, Void, String> {
        String urlJson = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            urlJson = MainReviwsURL;
            progressDialog.setMessage("Loading...");
            progressDialog.show();

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
                    progressDialog.dismiss();
                    Toast.makeText(ReviewsActivity.this, "Please,Check Internet Connection..", Toast.LENGTH_LONG).show();
                } else {
                    loadIntoListView(s);
                    progressDialog.dismiss();


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
                REVIWS_Author = obj.getString("author");
                REVIWS_Content = obj.getString("content");
                REVIWS_URL = obj.getString("url");
                ReviewsList.add(new FavoriteItems(REVIWS_Author, REVIWS_Content, REVIWS_URL));


            }

            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(ReviewsActivity.this, ReviewsList);
            recyc_Views.setLayoutManager(new LinearLayoutManager(ReviewsActivity.this));
            recyc_Views.setAdapter(reviewsAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
