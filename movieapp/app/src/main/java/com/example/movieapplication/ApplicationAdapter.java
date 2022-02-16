package com.example.movieapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private Context context;
    private List<Movie> movies;

    public ApplicationAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView movieName;
        private TextView yearOfApparition;
        private ImageView ivImage;
        private Movie movie;
        private Context context;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            movieName = (TextView) itemView.findViewById(R.id.tvMovieName);
            yearOfApparition = (TextView) itemView.findViewById(R.id.tvYear);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(this.context, com.example.movieapplication.MovieActivity.class);
            i.putExtra("movie", movie);
            this.context.startActivity(i);
        }

        void bind(Movie movie) {
            movieName.setText(movie.getMovieName());
            yearOfApparition.setText(movie.getYearOfApparition());
            Picasso.get().load(movie.getMovieImageUrl()).into(ivImage);
            this.movie = movie;
        }
    }

    @NonNull
    @Override
    public ApplicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view, this.context);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationAdapter.ViewHolder holder, int position) {

        Movie movie = movies.get(position);
        holder.bind(movie);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

}
