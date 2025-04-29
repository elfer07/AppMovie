package ar.com.mymovies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ar.com.mymovies.core.Resource
import ar.com.mymovies.data.model.MovieList
import ar.com.mymovies.repository.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Fernando Moreno on 21/3/2021.
 */
class MovieViewModel(
    private val repo: MovieRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _moviesState = MutableStateFlow<Resource<Triple<MovieList, MovieList, MovieList>>>(Resource.Loading())
    val moviesState: StateFlow<Resource<Triple<MovieList, MovieList, MovieList>>> = _moviesState.asStateFlow()

    init {
        fetchMovies()
    }

    fun fetchMovies() {
        viewModelScope.launch(dispatcher) {
            _moviesState.value = Resource.Loading()
            runCatching {
                coroutineScope {
                    val upcoming = async { repo.getUpcomingMovies() }
                    val topRated = async { repo.getTopRatedMovies() }
                    val popular = async { repo.getPopularMovies() }
                    Triple(upcoming.await(), topRated.await(), popular.await())
                }
            }.fold(
                onSuccess = { (upcoming, topRated, popular) ->
                    _moviesState.value = Resource.Success(Triple(upcoming, topRated, popular))
                },
                onFailure = {
                    _moviesState.value = Resource.Failure(Exception(it))
                }
            )
        }
    }
}

class MovieViewModelFactory(
    private val repo: MovieRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            MovieRepository::class.java,
            CoroutineDispatcher::class.java
        ).newInstance(repo, dispatcher)
    }
}