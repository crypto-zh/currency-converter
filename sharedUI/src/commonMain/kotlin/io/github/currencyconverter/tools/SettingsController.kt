package io.github.currencyconverter.tools

expect object SettingsController {

    fun saveCurrencies(currencies: Set<String>)
    fun getCurrencies(): Set<String>
}

internal fun String?.toSet(): Set<String>? =
    if (this?.isNotBlank() == true) {
        this.split(",")
            .toSet()
    } else {
        null
    }