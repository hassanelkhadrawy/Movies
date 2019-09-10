package com.example.hassan.movies.model;

public class MoviesItems {
    public String id;
    public String movie_poster;
    public String overview;
    public String original_title;
    public String release_date;
    public String vote_average;


    public MoviesItems(String id, String movie_poster, String overview, String original_title, String release_date, String vote_average) {
        this.id = id;
        this.movie_poster = movie_poster;
        this.overview = overview;
        this.original_title = original_title;
        this.release_date = release_date;
        this.vote_average = vote_average;
    }


}
