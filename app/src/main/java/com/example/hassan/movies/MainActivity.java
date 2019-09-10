package com.example.hassan.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;

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

public class MainActivity extends AppCompatActivity {
    RecyclerView movies_recyclerView;
    ArrayList<MoviesItems> moviesItems = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> arryCheck = new ArrayList<>();
    String movie_id, movie_image, movie_overview, movie_vote, movie_date, movie_title, url;
    SwipeRefreshLayout refreshLayout;
    RelativeLayout relativeLayout;
    private String MAIN_URL = "https://api.themoviedb.org/3/movie/";
    private String API_KEY = "";
    String state;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPrefrance;
    adabter a;
    LinearLayoutManager llm;
    private Parcelable listState;
    String LIST_STATE_KEY = "list__state_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movies_recyclerView = findViewById(R.id.recycle_view);
        relativeLayout = findViewById(R.id.relative);

        refreshLayout = findViewById(R.id.refresh_layout);
        sharedPrefrance = getSharedPreferences("state", Context.MODE_PRIVATE);
        editor = sharedPrefrance.edit();
        moviesItems.clear();
        list.clear();
        arryCheck.clear();
        movies_recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        movies_recyclerView.setLayoutManager(llm);
//        listState=savedInstanceState.getParcelable("ListState");

        //onRestoreInstanceState();


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            moviesItems.clear();
                            new JsonUtils().execute();


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        moviesItems.clear();
        list.clear();
        onRestoreData();

    }

    class JsonUtils extends AsyncTask<Void, Void, String> {
        String urlJson = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            urlJson = url;

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

                    Toast.makeText(MainActivity.this, "Please,Check Internet Connection..", Toast.LENGTH_LONG).show();
                    refreshLayout.setRefreshing(false);
                } else if (arryCheck.contains("NotFavourite")) {

                    loadIntoListView(s);
                    relativeLayout.setBackgroundResource(0);
                    refreshLayout.setRefreshing(false);


                } else {
                    loadFavouriteListView(s);
                    relativeLayout.setBackgroundResource(0);
                    refreshLayout.setRefreshing(false);
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
                movie_id = obj.getString("id");
                movie_image = obj.getString("poster_path");
                movie_overview = obj.getString("overview");
                movie_title = obj.getString("original_title");
                movie_date = obj.getString("release_date");
                movie_vote = obj.getString("vote_average");
                moviesItems.add(new MoviesItems(movie_id, movie_image, movie_overview, movie_title, movie_date, movie_vote));

            }
            Log.d("test_id", movie_id);

            a = new adabter(MainActivity.this, moviesItems);
            movies_recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
            movies_recyclerView.setAdapter(a);
            movies_recyclerView.getLayoutManager().onRestoreInstanceState(listState);

        }

        private void loadFavouriteListView(String json) throws JSONException {
            JSONObject object = new JSONObject(json);

            movie_id = object.getString("id");
            movie_image = object.getString("poster_path");

            movie_overview = object.getString("overview");
            movie_title = object.getString("original_title");
            movie_date = object.getString("release_date");
            movie_vote = object.getString("vote_average");

            moviesItems.add(new MoviesItems(movie_id, movie_image, movie_overview, movie_title, movie_date, movie_vote));

            a = new adabter(MainActivity.this, moviesItems);
            movies_recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
            movies_recyclerView.setAdapter(a);
            movies_recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.popular) {


            state = "popular";
            editor.putString("state", "popular");
            editor.apply();
            moviesItems.clear();
            arryCheck.add("NotFavourite");
            url = MAIN_URL + "popular?" + API_KEY + "&language=en-US&page=1";

            new JsonUtils().execute();
        } else if (id == R.id.top) {
            state = "toprated";
            editor.putString("state", "toprated");
            editor.apply();
            moviesItems.clear();
            arryCheck.add("NotFavourite");
            url = MAIN_URL + "top_rated?" + API_KEY + "&language=en-US&page=1";
            new JsonUtils().execute();
        } else if (id == R.id.favourites) {

            state = "favourites";
            editor.putString("state", "favourites");
            editor.apply();
            getFavouriteMovies();

        }

        return super.onOptionsItemSelected(item);
    }


    protected void onRestoreData() {
        String checkState = sharedPrefrance.getString("state", "no data");
        if (checkState.equals("popular")) {
            Toast.makeText(this, checkState, Toast.LENGTH_SHORT).show();
            moviesItems.clear();
            arryCheck.add("NotFavourite");
            url = MAIN_URL + "popular?" + API_KEY + "&language=en-US&page=1";
            new JsonUtils().execute();

        } else if (checkState.equals("toprated")) {
            Toast.makeText(this, checkState, Toast.LENGTH_SHORT).show();

            moviesItems.clear();
            arryCheck.add("NotFavourite");
            url = MAIN_URL + "top_rated?" + API_KEY + "&language=en-US&page=1";
            new JsonUtils().execute();
        } else if (checkState.equals("favourites")) {
            Toast.makeText(this, checkState, Toast.LENGTH_SHORT).show();

            getFavouriteMovies();


        } else {

            arryCheck.add("NotFavourite");
            url = MAIN_URL + "popular?" + API_KEY + "&language=en-US&page=1";
            new JsonUtils().execute();
        }
    }

    public void getdata() {
        list.clear();
        String URL = "content://com.example.hassan.movies.MoviesProvider";
        Uri movies = Uri.parse(URL);
        Cursor c = managedQuery(movies, null, null, null, "name");

        if (c.moveToFirst()) {
            do {

                String id = c.getString(c.getColumnIndex(MoviesProvider._ID));
                list.add(id);

            } while (c.moveToNext());
        }

    }

    private void getFavouriteMovies() {
        list.clear();
        getdata();
        moviesItems.clear();
        arryCheck.clear();


        if (list.size() - 0 == 0) {

            arryCheck.add("NotFavourite");
            url = MAIN_URL + "popular?" + API_KEY + "&language=en-US&page=1";
            new JsonUtils().execute();
        } else {
            for (int i = 0; i < list.size(); i++) {

                url = MAIN_URL + "" + list.get(i) + "?" + API_KEY;
                new JsonUtils().execute();
            }
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editor.putString("state", "");
        editor.apply();
        finish();
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
        listState = llm.onSaveInstanceState();
        state.putParcelable(LIST_STATE_KEY, listState);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions

        if (state != null)
            listState = state.getParcelable(LIST_STATE_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (listState != null) {

            llm.onRestoreInstanceState(listState);
        }
    }
}
