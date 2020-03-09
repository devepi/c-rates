package com.revolut.mobile.crates.data.json

import org.junit.Test

import org.junit.Assert.*

class JsonMapperTest {
    private val currenciesSource =
        """
        { 
            "EUR": "Euro",
            "RUB": "Russian Ruble",
            "USD": "United States Dollar",
            "NIO": "Nicaraguan Córdoba"
        }
        """
    private val ratesSource = """
            {
                baseCurrency: "RUB",
                rates: {
                    AUD: 0.021,
                    EUR: 0.026,
                    NIO: 0.056
                }
            }
            """

    @Test fun currenciesSuccess() {
        val currencies = currenciesSource.json().currencies()
        assertNotNull(currencies)
        assertEquals(4, currencies.size)
        assertEquals("Russian Ruble", currencies["RUB"])
        assertEquals("Nicaraguan Córdoba", currencies["NIO"])
    }

    @Test fun ratesSuccess() {
        val rates = ratesSource.json().rates(currenciesSource.json().currencies())
        assertNotNull(rates)
        assertEquals(4, rates.size)
        // base currency
        assertEquals("RUB", rates[0].name)
        assertEquals("Russian Ruble", rates[0].description)
        assertEquals(1.0, rates[0].rate!!, 0.0001)
        // first rate
        assertEquals("AUD", rates[1].name)
        assertNull(rates[1].description)
        assertNotNull(rates[1].rate)
        assertEquals(0.021, rates[1].rate!!, 0.0001)
    }

    @Test fun currenciesEmpty() {
        val currencies = "".json().currencies()
        assertNotNull(currencies)
        assertEquals(0, currencies.size)
    }

    @Test fun ratesEmptyWithEmptyCurrencies() {
        val currencies = "".json().currencies()
        val rates = "".json().rates(currencies)
        assertNotNull(rates)
        assertEquals(0, rates.size)
    }

    @Test fun ratesWithNullCurrencies() {
        val rates = ratesSource.json().rates()
        assertNotNull(rates)
        assertEquals(4, rates.size)
        // base currency
        assertEquals("RUB", rates[0].name)
        assertNull(rates[0].description)
        assertEquals(1.0, rates[0].rate!!, 0.0001)
        // first rate
        assertEquals("AUD", rates[1].name)
        assertNull(rates[1].description)
        assertNotNull(rates[1].rate)
        assertEquals(0.021, rates[1].rate!!, 0.0001)
    }
}
