package ru.androidlearning.theuniversearoundus.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.androidlearning.theuniversearoundus.R
import ru.androidlearning.theuniversearoundus.databinding.MainActivityBinding
import ru.androidlearning.theuniversearoundus.ui.photo_of_the_day.PhotoOfTheDayFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PhotoOfTheDayFragment.newInstance())
                .commitNow()
        }
    }
}
