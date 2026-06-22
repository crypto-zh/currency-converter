package io.github.currencyconverter.tools

import platform.Foundation.NSUserDefaults

actual object SettingsController {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun saveCurrencies(currencies: Set<String>) {
        defaults.setObject(currencies.joinToString(","), forKey = "currencies")
    }

    actual fun getCurrencies(): Set<String> =
        (defaults.stringForKey("currencies")).toSet() ?: emptySet()
}