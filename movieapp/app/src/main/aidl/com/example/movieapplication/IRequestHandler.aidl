// IRequestHandler.aidl
package com.example.movieapplication;

parcelable Movie;
// Declare any non-default types here with import statements

interface IRequestHandler {

    void getAllMovies(inout List<Movie> movies);
    void updateRating(in Movie movie);

}