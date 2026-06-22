package io.github.currencyconverter.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(getCommonModules())
    }
}