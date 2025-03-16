package net.heidylazaro.exchangerategraphapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import net.heidylazaro.exchangerategraphapp.ui.theme.ExchangeRateChartScreen
import net.heidylazaro.exchangerategraphapp.viewmodel.ExchangeRateViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ExchangeRateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val exchangeRates = viewModel.exchangeRates.observeAsState(emptyList())

            ExchangeRateChartScreen(
                exchangeRates = exchangeRates.value,
                onLoadData = {
                   /* val startDate = "2025-03-11"
                    val endDate = "2025-03-16"*/
                    currency, startDate, endDate ->
                    viewModel.fetchExchangeRates(currency, startDate, endDate)
                }
            )
        }
    }
}
