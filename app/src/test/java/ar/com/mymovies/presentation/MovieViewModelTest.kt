package ar.com.mymovies.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import ar.com.mymovies.core.Resource
import ar.com.mymovies.data.model.MovieList
import ar.com.mymovies.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
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
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MovieViewModel
    private val mockRepo: MovieRepository = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieViewModel(mockRepo, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchMainScreenMovies emits Loading then Success on success`() = runTest {
        // mocks
        val upcoming = MovieList()
        val topRated = MovieList()
        val popular = MovieList()
        coEvery { mockRepo.getUpcomingMovies() } returns upcoming
        coEvery { mockRepo.getTopRatedMovies() } returns topRated
        coEvery { mockRepo.getPopularMovies() } returns popular

        // LiveData Observer
        val liveData = viewModel.fetchMainScreenMovies()
        val emittedValues = mutableListOf<Resource<*>>()
        liveData.observeForever { emittedValues.add(it) }

        // Coroutines running
        testDispatcher.scheduler.advanceUntilIdle()

        // results
        assertEquals(2, emittedValues.size)
        assertTrue(emittedValues[0] is Resource.Loading)
        assertTrue(emittedValues[1] is Resource.Success)
        val successData = (emittedValues[1] as Resource.Success).data
        assertEquals(Triple(upcoming, topRated, popular), successData)
    }

    @Test
    fun `fetchMainScreenMovies emits Loading then Failure on exception`() = runTest {
        // mock for exception
        val exception = Exception("Error de prueba")
        coEvery { mockRepo.getUpcomingMovies() } throws exception

        // LiveData Observer
        val liveData = viewModel.fetchMainScreenMovies()
        val emittedValues = mutableListOf<Resource<*>>()
        liveData.observeForever { emittedValues.add(it) }

        // Coroutines running
        testDispatcher.scheduler.advanceUntilIdle()

        // results
        assertEquals(2, emittedValues.size)
        assertTrue(emittedValues[0] is Resource.Loading)
        assertTrue(emittedValues[1] is Resource.Failure)
        assertEquals(exception, (emittedValues[1] as Resource.Failure).exception)
    }
}