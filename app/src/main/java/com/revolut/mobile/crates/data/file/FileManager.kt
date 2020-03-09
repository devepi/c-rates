package com.revolut.mobile.crates.data.file

interface FileManager {

    fun read(file: String): String?

    fun readCurrencies(): String?

    companion object {
        const val CURRENCIES_FILE = "/res/raw/currencies.json"
    }
}