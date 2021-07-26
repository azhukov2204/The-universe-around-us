package ru.androidlearning.theuniversearoundus.ui.photos_of_the_universe.item_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.*
import coil.api.load
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.PhotosOfTheUniverseItemFragmentBinding
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PhotosOfTheUniverseDTO
import ru.androidlearning.theuniversearoundus.ui.utils.showSnackBar
import kotlin.random.Random

private const val ARG_PARAM_SEARCH_STRING = "ARG_PARAM_SEARCH_STRING"
private const val DEFAULT_SEARCH_STRING = "Earth photo"
private const val ANIMATION_EXPAND_IMAGE_DURATION: Long = 500L
private const val ANIMATION_FADE_DURATION: Long = 1000L

class PhotosOfTheUniverseItemFragment : Fragment() {
    companion object {
        fun newInstance(searchString: String = DEFAULT_SEARCH_STRING) = PhotosOfTheUniverseItemFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM_SEARCH_STRING, searchString)
            }
        }
    }

    private var _binding: PhotosOfTheUniverseItemFragmentBinding? = null
    private val binding get() = _binding!!
    private val photosOfTheUniverseItemViewModel: PhotosOfTheUniverseItemViewModel by lazy { ViewModelProvider(this).get(PhotosOfTheUniverseItemViewModel::class.java) }
    private lateinit var searchString: String
    private var isExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchString = it.getString(ARG_PARAM_SEARCH_STRING, DEFAULT_SEARCH_STRING)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PhotosOfTheUniverseItemFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photosOfTheUniverseItemViewModel.run {
            photosOfTheUniverseLiveData.observe(viewLifecycleOwner) {
                renderData(it)
            }
            getPhotosOfTheUniverseFromServer(searchString)
        }
    }

    private fun renderData(dataLoadState: DataLoadState<PhotosOfTheUniverseDTO>) {
        val transitionsContainer = binding.photoOfUniverseItemLayout
        TransitionManager.beginDelayedTransition(transitionsContainer, Fade().apply { duration = ANIMATION_FADE_DURATION })
        when (dataLoadState) {
            is DataLoadState.Success -> {
                fillData(dataLoadState.responseData)
            }
            is DataLoadState.Loading -> {
                loadingLayoutIsVisible(true)
            }
            is DataLoadState.Error -> {
                showError(dataLoadState.error.message)
            }
        }
    }

    private fun fillData(responseData: PhotosOfTheUniverseDTO) {
        try {
            //Приходит список картинок, из всего списка берем рандомную ссылку :
            val randomPictureIndex = Random.nextInt(0, responseData.collection.items.size)
            val imageUrl = responseData.collection.items[randomPictureIndex].links[0].href
            if (imageUrl.isBlank()) {
                binding.photoOfUniverseImageView.setImageResource(R.drawable.ic_no_photo)
                showError(getString(R.string.empty_image_url))
            } else {
                binding.photoOfUniverseImageView.load(imageUrl) {
                    lifecycle(this@PhotosOfTheUniverseItemFragment)
                    error(R.drawable.ic_error_load_image)
                }
                binding.photoOfUniverseImageView.setOnClickListener { expandOrCollapseImage(it as ImageView) }
            }
            loadingLayoutIsVisible(false)
        } catch (e: IndexOutOfBoundsException) {
            showError(e.message)
        }
    }

    private fun expandOrCollapseImage(imageView: ImageView) {
        isExpanded = !isExpanded
        val transitionSet = TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(ChangeImageTransform())
            duration = ANIMATION_EXPAND_IMAGE_DURATION
        }
        TransitionManager.beginDelayedTransition(binding.photoOfUniverseItemLayout, transitionSet)

        val imageViewLayoutParams: ViewGroup.LayoutParams = imageView.layoutParams
        imageViewLayoutParams.height = if (isExpanded) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
        imageView.layoutParams = imageViewLayoutParams
        imageView.scaleType = if (isExpanded) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER
    }

    private fun showError(errorMessage: String?) {
        val message = "${getString(R.string.error_loading_data)}: $errorMessage"
        view?.showSnackBar(message)
        loadingLayoutIsVisible(false)
    }

    private fun loadingLayoutIsVisible(isShow: Boolean) {
        binding.includedLoadingLayout.loadingLayout.visibility =
            if (isShow) {
                View.VISIBLE
            } else (View.GONE)
    }
}
