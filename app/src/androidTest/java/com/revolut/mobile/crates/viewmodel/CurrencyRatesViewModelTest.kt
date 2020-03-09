package com.revolut.mobile.crates.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.revolut.mobile.crates.data.coroutines.TestCoroutineContextProvider
import com.revolut.mobile.crates.data.repository.CurrencyRatesRepository
import com.revolut.mobile.crates.model.Currency
import com.revolut.mobile.crates.rule.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class CurrencyRatesViewModelTest {
    @get:Rule val instantExecuteRule = InstantTaskExecutorRule()
    @get:Rule val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: CurrencyRatesViewModel
    private val repo = mockk<CurrencyRatesRepository>()
    private val observer = spyk(object : Observer<List<Currency>> {
        override fun onChanged(t: List<Currency>?) {}
    })

    @Before fun setUp() {
        viewModel = CurrencyRatesViewModel(repo, TestCoroutineContextProvider())
    }

    @Test fun testSuccessOnce() = testCoroutineRule.runBlockingTest {
        // Given
        val list = listOf(Currency("TBR", "Tubrik", 7.77))
        coEvery { repo.get(any()) } returns list

        // When
        viewModel.crates.observeForever(observer)

        // Then
        verify { observer.onChanged(list) }
    }

    @Test fun testSuccessTwiceTimes() {
        // Given
        val list = listOf(Currency("TBR", "Tubrik", 7.77))
        
        testCoroutineRule.runBlockingTest {
            coEvery { repo.get(any()) } returns list

            // When
            viewModel.crates.observeForever(observer)
            advanceTimeBy(1_000)

        }
        // Then
        verify(exactly = 2, timeout = 1000) { observer.onChanged(list) }
    }
}