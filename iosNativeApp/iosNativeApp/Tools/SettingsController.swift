import Foundation

final class SettingsController {
    private static let key = "currencies"

    static func saveCurrencies(_ currencies: Set<String>) {
        UserDefaults.standard.set(currencies.joined(separator: ","), forKey: key)
    }

    static func getCurrencies() -> Set<String> {
        guard let raw = UserDefaults.standard.string(forKey: key), !raw.isEmpty else {
            return []
        }
        return Set(raw.split(separator: ",").map(String.init))
    }
}
