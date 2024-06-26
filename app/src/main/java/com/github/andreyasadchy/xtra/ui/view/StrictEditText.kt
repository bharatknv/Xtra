package com.github.andreyasadchy.xtra.ui.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class StrictEditText : TextInputEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSelectionChanged(start: Int, end: Int) {
        val length = text!!.length
        if (start != length || end != length) {
            setSelection(length, length)
            return
        }
        super.onSelectionChanged(start, end)
    }
}