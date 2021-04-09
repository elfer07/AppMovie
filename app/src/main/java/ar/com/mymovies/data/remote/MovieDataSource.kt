package ar.com.mymovies.data.remote

import ar.com.mymovies.application.AppConstants
import ar.com.mymovies.data.model.MovieList
import ar.com.mymovies.repository.WebService

/**
 * Created by Fernando Moreno on 21/3/2021.
 */
class MovieDataSource(private val webService: WebService) {

    suspend fun getUpcomingMovies(): MovieList = webService.getUpcomingMovies(AppConstants.API_KEY)

    suspend fun getTopRatedMovies(): MovieList = webService.getTopRatedMovies(AppConstants.API_KEY)

    suspend fun getPopularMovies(): MovieList = webService.getPopularMovies(AppConstants.API_KEY)
}