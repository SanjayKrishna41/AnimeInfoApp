package com.example.animeinfoapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.animeinfoapp.databinding.FragmentAnimeDetailsBinding
import com.example.animeinfoapp.ui.activities.MainActivity
import com.example.animeinfoapp.utils.Resource
import com.example.animeinfoapp.viewmodel.AnimeViewModel


class AnimeDetailsFragment : Fragment() {

    private lateinit var viewModel: AnimeViewModel
    private lateinit var binding: FragmentAnimeDetailsBinding
    private var trailerPlayer: ExoPlayer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAnimeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @UnstableApi
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get the arguments
        val animeId = arguments?.getInt("animeId")

        viewModel = (activity as MainActivity).animeViewModel

        Log.e("AnimeDetailsFragment", "$animeId")

        trailerPlayer = ExoPlayer.Builder(requireActivity()).build()
        binding.trailerView.player = trailerPlayer

        binding.trailerView.controllerHideOnTouch = true
        binding.trailerView.controllerAutoShow = true

        // fetch all the data releated to anime
        if (animeId == null) {
            // error in api call cannot be made
            Toast.makeText(requireActivity(), "error in fetching anime data", Toast.LENGTH_SHORT)
                .show()
        } else {
            animeId.let {
                viewModel.getAnimeById(animeId)
            }
        }

        // observe top animme list
        viewModel.animeDataById.observe(viewLifecycleOwner) { animeResponse ->
            when (animeResponse) {
                is Resource.Success -> {
                    hideProgressBar()
                    Log.e("Anime Data", animeResponse.data.toString())
                    animeResponse.data?.data?.let { animeData ->

                        if (animeData.trailer.url.isEmpty()) {
                            binding.trailerView.visibility = View.INVISIBLE
                            binding.ivAnimePoster.visibility = View.VISIBLE

                            Glide.with(this).load(
                                animeData.images.jpg.image_url
                            ).into(binding.ivAnimePoster)
                        } else {
                            playTrailer(animeData.trailer.url)
                        }

                        binding.tvAnimeTitle.text = "Title : ${animeData.title}"
                        binding.tvAnimeRatings.text = "Ratings : ${animeData.score}"
                        binding.tvAnimeEpisodes.text = "Number of Episodes : ${animeData.episodes}"
                        binding.tvAnimePlot.text = "Synopsis : ${animeData.synopsis}"

                        var genresString: StringBuilder? = null
                        genresString?.append("Genres : ")
                        for (gener in animeData.genres) {
                            genresString?.append("${gener.name} ")
                        }

                        binding.tvAnimeGenre.text = genresString
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    animeResponse.message?.let { message ->
                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                    }
                }

                is Resource.Loading -> {
                    Log.e("TopAnimeFragment", "Loading")
                    showProgressBar()
                }
            }
        }
    }

    private fun playTrailer(url: String) {
        val mediaItem = MediaItem.fromUri(url)

        trailerPlayer?.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    private fun hideProgressBar() {
        binding.animeDetailsProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.animeDetailsProgressBar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        trailerPlayer?.release()
        trailerPlayer = null
    }
}