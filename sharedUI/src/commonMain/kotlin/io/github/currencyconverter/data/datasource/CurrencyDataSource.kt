package io.github.currencyconverter.data.datasource

import io.github.currencyconverter.data.model.GetCurrencyResponse

interface CurrencyDataSource {

    suspend fun getCurrencies(): Result<GetCurrencyResponse>
}