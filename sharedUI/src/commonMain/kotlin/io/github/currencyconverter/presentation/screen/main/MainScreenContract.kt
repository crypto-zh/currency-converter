package io.github.currencyconverter.presentation.screen.main

import io.github.currencyconverter.presentation.model.CurrencyUIModel

data class MainScreenState(
    val currencies: List<CurrencyUIModel> = emptyList(),
    val showLoading: Boolean = false
)

sealed class MainScreenIntent {

    data class OnCurrencyValueChanged(val model: CurrencyUIModel) : MainScreenIntent()

    data class OnCurrencyAdded(val currency: String) : MainScreenIntent()

    data class OnCurrencyDeleted(val currency: String) : MainScreenIntent()

    data object OnCurrencyAddClicked: MainScreenIntent()
}

sealed class MainScreenEvent {

    data class NavigateToAdd(
        val availableCurrencies: List<String>
    ) : MainScreenEvent()

    data object ShowError : MainScreenEvent()
}