package net.heidylazaro.exchangerategraphapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.heidylazaro.exchangerategraphapp.model.ExchangeRate
import net.heidylazaro.exchangerategraphapp.repository.ExchangeRateRepository

class ExchangeRateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExchangeRateRepository(application.contentResolver)

    private val _exchangeRates = MutableLiveData<List<ExchangeRate>>()
    val exchangeRates: LiveData<List<ExchangeRate>> = _exchangeRates

    fun fetchExchangeRates(currency: String, starDate: String, endDate: String) {
        viewModelScope.launch {
            _exchangeRates.postValue(repository.getExchangeRates(currency, starDate, endDate))
        }
    }
}
