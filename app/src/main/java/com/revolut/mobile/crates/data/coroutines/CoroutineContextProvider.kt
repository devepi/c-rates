package com.revolut.mobile.crates.data.coroutines

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class CoroutineContextProvider {
    open val Main: CoroutineContext by lazy { Dispatchers.Main }
    open val IO: CoroutineContext by lazy { Dispatchers.IO }
}

class TestCoroutineContextProvider : CoroutineContextProvider() {
    override val Main: CoroutineContext by lazy { Dispatchers.Unconfined }
    override val IO: CoroutineContext by lazy { Dispatchers.Unconfined }
}