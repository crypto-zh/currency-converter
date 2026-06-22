package io.github.currencyconverter.di

import io.github.currencyconverter.data.HttpClientFactory
import io.github.currencyconverter.data.datasource.CurrencyDataSource
import io.github.currencyconverter.data.datasource.CurrencyDataSourceImpl
import io.github.currencyconverter.data.repository.CurrencyRepositoryImpl
import io.github.currencyconverter.domain.repository.CurrencyRepository
import io.github.currencyconverter.domain.usecase.GetCurrencyRatesUseCase
import io.github.currencyconverter.presentation.screen.main.MainViewModel
import org.koin.dsl.module

private val dataModule = module {
    single { HttpClientFactory.create() }
    single<CurrencyDataSource> { CurrencyDataSourceImpl(get()) }
    single<CurrencyRepository> { CurrencyRepositoryImpl(get()) }
}

private val domainModule = module {
    single { GetCurrencyRatesUseCase(get()) }
}

private val viewModelModule = module {
    factory { MainViewModel(get()) }
}


fun getCommonModules() = listOf(
    dataModule,
    domainModule,
    viewModelModule
)