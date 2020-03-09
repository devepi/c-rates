package com.revolut.mobile.crates.data.repository

import com.revolut.mobile.crates.model.Currency

interface CurrencyRatesRepository {
    suspend fun get(base: Currency? = null): List<Currency>
}
