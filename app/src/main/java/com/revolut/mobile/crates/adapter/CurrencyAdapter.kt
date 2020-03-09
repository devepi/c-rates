package com.revolut.mobile.crates.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.ConfigurationCompat
import androidx.emoji.text.EmojiCompat
import androidx.emoji.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.revolut.mobile.crates.R
import com.revolut.mobile.crates.model.Currency
import com.revolut.mobile.crates.model.formattedDescription
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class CurrencyAdapter(
    val context: Context
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private val locale: Locale by lazy {
        ConfigurationCompat.getLocales(context.resources.configuration)[0]
    }

    private val numberFormatter: NumberFormat by lazy {
        NumberFormat.getNumberInstance(locale)
    }

    private var items = mutableListOf<Currency>()
    private var baseValue: Double = 1.0

    var onItemClick: ((Currency) -> Unit)? = null

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CurrencyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_currency, parent, false))

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemId(position: Int): Long {
        return items[position].hashCode().toLong()
    }

    fun update(list: List<Currency>) {
        val removedItems = items.minus(list)
        val addedItems = list.minus(items)
        items.removeAll(removedItems)
        items.forEach { currency ->
            list
                .filter { it.name == currency.name }
                .forEach { currency.rate = it.rate }
        }
        items.addAll(addedItems)
        notifyDataSetChanged()
    }

    inner class CurrencyViewHolder(
        itemView: View,
        private val currencyDescription: TextView = itemView.findViewById(R.id.currencyDescription),
        private val currencyRate: EditText = itemView.findViewById(R.id.currencyRate),
        private val currencyFlag: EmojiTextView = itemView.findViewById(R.id.currencyFlag)
    ) : RecyclerView.ViewHolder(itemView), TextWatcher {

        init {
            itemView.setOnClickListener {
                adapterPosition.takeIf { it > NO_POSITION }?.also {
                    onItemClick?.invoke(items[it])
                    moveUp()
                    updateBaseValue(currencyRate.text)
                }
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateBaseValue(currencyRate.text.toString())
            itemView.post {
                notifyDataSetChanged()
            }
        }

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        fun bind(currency: Currency?) {
            currencyRate.removeTextChangedListener(this)
            currencyDescription.text = currency?.formattedDescription(context)
            if (adapterPosition == 0) {
                currencyRate.isClickable = true
                if (!currencyRate.isFocused) {
                    currencyRate.setText(numberFormatter.format(baseValue))
                }
                currencyRate.addTextChangedListener(this)
            } else {
                val value = (currency?.rate ?: 0.0) * baseValue
                currencyRate.setText(numberFormatter.format(value))
                currencyRate.isClickable = false
            }

            currencyFlag.text = EmojiCompat.get().process(currency?.image(locale) ?: "")
        }

        private fun moveUp() {
            adapterPosition.takeIf { isMovable(adapterPosition) }?.also { currentPosition ->
                items.removeAt(currentPosition).also {
                    items.add(0, it)
                }
                notifyItemMoved(currentPosition, 0)
            }
        }

        private fun isMovable(position: Int): Boolean = position > 0

        private fun updateBaseValue(string: CharSequence) {
            baseValue = try {
                numberFormatter.parse(string.toString())?.toDouble() ?: 0.0
            } catch (a: ParseException) {
                0.0
            }
        }
    }
}
