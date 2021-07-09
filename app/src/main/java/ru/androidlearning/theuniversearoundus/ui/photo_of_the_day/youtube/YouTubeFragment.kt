package ru.androidlearning.theuniversearoundus.ui.photo_of_the_day.youtube

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import ru.androidlearning.theuniversearoundus.databinding.YouTubeFragmentBinding

private const val ARG_VIDEO_ID = "VideoId"

class YouTubeFragment : Fragment() {
    private var _binding: YouTubeFragmentBinding? = null
    private val binding get() = _binding!!
    private var videoId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoId = it.getString(ARG_VIDEO_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = YouTubeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.youtubePlayerView.let {
            lifecycle.addObserver(it)
            it.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    videoId?.let { videoId -> youTubePlayer.loadVideo(videoId, 0f) }
                }
            })
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(videoId: String?) =
            YouTubeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_VIDEO_ID, videoId)
                }
            }
    }
}