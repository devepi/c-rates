package com.revolut.mobile.crates.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.revolut.mobile.crates.R
import com.revolut.mobile.crates.adapter.CurrencyAdapter
import com.revolut.mobile.crates.viewmodel.CurrencyRatesViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val model: CurrencyRatesViewModel by viewModel()
    private val adapter: CurrencyAdapter = CurrencyAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureViews()
    }

    override fun onStart() {
        super.onStart()
        subscribe()
    }

    override fun onPause() {
        super.onPause()
        unsubscribe()
    }

    private fun configureViews() {
        val recyclerView = findViewById<RecyclerView>(R.id.currenciesList)
        adapter.setHasStableIds(true)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                recyclerView.scrollToPosition(0)
            }
        })
        adapter.onItemClick = {
            model.baseCurrency = it
        }
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter
    }

    private fun subscribe() = model.crates.observe(this, Observer {
        adapter.update(it)
    })

    private fun unsubscribe() = model.crates.removeObservers(this)
}
