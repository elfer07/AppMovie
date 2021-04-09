package ar.com.mymovies.repository

import ar.com.mymovies.data.model.MovieList

/**
 * Created by Fernando Moreno on 21/3/2021.
 */
interface MovieRepository {
    suspend fun getUpcomingMovies(): MovieList
    suspend fun getTopRatedMovies(): MovieList
    suspend fun getPopularMovies(): MovieList
}