package com.revolut.mobile.crates.app

import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import com.revolut.mobile.crates.di.appModule
import com.revolut.mobile.crates.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class Crates : Application() {

    override fun onCreate() {
        super.onCreate()
        setupDi()
        setupEmoji()
    }

    private fun setupEmoji() {
        EmojiCompat.init(BundledEmojiCompatConfig(this))
    }

    private fun setupDi() {
        startKoin {
            androidLogger()
            androidContext(this@Crates)
            modules(appModule, dataModule)
        }
    }
}