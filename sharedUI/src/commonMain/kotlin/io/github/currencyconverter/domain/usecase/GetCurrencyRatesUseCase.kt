package io.github.currencyconverter.domain.usecase

import io.github.currencyconverter.domain.repository.CurrencyRepository

class GetCurrencyRatesUseCase(
    private val currencyRepository: CurrencyRepository
) {

    suspend operator fun invoke(): Result<Map<String, Double>> = currencyRepository.getCurrencyRates()
}