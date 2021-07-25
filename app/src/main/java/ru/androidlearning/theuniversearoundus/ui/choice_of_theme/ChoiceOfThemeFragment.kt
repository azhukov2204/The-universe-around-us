package ru.androidlearning.theuniversearoundus.ui.choice_of_theme

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.ChoiceOfThemeFragmentBinding
import ru.androidlearning.theuniversearoundus.ui.utils.getThemeNumberFromResourceId

const val THEME_KEY = "THEME"

class ChoiceOfThemeFragment : Fragment() {
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
        choiceOfThemeFragmentBinding.run {
            blueThemeButton.setOnClickListener(onClickListener)
            greenThemeButton.setOnClickListener(onClickListener)
            orangeThemeButton.setOnClickListener(onClickListener)
        }
    }

    private val onClickListener = View.OnClickListener { view ->
        val themeResourceId = when (view?.id) {
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

        activity?.let { fragmentActivity ->
            fragmentActivity.getPreferences(Context.MODE_PRIVATE).edit().putInt(THEME_KEY, getThemeNumberFromResourceId(themeResourceId)).apply()
            fragmentActivity.recreate()
        }
    }

}