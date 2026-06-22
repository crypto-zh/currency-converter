protocol CurrencyRepository {
    func getCurrencyRates() async -> Result<[String: Double], Error>
}
