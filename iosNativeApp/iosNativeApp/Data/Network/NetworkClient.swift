import Foundation

protocol NetworkClientProtocol {
    func get<T: Decodable>(url: URL) async throws -> T
}

final class NetworkClient: NetworkClientProtocol {
    private let session: URLSession

    init() {
        let config = URLSessionConfiguration.default
        config.timeoutIntervalForRequest = 10
        session = URLSession(configuration: config)
    }

    func get<T: Decodable>(url: URL) async throws -> T {
        let (data, _) = try await session.data(from: url)
        let decoder = JSONDecoder()
        return try decoder.decode(T.self, from: data)
    }
}
