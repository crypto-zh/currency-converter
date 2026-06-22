package io.github.currencyconverter.tools

import android.content.Context
import android.content.SharedPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import androidx.core.content.edit

actual object SettingsController: KoinComponent {
    private val prefs: SharedPreferences by lazy {
        get<Context>().getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
    }

    actual fun saveCurrencies(currencies: Set<String>) {
        prefs.edit { putString("currencies", currencies.joinToString(",")) }
    }

    actual fun getCurrencies(): Set<String> =
        prefs.getString("currencies", null).toSet() ?: emptySet()
}