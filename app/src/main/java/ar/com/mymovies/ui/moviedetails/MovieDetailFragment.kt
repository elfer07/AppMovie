package ar.com.mymovies.ui.moviedetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ar.com.mymovies.R
import ar.com.mymovies.application.AppConstants.IMAGE_BASE_URL
import ar.com.mymovies.databinding.FragmentMovieDetailBinding
import coil.compose.AsyncImage

class MovieDetailFragment : Fragment(R.layout.fragment_movie_detail) {

    private lateinit var binding: FragmentMovieDetailBinding

    private val args by navArgs<MovieDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) { }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding = FragmentMovieDetailBinding.bind(view)

        binding.composeViewDetail.setContent {
            MovieDetailScreen(
                posterUrl = args.posterImageUrl,
                backgroundUrl = args.backgroundImageUrl,
                title = args.title,
                overview = args.overview,
                language = args.language,
                voteAverage = args.voteAverage,
                voteCount = args.voteCount,
                releaseDate = args.releaseDate
            )
        }
    }
}

@Composable
fun MovieDetailScreen(
    posterUrl: String,
    backgroundUrl: String,
    title: String,
    overview: String,
    language: String,
    voteAverage: Float,
    voteCount: Int,
    releaseDate: String
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Background image
            AsyncImage(
                model = "${IMAGE_BASE_URL}$backgroundUrl",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
            )

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    ActionItem(icon = Icons.Outlined.BookmarkBorder, label = "Añadir")
                    ActionItem(icon = Icons.Outlined.FavoriteBorder, label = "Favorito")
                    ActionItem(icon = Icons.Outlined.Share, label = "Compartir")
                }
            }
        }

        // Gradient overlay
        Box(
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent),
                        startY = 0f,
                        endY = 500f
                    )
                )
        )

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 60.dp, end = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            IconText(
                icon = Icons.Outlined.StarBorder,
                text = "$voteAverage ($voteCount Reviews)"
            )
            Spacer(modifier = Modifier.height(4.dp))
            IconText(
                icon = Icons.Outlined.CalendarToday,
                text = "Release: $releaseDate"
            )
            Spacer(modifier = Modifier.height(4.dp))
            IconText(
                icon = Icons.Outlined.Language,
                text = "Language: [$language]"
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(200.dp))

            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Poster
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .size(width = 120.dp, height = 170.dp)
                ) {
                    AsyncImage(
                        model = "${IMAGE_BASE_URL}$posterUrl",
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Resumen",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )

            Text(
                text = overview,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            )
        }
    }
}

@Composable
fun IconText(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(icon, contentDescription = null, colorFilter = ColorFilter.tint(Color.White))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = Color.White)
    }
}

@Composable
fun ActionItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(icon, contentDescription = null)
        Text(text = label, fontSize = 12.sp)
    }
}
