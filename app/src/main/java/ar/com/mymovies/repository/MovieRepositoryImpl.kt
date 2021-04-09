package ar.com.mymovies.repository

import ar.com.mymovies.data.model.MovieList
import ar.com.mymovies.data.remote.MovieDataSource

/**
 * Created by Fernando Moreno on 21/3/2021.
 */
class MovieRepositoryImpl(private val dataSource: MovieDataSource) : MovieRepository {

    override suspend fun getUpcomingMovies(): MovieList = dataSource.getUpcomingMovies()

    override suspend fun getTopRatedMovies(): MovieList = dataSource.getTopRatedMovies()

    override suspend fun getPopularMovies(): MovieList = dataSource.getPopularMovies()
}