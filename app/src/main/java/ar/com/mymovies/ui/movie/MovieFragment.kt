package ar.com.mymovies.ui.movie

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import ar.com.mymovies.R
import ar.com.mymovies.core.Resource
import ar.com.mymovies.data.model.Movie
import ar.com.mymovies.data.remote.MovieDataSource
import ar.com.mymovies.databinding.FragmentMovieBinding
import ar.com.mymovies.presentation.MovieViewModel
import ar.com.mymovies.presentation.MovieViewModelFactory
import ar.com.mymovies.repository.MovieRepositoryImpl
import ar.com.mymovies.repository.RetrofitClient
import ar.com.mymovies.ui.movie.adapters.MovieAdapter
import ar.com.mymovies.ui.movie.adapters.concat.PopularConcatAdapter
import ar.com.mymovies.ui.movie.adapters.concat.TopRatedConcatAdapter
import ar.com.mymovies.ui.movie.adapters.concat.UpcomingConcatAdapter

class MovieFragment : Fragment(R.layout.fragment_movie), MovieAdapter.OnMovieClickListener {

    private lateinit var binding: FragmentMovieBinding

    private val viewModel by viewModels<MovieViewModel> {
        MovieViewModelFactory(
            MovieRepositoryImpl(
                MovieDataSource(RetrofitClient.webservice)
            )
        )
    }

    private lateinit var concatAdapter: ConcatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        concatAdapter = ConcatAdapter()

        binding = FragmentMovieBinding.bind(view)

        viewModel.fetchMainScreenMovies().observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("LiveData", "Loading...")
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {

                    binding.progressBar.visibility = View.GONE
                    concatAdapter.apply {
                        addAdapter(
                            0,
                            UpcomingConcatAdapter(
                                MovieAdapter(
                                    it.data.first.results,
                                    this@MovieFragment
                                )
                            )
                        )
                        addAdapter(
                            1,
                            TopRatedConcatAdapter(
                                MovieAdapter(
                                    it.data.second.results,
                                    this@MovieFragment
                                )
                            )
                        )
                        addAdapter(
                            2,
                            PopularConcatAdapter(
                                MovieAdapter(
                                    it.data.third.results,
                                    this@MovieFragment
                                )
                            )
                        )
                    }

                    binding.rvMovies.adapter = concatAdapter

                }
                is Resource.Failure -> {
                    Log.d("Error", "${it.exception}")
                    binding.progressBar.visibility = View.GONE
                }
            }
        })
    }

    override fun onMovieClick(movie: Movie) {
        Log.d("Movie", "onMovieClick: ${movie.title}")
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
        findNavController().navigate(action)
    }
}