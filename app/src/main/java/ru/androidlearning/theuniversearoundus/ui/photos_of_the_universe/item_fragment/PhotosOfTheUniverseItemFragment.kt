package ru.androidlearning.theuniversearoundus.ui.photos_of_the_universe.item_fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.api.load
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.PhotosOfTheUniverseItemFragmentBinding
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PhotosOfTheUniverseDTO
import ru.androidlearning.theuniversearoundus.ui.utils.showSnackBar
import java.lang.IndexOutOfBoundsException

private const val ARG_PARAM_SEARCH_STRING = "ARG_PARAM_SEARCH_STRING"
private const val DEFAULT_SEARCH_STRING = "Earth photo"

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
            //Приходит список картинок, из всего списка берем первую ссылку (пока не стал заморачиваться с более сложной логикой):
            val imageUrl = responseData.collection.items[0].links[0].href
            if (imageUrl.isBlank()) {
                binding.photoOfUniverseImageView.setImageResource(R.drawable.ic_no_photo)
                showError(getString(R.string.empty_image_url))
            } else {
                binding.photoOfUniverseImageView.load(imageUrl) {
                    lifecycle(this@PhotosOfTheUniverseItemFragment)
                    error(R.drawable.ic_error_load_image)
                }
            }
            loadingLayoutIsVisible(false)
        } catch (e: IndexOutOfBoundsException) {
            showError(e.message)
        }
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