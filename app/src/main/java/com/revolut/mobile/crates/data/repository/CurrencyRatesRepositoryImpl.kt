package com.revolut.mobile.crates.data.repository

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.revolut.mobile.crates.data.file.FileManager
import com.revolut.mobile.crates.data.json.JsonMapper
import com.revolut.mobile.crates.data.json.currencies
import com.revolut.mobile.crates.data.json.json
import com.revolut.mobile.crates.data.json.rates
import com.revolut.mobile.crates.model.Currency
import org.json.JSONObject

class CurrencyRatesRepositoryImpl(
    private val fileManager: FileManager,
    private val fuelManager: FuelManager
) : CurrencyRatesRepository {

    private val currencies: Map<String, String> by lazy {
        fileManager
            .readCurrencies()
            ?.json()
            ?.currencies()
            ?: mapOf()
    }

    override suspend fun get(base: Currency?): List<Currency> {
        val (_, _, result) = fuelManager
            .get("/latest", listOf("base" to base?.name))
            .responseString()

        return when (result) {
            is Result.Failure ->
                listOf()
            is Result.Success ->
                result
                    .get()
                    .json()
                    .rates(currencies)
        }
    }
}
