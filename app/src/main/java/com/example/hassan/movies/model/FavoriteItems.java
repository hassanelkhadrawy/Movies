package com.example.hassan.movies.model;

public class FavoriteItems {

    public String id;
    public String original_title;
    public String Author;
    public String Content;
    public String URL;

    public FavoriteItems(String id, String original_title) {
        this.id = id;
        this.original_title = original_title;
    }

    public FavoriteItems(String author, String content, String URL) {
        Author = author;
        Content = content;
        this.URL = URL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
