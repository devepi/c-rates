package com.revolut.mobile.crates.data.json

import com.revolut.mobile.crates.model.Currency
import org.json.JSONException
import org.json.JSONObject

/**
 * All-in-one parser
 *
 * Single mapper class to parse all JSONs
 */
class JsonMapper {

    /**
     * Parsing json to currencies list:
     *  key - currency code, e.g. EUR
     *  value - currency name Euro
     *
     * @param json source content
     *
     * @return Key-value list with currencies code and descriptions of an empty map
     */
    fun currencies(json: JSONObject?): Map<String, String> =
        mutableMapOf<String, String>().apply {
            json?.keys()?.forEach { code ->
                (json.opt(code) as? String)?.let { name ->
                    put(code, name)
                }
            }
        }

    /**
     * Parsing json to list of {@link Currency}'s rates
     *
     * @param json source content
     * @param currencies optional map of currencies
     *
     * @return list of currency rates or an empty list
     */
    fun rates(json: JSONObject?, currencies: Map<String, String>?): List<Currency> =
        mutableListOf<Currency>().apply {
            (json?.opt("baseCurrency") as? String)?.let { baseCode ->
                add(Currency(baseCode, currencies?.get(baseCode), 1.0)) // base currency is always 1.0
            }
            json?.optJSONObject("rates")?.let { rates ->
                rates.keys().forEach {
                    add(Currency(it, currencies?.get(it), rates.opt(it) as? Double))
                }
            }
        }
}

/**
 * JsonMapper helper methods
 */
fun String.json() = try { JSONObject(this) } catch (e: JSONException) { JSONObject() }
fun JSONObject.currencies() = JsonMapper().currencies(this)
fun JSONObject.rates(currencies: Map<String, String>? = null) = JsonMapper().rates(this, currencies)
