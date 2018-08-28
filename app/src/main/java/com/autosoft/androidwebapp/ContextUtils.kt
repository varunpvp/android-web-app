package com.autosoft.androidwebapp

import android.content.Context
import android.content.Intent
import android.widget.Toast

fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.openImageChooser() {

    val intent = Intent(Intent.ACTION_GET_CONTENT)

    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "image/*"

    val chooser = Intent.createChooser(intent, "Choose image")

    startActivity(chooser)
}

