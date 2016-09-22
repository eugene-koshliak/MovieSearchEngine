package com.example.eugene.moviesearchengine;

import android.graphics.Bitmap;

/**
 * Created by eugene on 13.09.16.
 */

public class Movies {

    private String title;
    private String year;
    private Bitmap poster;
    private String director;
    private String writer;
    private String[] actors;
    private String country;
    private String imdbRating;
    private String type;
    private String imdbVotes;

    public Movies(Bitmap poster, String title, String type, String year) {
        this.poster = poster;
        this.title = title;
        this.type = type;
        this.year = year;
    }

    public Movies(String title, String year, Bitmap poster, String director, String writer,
                  String[] actors, String country, String imdbRating, String type, String imdbVotes) {
        this.title = title;
        this.year = year;
        this.poster = poster;
        this.director = director;
        this.writer = writer;
        this.actors = actors;
        this.country = country;
        this.imdbRating = imdbRating;
        this.type = type;
        this.imdbVotes = imdbVotes;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public String getDirector() {
        return director;
    }

    public String getWriter() {
        return writer;
    }

    public String[] getActors() {
        return actors;
    }

    public String getCountry() {
        return country;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public String getType() {
        return type;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }
}
