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
        Log.d("ExchangeRateRepository", "getExchangeRates() called with currency: $currency, startDate: $startDate, endDate: $endDate")

        // Construir la URI para la consulta en el ContentProvider
        val uri = Uri.parse("content://net.heidylazaro.exchangerates.contentprovider/exchange_rates/$currency/$startDate/$endDate")
        Log.d("ExchangeRateRepository", "Query URI: $uri")

        // Realizar la consulta especificando las columnas a recuperar
        val cursor: Cursor? = contentResolver.query(
            uri,
            arrayOf("currency", "rate", "lastUpdateUnix"),  // Especifica las columnas que esperas
            null,
            null,
            null
        )

        Log.d("ExchangeRateRepository", "Cursor obtained: ${cursor != null}")

        // Crear la lista donde se almacenarán las tasas de cambio
        val exchangeRates = mutableListOf<ExchangeRate>()

        // Si el cursor no es nulo y tiene datos
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    // Obtener los datos de las columnas
                    val currencyValue = it.getString(it.getColumnIndex("currency"))
                    val rate = it.getDouble(it.getColumnIndex("rate"))
                    val lastUpdateUnix = it.getString(it.getColumnIndex("lastUpdateUnix"))

                    // Imprimir los valores para depuración
                    Log.d("ExchangeRateRepository", "Currency: $currencyValue, Rate: $rate, Last Update: $lastUpdateUnix")

                    // Agregar la tasa de cambio a la lista
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
        }

        // Verificar cuántas tasas de cambio se han recuperado
        Log.d("ExchangeRateRepository", "Total exchange rates retrieved: ${exchangeRates.size}")
        return exchangeRates
    }
}
