package ru.androidlearning.theuniversearoundus.ui.photos_of_the_universe

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.androidlearning.theuniversearoundus.ui.photos_of_the_universe.item_fragment.PhotosOfTheUniverseItemFragment

class PhotosOfTheUniverseViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return PhotosOfTheUniverseFragment.childFragmentDetailsList.size
    }

    override fun createFragment(position: Int): Fragment {
        return PhotosOfTheUniverseItemFragment.newInstance(PhotosOfTheUniverseFragment.childFragmentDetailsList[position].searchString)
    }
}
