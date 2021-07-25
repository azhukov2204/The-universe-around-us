package ru.androidlearning.theuniversearoundus.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.MainActivityBinding
import ru.androidlearning.theuniversearoundus.ui.choice_of_theme.THEME_KEY
import ru.androidlearning.theuniversearoundus.ui.photo_of_the_day.PhotoOfTheDayFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        applySavedTheme()
        setContentView(view)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PhotoOfTheDayFragment.newInstance())
                .commitNow()
        }
    }

    private fun applySavedTheme() {
        val theme = getPreferences(Context.MODE_PRIVATE).getInt(THEME_KEY, R.id.blue_theme_button)
        setTheme(theme)
    }

}
