package ir.mostafa.nematpour.khodhesab.ui

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat


class MyNumberWatcher(private val editText: EditText) : TextWatcher {
    private val digit = 0
    override fun beforeTextChanged(
        charSequence: CharSequence,
        i: Int,
        i1: Int,
        i2: Int
    ) {
    }

    override fun onTextChanged(
        charSequence: CharSequence,
        i: Int,
        i1: Int,
        i2: Int
    ) {
    }

    override fun afterTextChanged(editable: Editable) {
        editText.removeTextChangedListener(this)
        var s = editText.text.toString()
        s = s.replace(",", "")
        if (s.length > 0) {
            val sdd = DecimalFormat("#,###")
            val doubleNumber = s.toDouble()
            val format: String = sdd.format(doubleNumber)
            editText.setText(format)
            editText.setSelection(format.length)
        }
        editText.addTextChangedListener(this)
    }

}