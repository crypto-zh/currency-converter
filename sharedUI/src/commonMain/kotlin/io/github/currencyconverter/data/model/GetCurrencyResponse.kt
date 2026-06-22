package io.github.currencyconverter.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GetCurrencyResponse(
    @SerialName("date")
    val date: String,
    @SerialName("base")
    val base: String,
    @SerialName("rates")
    val rates: JsonObject
)