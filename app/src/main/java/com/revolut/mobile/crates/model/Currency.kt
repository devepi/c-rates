package com.revolut.mobile.crates.model

import android.content.Context
import com.revolut.mobile.crates.R
import com.revolut.mobile.crates.helper.dp
import com.revolut.mobile.crates.helper.foregroundColor
import com.revolut.mobile.crates.helper.textSize
import java.util.*

data class Currency (
    val name: String?,
    val description: String?,
    var rate: Double?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Currency

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name?.hashCode() ?: 0
    }

    fun image(locale: Locale): String? = name?.let {
        val countryCodeCaps = it.toUpperCase(locale) // upper case is important because we are calculating offset
        val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }
}

fun Currency.formattedDescription(context: Context) : CharSequence? {
    val fn = name
        ?.foregroundColor(context, R.color.primaryTextColor)
        ?.textSize(16.dp)
    val fd = description
        ?.foregroundColor(context, R.color.secondaryTextColor)
        ?.textSize(12.dp)
    return fn
        ?.append(if (fd?.isEmpty() == true) "" else "\n")
        ?.append(fd ?: "")
}
