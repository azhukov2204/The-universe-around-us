package ru.androidlearning.theuniversearoundus.ui.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import ru.androidlearning.theuniversearoundus.R

fun View.showSnackBar(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    actionText: String? = null,
    action: ((View) -> Unit)? = null
) {
    Snackbar.make(this, message, length).setAction(actionText, action).show()
}

fun getThemeNumberFromResourceId(resourceId: Int) = when (resourceId) {
    R.style.TheUniverseAroundUs -> 0
    R.style.TheUniverseAroundUs_OrangeTheme -> 1
    R.style.TheUniverseAroundUs_GreenTheme -> 2
    else -> 0
}

fun getResourceIdFromThemeNumber(themeNumber: Int) = when (themeNumber) {
    0 -> R.style.TheUniverseAroundUs
    1 -> R.style.TheUniverseAroundUs_OrangeTheme
    2 -> R.style.TheUniverseAroundUs_GreenTheme
    else -> R.style.TheUniverseAroundUs
}
