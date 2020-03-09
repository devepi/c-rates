package com.revolut.mobile.crates.data.file

class FileManagerImpl : FileManager {
    override fun read(file: String): String? = javaClass.getResource(file)?.readText()

    override fun readCurrencies(): String? = read(FileManager.CURRENCIES_FILE)
}