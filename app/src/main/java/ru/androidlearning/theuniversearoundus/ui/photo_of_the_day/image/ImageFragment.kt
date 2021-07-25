package ru.androidlearning.theuniversearoundus.ui.photo_of_the_day.image

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.api.load
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.ImageFragmentBinding
import ru.androidlearning.theuniversearoundus.ui.utils.showSnackBar

class ImageFragment : Fragment() {
    private var _binding: ImageFragmentBinding? = null
    private val binding get() = _binding!!
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString(ARG_IMAGE_URL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ImageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (imageUrl.isNullOrBlank()) {
            binding.photoOfTheDayImageView.setImageResource(R.drawable.ic_no_photo)
            showError(getString(R.string.empty_image_url))
        } else {
            binding.photoOfTheDayImageView.load(imageUrl) {
                lifecycle(this@ImageFragment)
                error(R.drawable.ic_error_load_image)
            }
        }
    }

    private fun showError(errorMessage: String?) {
        val message = "${getString(R.string.error_loading_data)}: $errorMessage"
        view?.showSnackBar(message)
    }

    companion object {
        private const val ARG_IMAGE_URL = "ImageURL"

        @JvmStatic
        fun newInstance(imageUrl: String?) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE_URL, imageUrl)
                }
            }
    }
}