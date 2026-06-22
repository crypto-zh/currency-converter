package io.github.currencyconverter.data.datasource

import io.github.currencyconverter.Constants
import io.github.currencyconverter.data.model.GetCurrencyResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class CurrencyDataSourceImpl(
    private val client: HttpClient
) : CurrencyDataSource {

    override suspend fun getCurrencies(): Result<GetCurrencyResponse> =
        runCatching {
            client.get(
                urlString = "https://api.currencyfreaks.com/v2.0/rates/latest?apikey=${Constants.CURRENCY_API_KEY}"
            ).body<GetCurrencyResponse>()
        }
}