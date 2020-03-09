package com.revolut.mobile.crates.helper

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun SpannableStringBuilder.spanText(span: Any): SpannableStringBuilder {
    setSpan(span, 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

fun CharSequence.toSpannable() = SpannableStringBuilder(this)

fun CharSequence.textSize(sizeInPixels: Int) = toSpannable().spanText(AbsoluteSizeSpan(sizeInPixels))

fun CharSequence.foregroundColor(context: Context, @ColorRes color: Int): SpannableStringBuilder =
    toSpannable().spanText(ForegroundColorSpan(ContextCompat.getColor(context, color)))