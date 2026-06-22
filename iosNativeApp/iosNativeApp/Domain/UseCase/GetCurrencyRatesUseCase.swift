final class GetCurrencyRatesUseCase {
    private let repository: CurrencyRepository

    init(repository: CurrencyRepository) {
        self.repository = repository
    }

    func execute() async -> Result<[String: Double], Error> {
        return await repository.getCurrencyRates()
    }
}
