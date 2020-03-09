package com.revolut.mobile.crates.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.revolut.mobile.crates.data.coroutines.CoroutineContextProvider
import com.revolut.mobile.crates.data.repository.CurrencyRatesRepository
import com.revolut.mobile.crates.model.Currency
import kotlinx.coroutines.delay

class CurrencyRatesViewModel(
    repo: CurrencyRatesRepository,
    contextProvider: CoroutineContextProvider
): ViewModel() {
    var baseCurrency: Currency? = null

    val crates: LiveData<List<Currency>> = liveData(contextProvider.IO) {
        do {
            emit(repo.get(baseCurrency))
            delay(1_000)
        } while (true)
    }
}
