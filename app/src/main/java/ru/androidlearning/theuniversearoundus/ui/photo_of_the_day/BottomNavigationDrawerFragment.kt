package ru.androidlearning.theuniversearoundus.ui.photo_of_the_day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import ru.androidlearning.theuniversearoundus.R

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_navigation_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnMenuItemSelectedActions(view.findViewById(R.id.bottom_navigation_view))
    }

    private fun setOnMenuItemSelectedActions(navigationView: NavigationView?) {
        navigationView?.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_one -> {
                    Toast.makeText(context, "Navigation ill be implemented later", Toast.LENGTH_SHORT).show() //заглушка, в будущем будет доработано
                    dismiss()
                }
                R.id.navigation_two -> {
                    Toast.makeText(context, "Navigation will be implemented later", Toast.LENGTH_SHORT).show() //заглушка, в будущем будет доработано
                    dismiss()
                }
            }
            true
        }
    }
}
