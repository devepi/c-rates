package com.revolut.mobile.crates.data.repository

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.revolut.mobile.crates.data.file.FileManager
import com.revolut.mobile.crates.model.Currency
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CurrencyRatesRepositoryImplTest {

    private val client: Client = mockk()
    private val fileManager: FileManager = mockk()
    private lateinit var fuelManager: FuelManager
    private lateinit var currencyRatesRepository: CurrencyRatesRepository

    @Before fun setup() {
        fuelManager = FuelManager()
        fuelManager.client = client
        fuelManager.basePath = "https://127.0.0.1"
        currencyRatesRepository = CurrencyRatesRepositoryImpl(fileManager, fuelManager)
    }

    @Test fun getFailed() {
        val someJson = """
            {
                baseCurrency: "RUB",
                rates: {
                    AUD: 0.021,
                    BGN: 0.026,
                    BRL: 0.056
                }
            }
            """

        every { client.executeRequest(any()).statusCode } returns 400
        every { client.executeRequest(any()).responseMessage } returns "BAD REQUEST"
        every { client.executeRequest(any()).data } returns someJson.toByteArray()

        val result = runBlocking { currencyRatesRepository.get() }
        assertNotNull(result)
        assertEquals(0, result.size)
    }
    
    @Test fun getSuccess() {
        val someJson = """
            {
                baseCurrency: "RUB",
                rates: {
                    AUD: 0.021,
                    BGN: 0.026,
                    EUR: 1.256
                }
            }
            """
        val currenciesSource =
            """
            { 
                "EUR": "Euro",
                "RUB": "Russian Ruble",
                "USD": "United States Dollar",
                "NIO": "Nicaraguan Córdoba"
            }
            """

        every { fileManager.readCurrencies() } returns currenciesSource

        every { client.executeRequest(any()).statusCode } returns 200
        every { client.executeRequest(any()).responseMessage } returns "OK"
        every { client.executeRequest(any()).data } returns someJson.toByteArray()

        val result = runBlocking { currencyRatesRepository.get() }
        assertNotNull(result)
        assertEquals(4, result.size)
        assertTrue(result.contains(Currency("EUR", null, null)))
        assertTrue(result.contains(Currency("BGN", null, null)))
        assertEquals("Euro", result.find { it.name == "EUR" }?.description)
        assertNull(result.find { it.name == "BGN" }?.description)
    }

    @Test fun getSuccessWithoutDescriptions() {
        val someJson = """
            {
                baseCurrency: "RUB",
                rates: {
                    AUD: 0.021,
                    BGN: 0.026,
                    BRL: 0.056
                }
            }
            """

        every { fileManager.readCurrencies() } returns null

        every { client.executeRequest(any()).statusCode } returns 200
        every { client.executeRequest(any()).responseMessage } returns "OK"
        every { client.executeRequest(any()).data } returns someJson.toByteArray()

        val result = runBlocking { currencyRatesRepository.get() }
        assertNotNull(result)
        assertEquals(4, result.size)
        assertTrue(result.contains(Currency("RUB", null, null)))
        assertTrue(result.contains(Currency("AUD", null, null)))
        assertTrue(result.contains(Currency("BGN", null, null)))
        assertTrue(result.contains(Currency("BRL", null, null)))
        assertNull(result.find { it.name == "RUB" }?.description)
        assertNull(result.find { it.name == "AUD" }?.description)
        assertNull(result.find { it.name == "BGN" }?.description)
        assertNull(result.find { it.name == "BRL" }?.description)
    }

}
