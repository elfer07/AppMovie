package ar.com.mymovies.ui.moviedetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ar.com.mymovies.R
import ar.com.mymovies.databinding.FragmentMovieDetailBinding
import com.bumptech.glide.Glide

class MovieDetailFragment : Fragment(R.layout.fragment_movie_detail) {

    private lateinit var binding: FragmentMovieDetailBinding

    private val args by navArgs<MovieDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMovieDetailBinding.bind(view)

        Glide.with(requireContext()).load("https://image.tmdb.org/t/p/w500/${args.posterImageUrl}").centerCrop().into(binding.imgMovie)
        Glide.with(requireContext()).load("https://image.tmdb.org/t/p/w500/${args.backgroundImageUrl}").centerCrop().into(binding.imgBackground)
        binding.tvDescription.text = args.overview
        binding.tvTitle.text = args.title
        binding.tvLanguage.text = "Language: [${args.language}]"
        binding.tvRating.text = "${args.voteAverage} (${args.voteCount} Reviews)"
        binding.tvReleased.text = "Release: ${args.releaseDate}"
    }
}