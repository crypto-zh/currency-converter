protocol CurrencyDataSource {
    func getCurrencies() async -> Result<CurrencyResponse, Error>
}
