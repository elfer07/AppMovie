package ar.com.mymovies.presentation

import ar.com.mymovies.core.Resource
import ar.com.mymovies.data.model.Movie
import ar.com.mymovies.data.model.MovieList
import ar.com.mymovies.repository.MovieRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

private val mockUpcomingMovieList = MovieList(
    results = listOf(
        Movie(id = 1, title = "Test Movie", overview = "Overview", poster_path = "", vote_average = 8.0)
    )
)
private val mockPopularMovieList = MovieList(
    results = listOf(
        Movie(id = 1, title = "Test Movie", overview = "Overview", poster_path = "", vote_average = 8.0)
    )
)
private val mockTopRatedMovieList = MovieList(
    results = listOf(
        Movie(id = 1, title = "Test Movie", overview = "Overview", poster_path = "", vote_average = 8.0)
    )
)

@ExperimentalCoroutinesApi
class MovieViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeMovieRepository
    private lateinit var viewModel: MovieViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMovieRepository()
        viewModel = MovieViewModel(fakeRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchMovies emits success`() = runTest {
        viewModel.fetchMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.moviesState.value
        assertTrue(state is Resource.Success)
        val data = (state as Resource.Success).data

        assertEquals(mockUpcomingMovieList, data.first)
        assertEquals(mockPopularMovieList, data.second)
        assertEquals(mockTopRatedMovieList, data.third)
    }

    @Test
    fun `fetchMovies emits failure when repository throws`() = runTest {
        fakeRepository.shouldReturnError = true
        viewModel.fetchMovies()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.moviesState.value
        assertTrue(state is Resource.Failure)
    }

    // Fake repository
    class FakeMovieRepository : MovieRepository {
        var shouldReturnError = false

        override suspend fun getUpcomingMovies(): MovieList {
            if (shouldReturnError) throw Exception("Error")
            return mockUpcomingMovieList
        }

        override suspend fun getTopRatedMovies(): MovieList {
            if (shouldReturnError) throw Exception("Error")
            return mockTopRatedMovieList
        }

        override suspend fun getPopularMovies(): MovieList {
            if (shouldReturnError) throw Exception("Error")
            return mockPopularMovieList
        }
    }
}