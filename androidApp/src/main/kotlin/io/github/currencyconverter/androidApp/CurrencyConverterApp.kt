package io.github.currencyconverter.androidApp

import android.app.Application
import io.github.currencyconverter.di.getCommonModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CurrencyConverterApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            modules(getCommonModules())
            androidContext(this@CurrencyConverterApp)
        }
    }
}