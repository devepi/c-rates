package com.revolut.mobile.crates.di

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.interceptors.LogRequestAsCurlInterceptor
import com.github.kittinunf.fuel.core.interceptors.LogResponseInterceptor
import com.revolut.mobile.crates.BuildConfig
import com.revolut.mobile.crates.data.coroutines.CoroutineContextProvider
import com.revolut.mobile.crates.data.file.FileManager
import com.revolut.mobile.crates.data.file.FileManagerImpl
import com.revolut.mobile.crates.data.repository.CurrencyRatesRepository
import com.revolut.mobile.crates.data.repository.CurrencyRatesRepositoryImpl
import com.revolut.mobile.crates.viewmodel.CurrencyRatesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    factory<FileManager> { FileManagerImpl() }

    factory {
        FuelManager().apply { 
            BuildConfig.DEBUG.takeIf { it }.also {
                addRequestInterceptor(LogRequestAsCurlInterceptor)
                addResponseInterceptor(LogResponseInterceptor)
            }
            basePath = "https://hiring.revolut.codes/api/android"
        }
    }

    single { CoroutineContextProvider() }

    viewModel { CurrencyRatesViewModel(get(), get()) }
}

val dataModule = module {
    factory<CurrencyRatesRepository> { CurrencyRatesRepositoryImpl(get(), get()) }
}
