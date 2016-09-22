package com.example.eugene.moviesearchengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by eugene on 13.09.16.
 */

public class MoviesAdapter extends ArrayAdapter<Movies> {

    public MoviesAdapter(Context context, List<Movies> movies) {
        super(context, 0, movies);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.movies_list_item, parent, false);
        }

        Movies currentMovie = getItem(position);

        ImageView moviePosterView = (ImageView) listItemView.findViewById(R.id.movie_poster);
        Bitmap moviePoster = currentMovie.getPoster();
        moviePosterView.setImageBitmap(moviePoster);


        TextView movieTitleView = (TextView) listItemView.findViewById(R.id.movie_title);
        String movieTitle = currentMovie.getTitle();
        movieTitleView.setText(movieTitle);

        TextView movieTypeView = (TextView) listItemView.findViewById(R.id.movie_type);
        String movieType = currentMovie.getType();
        movieTypeView.setText(movieType);

        TextView movieYearView = (TextView) listItemView.findViewById(R.id.movie_year);
        String movieYear = currentMovie.getYear();
        movieYearView.setText(movieYear);

        return listItemView;
    }
}
