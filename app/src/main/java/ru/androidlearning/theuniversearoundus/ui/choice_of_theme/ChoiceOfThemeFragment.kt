package ru.androidlearning.theuniversearoundus.ui.choice_of_theme

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.ChoiceOfThemeFragmentBinding
import ru.androidlearning.theuniversearoundus.ui.MainActivity

const val THEME_KEY = "THEME"

class ChoiceOfThemeFragment : Fragment(), View.OnClickListener {
    companion object {
        @JvmStatic
        fun newInstance() = ChoiceOfThemeFragment()
    }

    private var _binding: ChoiceOfThemeFragmentBinding? = null
    private val choiceOfThemeFragmentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChoiceOfThemeFragmentBinding.inflate(inflater, container, false)
        return choiceOfThemeFragmentBinding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        (activity as MainActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            setHasOptionsMenu(true)
        }
        choiceOfThemeFragmentBinding.run {
            blueThemeButton.setOnClickListener(this@ChoiceOfThemeFragment)
            greenThemeButton.setOnClickListener(this@ChoiceOfThemeFragment)
            orangeThemeButton.setOnClickListener(this@ChoiceOfThemeFragment)
        }
    }

    override fun onClick(v: View?) {
        val theme = when (v?.id) {
            R.id.blue_theme_button -> {
                R.style.TheUniverseAroundUs
            }
            R.id.green_theme_button -> {

                R.style.TheUniverseAroundUs_GreenTheme
            }
            R.id.orange_theme_button -> {

                R.style.TheUniverseAroundUs_OrangeTheme
            }
            else -> {
                R.style.TheUniverseAroundUs
            }
        }

        activity?.let {
            it.getPreferences(Context.MODE_PRIVATE).edit().putInt(THEME_KEY, theme).apply()
            it.recreate()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (parentFragmentManager.backStackEntryCount > 0) {
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}