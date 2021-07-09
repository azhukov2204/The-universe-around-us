package ru.androidlearning.theuniversearoundus.ui.photos_of_the_universe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.PhotosOfTheUniverseFragmentBinding

class PhotosOfTheUniverseFragment : Fragment() {


    private var _binding: PhotosOfTheUniverseFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PhotosOfTheUniverseFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            photosOfUniverseViewPager.adapter = PhotosOfTheUniverseViewPagerAdapter(this@PhotosOfTheUniverseFragment)
            TabLayoutMediator(photosOfUniverseTabLayout, photosOfUniverseViewPager) { tab, position ->
                tab.setIcon(childFragmentDetailsList[position].iconResourceId)
                tab.text = context?.getText(childFragmentDetailsList[position].titleStringResourceId)
            }.attach()
            viewPagerIndicator.setViewPager(photosOfUniverseViewPager)
            viewPagerIndicator.createIndicators(3, 0)
            photosOfUniverseViewPager.setPageTransformer(ZoomOutPageTransformer())
        }
    }

    data class ChildFragmentDetails(
        val iconResourceId: Int,
        val titleStringResourceId: Int,
        val searchString: String
    )

    companion object {
        val childFragmentDetailsList: List<ChildFragmentDetails> = listOf(
            ChildFragmentDetails(R.drawable.ic_earth, R.string.earth_tab_title, "Earth photo"),
            ChildFragmentDetails(R.drawable.ic_mars, R.string.mars_tab_title, "Mars photo"),
            ChildFragmentDetails(R.drawable.ic_moon, R.string.moon_tab_title, "Moon photo")
        )
    }
}
