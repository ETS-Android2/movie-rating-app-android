package com.example.movieapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Movie implements Parcelable {

    // declare the movie class fields
    private int id;
    private String dbId;
    private String movieName;
    private String yearOfApparition;
    private String movieImageUrl;
    private int movieDuration;
    private String movieDirector;
    private String[] movieMainActors;
    private String movieDescription;
    private double movieRating;
    private int movieNumberOfRatings;


    public Movie() {}

    public Movie(String dbId, String movieName, String yearOfApparition, String movieImageUrl, int movieDuration,
                 String movieDirector, String[] movieMainActors, String movieDescription,
                 double movieRating, int movieNumberOfRatings) {
        this.dbId = dbId;
        this.movieName = movieName;
        this.yearOfApparition = yearOfApparition;
        this.movieImageUrl = movieImageUrl;
        this.movieDuration = movieDuration;
        this.movieDirector = movieDirector;
        this.movieMainActors = movieMainActors;
        this.movieDescription = movieDescription;
        this.movieRating = movieRating;
        this.movieNumberOfRatings = movieNumberOfRatings;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        dbId = in.readString();
        movieName = in.readString();
        yearOfApparition = in.readString();
        movieImageUrl = in.readString();
        movieDuration = in.readInt();
        movieDirector = in.readString();
        movieMainActors = in.createStringArray();
        movieDescription = in.readString();
        movieRating = in.readDouble();
        movieNumberOfRatings = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getYearOfApparition() {
        return yearOfApparition;
    }

    public void setYearOfApparition(String yearOfApparition) {
        this.yearOfApparition = yearOfApparition;
    }

    public String getMovieImageUrl() {
        return movieImageUrl;
    }

    public void setMovieImageUrl(String movieImageUrl) {
        this.movieImageUrl = movieImageUrl;
    }

    public int getMovieDuration() {
        return movieDuration;
    }

    public void setMovieDuration(int movieDuration) {
        this.movieDuration = movieDuration;
    }

    public String getMovieDirector() {
        return movieDirector;
    }

    public void setMovieDirector(String movieDirector) {
        this.movieDirector = movieDirector;
    }

    public String[] getMovieMainActors() {
        return movieMainActors;
    }

    public void setMovieMainActors(String[] movieMainActors) {
        this.movieMainActors = movieMainActors;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public double getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(double movieRating) {
        this.movieRating = movieRating;
    }

    public int getMovieNumberOfRatings() {
        return movieNumberOfRatings;
    }

    public void setMovieNumberOfRatings(int movieNumberOfRatings) {
        this.movieNumberOfRatings = movieNumberOfRatings;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", dbId='" + dbId + '\'' +
                ", movieName='" + movieName + '\'' +
                ", yearOfApparition='" + yearOfApparition + '\'' +
                ", movieImageUrl='" + movieImageUrl + '\'' +
                ", movieDuration=" + movieDuration +
                ", movieDirector='" + movieDirector + '\'' +
                ", movieMainActors=" + Arrays.toString(movieMainActors) +
                ", movieDescription='" + movieDescription + '\'' +
                ", movieRating=" + movieRating +
                ", movieNumberOfRatings=" + movieNumberOfRatings +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(dbId);
        dest.writeString(movieName);
        dest.writeString(yearOfApparition);
        dest.writeString(movieImageUrl);
        dest.writeInt(movieDuration);
        dest.writeString(movieDirector);
        dest.writeStringArray(movieMainActors);
        dest.writeString(movieDescription);
        dest.writeDouble(movieRating);
        dest.writeInt(movieNumberOfRatings);
    }
}
