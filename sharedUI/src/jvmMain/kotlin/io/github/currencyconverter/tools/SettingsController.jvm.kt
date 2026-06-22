package io.github.currencyconverter.tools

import java.util.prefs.Preferences

actual object SettingsController {
    private val prefs = Preferences.userRoot().node("currency_converter")

    actual fun saveCurrencies(currencies: Set<String>) {
        prefs.put("currencies", currencies.joinToString(","))
    }

    actual fun getCurrencies(): Set<String> =
        prefs.get("currencies", null).toSet() ?: emptySet()
}