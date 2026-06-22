final class DIContainer {
    static let shared = DIContainer()

    private init() {}

    private lazy var networkClient: NetworkClientProtocol = NetworkClient()
    private lazy var currencyDataSource: CurrencyDataSource = CurrencyDataSourceImpl(networkClient: networkClient)
    private lazy var currencyRepository: CurrencyRepository = CurrencyRepositoryImpl(dataSource: currencyDataSource)
    private lazy var getCurrencyRatesUseCase = GetCurrencyRatesUseCase(repository: currencyRepository)

    @MainActor
    func makeMainViewModel() -> MainViewModel {
        MainViewModel(getCurrencyRatesUseCase: getCurrencyRatesUseCase)
    }
}
