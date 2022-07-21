package com.prokhach.mymovies.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoviesDao {
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM movies")
    LiveData<List<FavouriteMovie>> getAllFavouriteMovies();

    @Query("SELECT * FROM movies WHERE id == :movieId")
    Movie getMovieId(int movieId);

    @Query("SELECT * FROM favourite_movies WHERE id == :movieId")
    FavouriteMovie getFavouriteMovieId(int movieId);

    @Query("DELETE FROM movies")
    void deleteMovies();

    @Insert
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);

    @Insert
    void insertFavouriteMovie(FavouriteMovie favouriteMovie);

    @Delete
    void deleteFavouriteMovie(FavouriteMovie favouriteMovie);
}
