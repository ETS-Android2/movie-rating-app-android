package com.example.movieapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class MovieActivity extends AppCompatActivity {
    private static final String TAG = "MovieActivity";
    private static final DecimalFormat df = new DecimalFormat("0.0");

    private ImageView ivImage;
    private TextView tvMovieName, tvReleaseDate, tvMovieDuration, tvMovieRating, tvMovieNoRates,
            tvDescription, tvActors, tvDirector, textView5;
    private RatingBar ratingBar;
    private boolean alreadyRated = false;
    private double initialRating;
    private LoadingDialog loadingDialog;

    IRequestHandler service;

    android.content.ServiceConnection connection = new android.content.ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder remoteService) {
            service = IRequestHandler.Stub.asInterface(remoteService);
            Log.d(TAG, "onServiceConnected() connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.d(TAG, "onServiceDisconnected() disconnected");
        }
    };

    public void initService() {
        Intent i = new Intent();
        i.setClassName("com.example.movieapplication", com.example.movieapplication.RequestHandler.class.getName());
        boolean ret = bindService(i, connection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "initService() bound with " + ret);
    }

    private void releaseService() {
        unbindService(connection);
        connection = null;
        Log.d(TAG, "releaseService() unbound.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        initService();
        loadingDialog = new LoadingDialog(this);

        Movie movie = getIntent().getParcelableExtra("movie");
        initialRating = movie.getMovieRating();
        Log.d("TEST", movie.toString());
        setUpWidgets();
        setUpTexts(movie);

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            loadingDialog.startLoadingAnimation();
            Handler handler = new Handler(Looper.getMainLooper());
            if (alreadyRated) {
                Toast.makeText(getApplicationContext(), "Thank You for Your Rating",
                        Toast.LENGTH_SHORT).show();
                handler.postDelayed(() -> {
                    try {
                        double newRating = ((initialRating * (movie.getMovieNumberOfRatings() - 1)
                                + rating) / movie.getMovieNumberOfRatings());
                        movie.setMovieRating(Double.parseDouble(df.format(newRating)));
                        service.updateRating(movie);
                        tvMovieRating.setText(df.format(newRating));
                    } catch (RemoteException e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong..." +
                                " Please try again!", Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismissDialog();
                }, 700);
            } else {
                Toast.makeText(getApplicationContext(), "Thank You for Your Rating",
                        Toast.LENGTH_SHORT).show();
                alreadyRated = true;
                handler.postDelayed(() -> {
                    try {
                        double newRating = ((movie.getMovieRating() * movie.getMovieNumberOfRatings())
                                + rating) / (movie.getMovieNumberOfRatings() + 1);
                        tvMovieRating.setText(df.format(newRating));
                        String ratings;
                        if (movie.getMovieNumberOfRatings() + 1 == 1) {
                            ratings = format(movie.getMovieNumberOfRatings() + 1) + " review";
                        } else {
                            ratings = format(movie.getMovieNumberOfRatings() + 1) + " reviews";
                        }
                        movie.setMovieNumberOfRatings(movie.getMovieNumberOfRatings() + 1);
                        movie.setMovieRating(Double.parseDouble(df.format(newRating)));
                        service.updateRating(movie);
                        tvMovieNoRates.setText(ratings);
                        textView5.setText("Would you like to change your rating?");
                    } catch (RemoteException e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong..." +
                                " Please try again!", Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismissDialog();
                }, 700);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseService();
    }

    private void setUpWidgets() {
        ivImage = findViewById(R.id.ivImage);
        tvMovieName = findViewById(R.id.tvMovieName);
        tvReleaseDate = findViewById(R.id.tvMovieReleaseDate);
        tvMovieDuration = findViewById(R.id.tvMovieDuration);
        tvMovieRating = findViewById(R.id.tvMovieRating);
        tvMovieNoRates = findViewById(R.id.tvMovieNoRates);
        tvDescription = findViewById(R.id.tvDescription);
        tvActors = findViewById(R.id.tvActors);
        tvDirector = findViewById(R.id.tvDirector);
        ratingBar = findViewById(R.id.myRatingBar);
        textView5 = findViewById(R.id.textView5);
    }

    private void setUpTexts(Movie movie) {
        Picasso.get().load(movie.getMovieImageUrl()).into(ivImage);
        tvMovieName.setText(movie.getMovieName());
        tvReleaseDate.setText(movie.getYearOfApparition());
        int hours = movie.getMovieDuration() / 60;
        int minutes = movie.getMovieDuration() % 60;
        String movieDuration = String.format("%dh %02dm ", hours, minutes);
        tvMovieDuration.setText(movieDuration);
        tvMovieRating.setText(String.valueOf(movie.getMovieRating()));
        String ratings;
        if (movie.getMovieNumberOfRatings() == 1) {
            ratings = format(movie.getMovieNumberOfRatings()) + " review";
        } else {
            ratings = format(movie.getMovieNumberOfRatings()) + " reviews";
        }
        tvMovieNoRates.setText(ratings);
        tvDescription.setText(movie.getMovieDescription());
        StringBuilder actors = new StringBuilder();
        for (String actor : movie.getMovieMainActors()) {
            actors.append("•").append(actor).append("  ");
        }
        tvActors.setText(actors.toString());
        tvDirector.setText(movie.getMovieDirector());
    }


    private String format(double number) {
        if(number <= 9999){
            return String.valueOf((int) number);
        }
        String[] suffix = new String[]{"", "k", "m", "b", "t"};
        int MAX_LENGTH = 4;
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }
        return r;
    }

}