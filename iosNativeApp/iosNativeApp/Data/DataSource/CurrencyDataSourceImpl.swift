import Foundation

final class CurrencyDataSourceImpl: CurrencyDataSource {
    private let networkClient: NetworkClientProtocol

    init(networkClient: NetworkClientProtocol) {
        self.networkClient = networkClient
    }

    func getCurrencies() async -> Result<CurrencyResponse, Error> {
        guard let url = URL(string: "\(Constants.baseURL)?apikey=\(Constants.currencyApiKey)") else {
            return .failure(URLError(.badURL))
        }
        do {
            let response: CurrencyResponse = try await networkClient.get(url: url)
            return .success(response)
        } catch {
            return .failure(error)
        }
    }
}
