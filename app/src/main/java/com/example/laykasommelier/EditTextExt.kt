package com.example.laykasommelier

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

inline fun EditText.doAfterTextChanged(crossinline action: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { action(s.toString()) }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}