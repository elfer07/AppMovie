package ar.com.mymovies.ui.movie

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import ar.com.mymovies.R
import ar.com.mymovies.application.AppConstants.IMAGE_BASE_URL
import ar.com.mymovies.core.Resource
import ar.com.mymovies.data.model.Movie
import ar.com.mymovies.data.model.MovieList
import ar.com.mymovies.data.remote.MovieDataSource
import ar.com.mymovies.databinding.FragmentMovieBinding
import ar.com.mymovies.presentation.MovieViewModel
import ar.com.mymovies.presentation.MovieViewModelFactory
import ar.com.mymovies.repository.MovieRepositoryImpl
import ar.com.mymovies.repository.RetrofitClient
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

class MovieFragment : Fragment(R.layout.fragment_movie) {

    private lateinit var binding: FragmentMovieBinding

    private val viewModel by viewModels<MovieViewModel> {
        MovieViewModelFactory(
            MovieRepositoryImpl(
                MovieDataSource(RetrofitClient.webservice)
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMovieBinding.bind(view)

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moviesState
                    .collect {
                        when (it) {
                            is Resource.Loading -> {
                                Log.d("flow", "Loading...")
                                binding.composeView.setContent {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                            is Resource.Success -> {
                                binding.composeView.setContent {
                                    MoviesList(it.data, findNavController())
                                }

                            }
                            is Resource.Failure -> {
                                Log.d("Error", "${it.exception}")
                            }
                        }
                    }
            }
        }
    }
}

@Composable
fun MoviesList(
    movies: Triple<MovieList, MovieList, MovieList>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        MovieCategories(
            upcoming = movies.first.results,
            topRated = movies.second.results,
            popular = movies.third.results,
            onMovieClick = { movie ->
                val action = MovieFragmentDirections.actionMovieFragmentToMovieDetailFragment(
                    movie.poster_path,
                    movie.backdrop_path,
                    movie.vote_average.toFloat(),
                    movie.vote_count,
                    movie.overview,
                    movie.title,
                    movie.original_language,
                    movie.release_date
                )
                navController.navigate(action)
            }
        )
    }
}

@Composable
private fun MovieCategories(
    upcoming: List<Movie>,
    topRated: List<Movie>,
    popular: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { CategorySection("Upcoming Movies", upcoming, onMovieClick) }
        item { CategorySection("Top Rated Movies", topRated, onMovieClick) }
        item { CategorySection("Popular Movies", popular, onMovieClick) }
    }
}

@Composable
private fun CategorySection(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(movies) { movie ->
                MovieItem(movie, onMovieClick)
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onMovieClick: (Movie) -> Unit) {
    Card(
        modifier = Modifier
            .size(100.dp, 150.dp)
            .clickable { onMovieClick(movie) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = "${IMAGE_BASE_URL}${movie.poster_path}",
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.error_image)
            ),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}