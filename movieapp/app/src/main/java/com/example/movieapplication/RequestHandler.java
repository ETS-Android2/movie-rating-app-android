package com.example.movieapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class RequestHandler extends Service {
    private static final String TAG = "RequestHandler";
    private IRequestHandlerImpl myBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        this.myBinder = new IRequestHandlerImpl(this);
        Log.d(TAG, "Inside onCreate() method");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Inside onDestroy() method");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    private class IRequestHandlerImpl extends IRequestHandler.Stub {
        private final String url = "http://10.0.2.2:3000/movies";
        private final Context context;

        public IRequestHandlerImpl(Context context) {
            this.context = context;
        }

        @Override
        public void getAllMovies(List<Movie> movies) {
            RequestQueue queue = Volley.newRequestQueue(context);
            movies.clear();
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                Movie movie = jsonToMovie(obj);
                                movie.setId(i+1);
                                //Log.d(TAG, "Movie: " + movie.toString());
                                movies.add(movie);
                            } catch (JSONException e) {
                                Log.d(TAG, "Inside getAllMovies() exception: " + e.toString());
                            }
                        }
                    }, error -> Log.d(TAG, "Something went wrong: " + Arrays.toString(error.getStackTrace())));
            queue.add(request);
            Log.d(TAG, "Movies: " + movies.toString());
        }

        @Override
        public void updateRating(Movie movie) {
            RequestQueue queue = Volley.newRequestQueue(context);
            String newUrl = String.format(url + "/%s", movie.getDbId());
            JSONObject json = new JSONObject();
            try {
                json.put("movieUserRating", String.valueOf(movie.getMovieRating()));
                json.put("movieNumberOfRatings", String.valueOf(movie.getMovieNumberOfRatings()));
            } catch (JSONException e){
                Log.d(TAG, e.toString());
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, newUrl, json, response -> {
                Log.d(TAG, response.toString());
            }, error -> {
                Log.d(TAG, "Something went wrong: " + error.toString());
            });
            queue.add(request);
        }
    }

    private Movie jsonToMovie(JSONObject obj) {
        Movie movie = null;
        try {
            String dbId = obj.getString("_id");
            String movieName = obj.getString("movieName");
            String yearOfAparition = obj.getString("yearOfApparition");
            String movieImageUrl = obj.getString("movieImageUrl");
            int movieDuration = obj.getInt("movieDuration");
            String movieDirector = obj.getString("movieDirector");
            String[] movieMainActors = new String[5];
            JSONArray actors = obj.getJSONArray("movieMainActors");
            for(int i = 0; i < actors.length(); i++) {
                movieMainActors[i] = actors.get(i).toString();
            }
            String movieDescription = obj.getString("movieDescription");
            JSONObject rating = obj.getJSONObject("movieUserRating");
            double movieUserRating = rating.getDouble("$numberDecimal");
            int movieNumberOfRatings = obj.getInt("movieNumberOfRatings");
            movie = new Movie(dbId, movieName, yearOfAparition, movieImageUrl, movieDuration, movieDirector,
                    movieMainActors, movieDescription, movieUserRating, movieNumberOfRatings);
        } catch (JSONException e) {
            Log.d(TAG, "Inside jsonToMovie() exception: " + e.toString());
        }
        return movie;
    }
}
