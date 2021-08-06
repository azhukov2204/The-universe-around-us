package ru.androidlearning.theuniversearoundus.ui.photo_of_the_day

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.TransitionManager
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.PhotoOfTheDayFragmentBinding
import ru.androidlearning.theuniversearoundus.model.DataLoadState
import ru.androidlearning.theuniversearoundus.model.web.data_sources.api.PictureOfTheDayDTO
import ru.androidlearning.theuniversearoundus.ui.photo_of_the_day.image.ImageFragment
import ru.androidlearning.theuniversearoundus.ui.photo_of_the_day.youtube.YouTubeFragment
import ru.androidlearning.theuniversearoundus.ui.utils.showSnackBar
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

private const val WIKI_BASE_URL = "https://en.wikipedia.org/wiki/"
private const val IMAGE_MEDIA_TYPE = "image"
private const val VIDEO_MEDIA_TYPE = "video"
private const val REGEX_GET_VIDEO_ID = "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$"
private const val DATE_FORMAT = "yyyy-MM-dd"
private const val ANIMATION_FADE_DURATION: Long = 1000L

class PhotoOfTheDayFragment : Fragment() {
    private var _binding: PhotoOfTheDayFragmentBinding? = null
    private val photoOfTheDayFragmentBinding get() = _binding!!
    private val photoOfTheDayViewModel: PhotoOfTheDayViewModel by lazy { ViewModelProvider(this).get(PhotoOfTheDayViewModel::class.java) }
    private val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

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
        initChips()
    }

    private fun initChips() {
        val calendar = Calendar.getInstance()
        val currentDateString = simpleDateFormat.format(calendar.time)
        val oneDayAgoString = simpleDateFormat.format(calendar.apply { add(Calendar.DATE, -1) }.time)
        val twoDayAgoString = simpleDateFormat.format(calendar.apply { add(Calendar.DATE, -1) }.time)
        //twoDayAgoString = "2021-07-07" //за эту дату приходит ссылка на видео, для теста

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

    private fun renderData(dataLoadState: DataLoadState<PictureOfTheDayDTO>) {
        val transitionsContainer = photoOfTheDayFragmentBinding.photoOfTheDayDescriptionScroll
        TransitionManager.beginDelayedTransition(transitionsContainer, Fade().apply { duration = ANIMATION_FADE_DURATION })
        when (dataLoadState) {
            is DataLoadState.Success -> {
                fillData(dataLoadState.responseData)
            }
            is DataLoadState.Loading -> {
                clearData()
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

    private fun clearData() {
        photoOfTheDayFragmentBinding.includedPhotoDescriptionSheet.apply {
            bottomSheetDescription.text = null
            bottomSheetDescriptionTitle.text = null
        }
        for (fragment in childFragmentManager.fragments) {
            childFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    private fun fillData(responseData: PictureOfTheDayDTO) {
        photoOfTheDayFragmentBinding.includedPhotoDescriptionSheet.apply {
            responseData.explanation?.let { it ->
                val lengthOfFirstWord = it.split(" ").first().length
                val spannableDescription = SpannableString(it).apply {
                    setSpan(StyleSpan(Typeface.BOLD_ITALIC), 0, lengthOfFirstWord, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(UnderlineSpan(), 0, lengthOfFirstWord, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(ForegroundColorSpan(Color.BLACK), 0, lengthOfFirstWord, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                bottomSheetDescription.setText(spannableDescription, TextView.BufferType.SPANNABLE)
            }
            bottomSheetDescriptionTitle.text = responseData.title
        }
        val imageUrl = responseData.url
        when (responseData.mediaType) {
            VIDEO_MEDIA_TYPE -> loadVideo(imageUrl)
            IMAGE_MEDIA_TYPE -> loadImage(imageUrl)
            else -> loadImage(imageUrl)
        }
    }

    private fun loadImage(imageUrl: String?) {
        childFragmentManager.beginTransaction().replace(R.id.image_or_video_fragment, ImageFragment.newInstance(imageUrl)).runOnCommit {
            loadingLayoutIsVisible(false)
        }.commit()
    }

    private fun loadVideo(imageUrl: String?) {
        var videoId: String? = null
        val pattern = Pattern.compile(REGEX_GET_VIDEO_ID, Pattern.CASE_INSENSITIVE)
        imageUrl?.let {
            val matcher = pattern.matcher(it)
            if (matcher.matches()) {
                videoId = matcher.group(1)
            }
        }
        childFragmentManager.beginTransaction().replace(R.id.image_or_video_fragment, YouTubeFragment.newInstance(videoId)).runOnCommit {
            loadingLayoutIsVisible(false)
        }.commit()
    }
}
