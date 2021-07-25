package ru.androidlearning.theuniversearoundus.ui.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    actionText: String? = null,
    action: ((View) -> Unit)? = null
) {
    Snackbar.make(this, message, length).setAction(actionText, action).show()
}
