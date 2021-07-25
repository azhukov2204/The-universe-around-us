package ru.androidlearning.theuniversearoundus.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.MainActivityBinding
import ru.androidlearning.theuniversearoundus.ui.choice_of_theme.THEME_KEY
import ru.androidlearning.theuniversearoundus.ui.utils.getResourceIdFromThemeNumber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedTheme()
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun applySavedTheme() {
        val themeResourceId = getResourceIdFromThemeNumber(getPreferences(Context.MODE_PRIVATE).getInt(THEME_KEY, 0))
        setTheme(themeResourceId)
    }
}
