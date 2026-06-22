final class CurrencyRepositoryImpl: CurrencyRepository {
    private let dataSource: CurrencyDataSource

    init(dataSource: CurrencyDataSource) {
        self.dataSource = dataSource
    }

    func getCurrencyRates() async -> Result<[String: Double], Error> {
        return await dataSource.getCurrencies().map { response in
            response.rates.compactMapValues { Double($0) }
        }
    }
}
