package io.github.currencyconverter.data.repository

import io.github.currencyconverter.data.datasource.CurrencyDataSource
import io.github.currencyconverter.domain.repository.CurrencyRepository
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class CurrencyRepositoryImpl(
    private val currencyDataSource: CurrencyDataSource
) : CurrencyRepository {

    override suspend fun getCurrencyRates(): Result<Map<String, Double>> =
        currencyDataSource.getCurrencies().map { response ->
            response.rates.convertRatesToMap()
        }

    private fun JsonObject.convertRatesToMap(): Map<String, Double> =
        entries
            .mapNotNull { (key, value) ->
                val currencyValue = value.jsonPrimitive.content.toDoubleOrNull()
                currencyValue?.let {
                    key to it
                }
            }
            .toMap()
}