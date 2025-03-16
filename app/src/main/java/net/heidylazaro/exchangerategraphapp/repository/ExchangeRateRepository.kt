package net.heidylazaro.exchangerategraphapp.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.util.Log
import net.heidylazaro.exchangerategraphapp.model.ExchangeRate
class ExchangeRateRepository(private val contentResolver: ContentResolver) {

    @SuppressLint("Range")
    fun getExchangeRates(currency: String, startDate: String, endDate: String): List<ExchangeRate> {
        Log.d(
            "ExchangeRateRepository",
            "getExchangeRates() called with currency: $currency, startDate: $startDate, endDate: $endDate"
        )

        val uri =
            Uri.parse("content://net.heidylazaro.exchangerates.contentprovider/exchange_rates/$currency/$startDate/$endDate")
        Log.d("ExchangeRateRepository", "Query URI: $uri")

        val cursor: Cursor? = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        Log.d("ExchangeRateRepository", "Cursor obtained: ${cursor != null}")

        val exchangeRates = mutableListOf<ExchangeRate>()

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val currencyValue = it.getString(it.getColumnIndex("currency"))
                    val rate = it.getDouble(it.getColumnIndex("rate"))
                    val lastUpdateUnix = it.getString(it.getColumnIndex("lastUpdateUnix"))

                    Log.d(
                        "ExchangeRateRepository",
                        "Currency: $currencyValue, Rate: $rate, Last Update: $lastUpdateUnix"
                    )

                    exchangeRates.add(
                        ExchangeRate(
                            currency = currencyValue,
                            rate = rate,
                            lastUpdateUnix = lastUpdateUnix
                        )
                    )
                } while (it.moveToNext())
            } else {
                Log.d("ExchangeRateRepository", "Cursor is empty")
            }
        } ?: run {
            Log.d("ExchangeRateRepository", "Cursor is null")
        }

        Log.d("ExchangeRateRepository", "Total exchange rates retrieved: ${exchangeRates.size}")
        return exchangeRates
    }
    }


