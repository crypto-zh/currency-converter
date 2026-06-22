package io.github.currencyconverter.domain.repository

interface CurrencyRepository {

    suspend fun getCurrencyRates(): Result<Map<String, Double>>
}