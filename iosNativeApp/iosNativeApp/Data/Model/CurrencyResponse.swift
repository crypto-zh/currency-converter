import Foundation

struct CurrencyResponse: Decodable {
    let date: String
    let base: String
    let rates: [String: String]
}
