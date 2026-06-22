package io.github.currencyconverter.presentation.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.currencyconverter.domain.usecase.GetCurrencyRatesUseCase
import io.github.currencyconverter.presentation.model.CurrencyUIModel
import io.github.currencyconverter.tools.SettingsController
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val getCurrencyRatesUseCase: GetCurrencyRatesUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<MainScreenState> = MutableStateFlow(
        MainScreenState(
            currencies = SettingsController.getCurrencies().map { name ->
                CurrencyUIModel(name = name, value = "")
            },
        )
    )
    private val _event = Channel<MainScreenEvent>(Channel.BUFFERED)

    private var ratesMap: Map<String, Double> = emptyMap()
    private var lastEditedCurrency: String? = null
    private var lastEditedValue: Double = 0.0

    val event = _event.receiveAsFlow()
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    init {
        loadRates()
    }

    fun onIntent(intent: MainScreenIntent) {
        when (intent) {
            is MainScreenIntent.OnCurrencyValueChanged -> onCurrencyValueChanged(intent.model)
            is MainScreenIntent.OnCurrencyAdded -> onCurrencyAdded(intent.currency)
            is MainScreenIntent.OnCurrencyDeleted -> onCurrencyDeleted(intent.currency)
            MainScreenIntent.OnCurrencyAddClicked -> onCurrencyAddClicked()
        }
    }

    private fun loadRates() {
        _state.update { it.copy(showLoading = true) }
        viewModelScope.launch {
            getCurrencyRatesUseCase.invoke()
                .onSuccess { rates ->
                    ratesMap = rates
                    _state.update { it.copy(showLoading = false) }
                }
                .onFailure {
                    _state.update { it.copy(showLoading = false) }
                    viewModelScope.launch { _event.send(MainScreenEvent.ShowError) }
                }
        }
    }

    private fun onCurrencyAddClicked() {
        viewModelScope.launch {
            val currentCurrencies = _state.value.currencies.map { it.name }.toSet()

            _event.send(
                MainScreenEvent.NavigateToAdd(
                    availableCurrencies = ratesMap.keys
                        .filter { it !in currentCurrencies }
                        .sorted()
                )
            )
        }
    }

    private fun onCurrencyValueChanged(model: CurrencyUIModel) {
        val inputValue = model.value.toDoubleOrNull() ?: run {
            _state.update { state ->
                state.copy(
                    currencies = state.currencies.map { it.copy(value = "") }
                )
            }
            lastEditedCurrency = null
            lastEditedValue = 0.0
            return
        }

        lastEditedCurrency = model.name
        lastEditedValue = inputValue

        val rateOfEdited = ratesMap[model.name] ?: return
        val amountInUsd = inputValue / rateOfEdited

        _state.update { state ->
            state.copy(
                currencies = state.currencies.map { currency ->
                    if (currency.name == model.name) {
                        currency.copy(value = model.value)
                    } else {
                        val targetRate = ratesMap[currency.name]
                        if (targetRate != null) {
                            currency.copy(value = formatValue(amountInUsd * targetRate))
                        } else {
                            currency.copy(value = "")
                        }
                    }
                }
            )
        }
    }

    private fun onCurrencyAdded(currency: String) {
        val currentCurrencies = _state.value.currencies
        if (currentCurrencies.any { it.name == currency }) return

        val newCurrency = CurrencyUIModel(
            name = currency,
            value = computeValueForCurrency(currency)
        )
        val updatedList = currentCurrencies + newCurrency

        SettingsController.saveCurrencies(updatedList.map { it.name }.toSet())
        _state.update { it.copy(currencies = updatedList) }
    }

    private fun onCurrencyDeleted(currency: String) {
        val updatedList = _state.value.currencies.filter { it.name != currency }
        SettingsController.saveCurrencies(updatedList.map { it.name }.toSet())
        _state.update { it.copy(currencies = updatedList) }
    }

    private fun computeValueForCurrency(currency: String): String {
        val editedCurrency = lastEditedCurrency ?: return ""
        val rateOfEdited = ratesMap[editedCurrency] ?: return ""
        val targetRate = ratesMap[currency] ?: return ""

        val amountInUsd = lastEditedValue / rateOfEdited
        return formatValue(amountInUsd * targetRate)
    }

    private fun formatValue(value: Double): String {
        return if (value >= 1.0) {
            val rounded = (value * 100).toLong()
            val intPart = rounded / 100
            val fracPart = rounded % 100
            "$intPart.${fracPart.toString().padStart(2, '0')}"
        } else {
            val scaled = (value * 100_000_000).toLong()
            val str = scaled.toString().padStart(8, '0')
            val trimmed = str.trimEnd('0')
            "0.$trimmed".trimEnd('.')
        }
    }
}