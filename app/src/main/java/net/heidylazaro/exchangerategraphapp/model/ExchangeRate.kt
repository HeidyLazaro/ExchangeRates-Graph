package net.heidylazaro.exchangerategraphapp.model

data class ExchangeRate(
    //val id: Int,
    val currency: String,
    val rate: Double,
    val lastUpdateUnix: String,
    //val lastUpdateUtc: String
)
