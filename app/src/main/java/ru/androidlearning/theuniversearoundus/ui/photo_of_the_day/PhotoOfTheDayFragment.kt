package ru.androidlearning.theuniversearoundus.ui.photo_of_the_day

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.constraintlayout.widget.ConstraintLayout
import coil.api.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.PhotoOfTheDayFragmentBinding
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO
import ru.androidlearning.theuniversearoundus.ui.MainActivity
import ru.androidlearning.theuniversearoundus.ui.choice_of_theme.ChoiceOfThemeFragment
import ru.androidlearning.theuniversearoundus.ui.utils.showSnackBar
import java.text.SimpleDateFormat
import java.util.*

private const val WIKI_BASE_URL = "https://en.wikipedia.org/wiki/"

class PhotoOfTheDayFragment : Fragment() {
    companion object {
        fun newInstance() = PhotoOfTheDayFragment()
    }

    private var _binding: PhotoOfTheDayFragmentBinding? = null
    private val photoOfTheDayFragmentBinding get() = _binding!!
    private val photoOfTheDayViewModel: PhotoOfTheDayViewModel by lazy { ViewModelProvider(this).get(PhotoOfTheDayViewModel::class.java) }
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PhotoOfTheDayFragmentBinding.inflate(inflater, container, false)
        return photoOfTheDayFragmentBinding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoOfTheDayViewModel.run {
            photoOfTheDayLiveData.observe(viewLifecycleOwner) { dataLoadState -> renderData(dataLoadState) }
            if (savedInstanceState == null) {
                getPictureOfTheDayFromServer()
            }
        }
        photoOfTheDayFragmentBinding.searchInWikiLayout.setEndIconOnClickListener {
            val searchText = photoOfTheDayFragmentBinding.searchInWikiEditText.text
            val uri = Uri.parse("${WIKI_BASE_URL}${searchText}")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        setBottomSheetBehavior(photoOfTheDayFragmentBinding.includedBottomSheetLayout.bottomSheetLayout)
        setBottomAppBar()
        initChips()
    }

    private fun initChips() {
        //пока вычисляю даты довольно хардконо:
        val calendar = Calendar.getInstance()
        val currentDateString = simpleDateFormat.format(calendar.time)
        val oneDayAgoString = simpleDateFormat.format(calendar.apply { add(Calendar.DATE, -1) }.time)
        val twoDayAgoString = simpleDateFormat.format(calendar.apply { add(Calendar.DATE, -1) }.time)

        photoOfTheDayFragmentBinding.apply {
            oneDaysAgoChip.text = oneDayAgoString
            twoDaysAgoChip.text = twoDayAgoString
            todayChip.text = currentDateString
        }

        photoOfTheDayFragmentBinding.chosePhotoChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.two_days_ago_chip -> {
                    photoOfTheDayViewModel.getPictureOfTheDayFromServer(twoDayAgoString)
                }
                R.id.one_days_ago_chip -> {
                    photoOfTheDayViewModel.getPictureOfTheDayFromServer(oneDayAgoString)
                }
                R.id.today_chip -> {
                    photoOfTheDayViewModel.getPictureOfTheDayFromServer(currentDateString)
                }
            }
        }
    }

    private fun setBottomAppBar() {
        (activity as MainActivity).setSupportActionBar(photoOfTheDayFragmentBinding.bottomAppBar)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_settings -> {
                activity?.run { supportFragmentManager.beginTransaction().replace(R.id.container, ChoiceOfThemeFragment()).addToBackStack(null).commit() }
            }
            android.R.id.home -> {
                BottomNavigationDrawerFragment().show(parentFragmentManager, null)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBottomSheetBehavior(bottomSheetLayout: ConstraintLayout) {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun renderData(dataLoadState: DataLoadState<PictureOfTheDayDTO>) {
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

    private fun showError(errorMessage: String?) {
        val message = "${getString(R.string.error_loading_data)}: $errorMessage"
        view?.showSnackBar(message)
        loadingLayoutIsVisible(false)
    }

    private fun loadingLayoutIsVisible(isShow: Boolean) {
        photoOfTheDayFragmentBinding.includedLoadingLayout.loadingLayout.visibility =
            if (isShow) {
                View.VISIBLE
            } else (View.GONE)
    }

    private fun fillData(responseData: PictureOfTheDayDTO) {
        val imageUrl = responseData.url
        if (imageUrl.isNullOrBlank()) {
            photoOfTheDayFragmentBinding.photoOfTheDayImageView.setImageResource(R.drawable.ic_no_photo)
            showError(getString(R.string.empty_image_url))
        } else {
            photoOfTheDayFragmentBinding.photoOfTheDayImageView.load(imageUrl) {
                lifecycle(this@PhotoOfTheDayFragment)
                error(R.drawable.ic_error_load_image)
            }
        }

        photoOfTheDayFragmentBinding.includedBottomSheetLayout.apply {
            bottomSheetDescription.text = responseData.explanation
            bottomSheetDescriptionTitle.text = responseData.title
        }

        loadingLayoutIsVisible(false)
    }
}
