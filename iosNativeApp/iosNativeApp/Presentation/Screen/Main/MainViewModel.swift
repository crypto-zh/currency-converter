import Foundation

@MainActor
final class MainViewModel: ObservableObject {
    @Published var currencies: [CurrencyUIModel] = []
    @Published var showLoading = false
    @Published var showAddSheet = false
    @Published var availableCurrenciesForAdd: [String] = []
    @Published var showError = false

    private var ratesMap: [String: Double] = [:]
    private var lastEditedCurrency: String? = nil
    private var lastEditedValue: Double = 0.0

    private let getCurrencyRatesUseCase: GetCurrencyRatesUseCase

    init(getCurrencyRatesUseCase: GetCurrencyRatesUseCase) {
        self.getCurrencyRatesUseCase = getCurrencyRatesUseCase
        self.currencies = SettingsController.getCurrencies().map { CurrencyUIModel(name: $0, value: "") }
        Task { await loadRates() }
    }

    func onCurrencyAddClicked() {
        let currentNames = Set(currencies.map { $0.name })
        availableCurrenciesForAdd = ratesMap.keys
            .filter { !currentNames.contains($0) }
            .sorted()
        showAddSheet = true
    }

    func onCurrencyValueChanged(_ model: CurrencyUIModel) {
        guard let inputValue = Double(model.value) else {
            currencies = currencies.map { CurrencyUIModel(name: $0.name, value: "") }
            lastEditedCurrency = nil
            lastEditedValue = 0
            return
        }

        lastEditedCurrency = model.name
        lastEditedValue = inputValue

        guard let rateOfEdited = ratesMap[model.name] else { return }
        let amountInUsd = inputValue / rateOfEdited

        currencies = currencies.map { currency in
            if currency.name == model.name {
                return CurrencyUIModel(name: currency.name, value: model.value)
            } else if let targetRate = ratesMap[currency.name] {
                return CurrencyUIModel(name: currency.name, value: formatValue(amountInUsd * targetRate))
            } else {
                return CurrencyUIModel(name: currency.name, value: "")
            }
        }
    }

    func onCurrencyAdded(_ currency: String) {
        guard !currencies.contains(where: { $0.name == currency }) else { return }
        let newCurrency = CurrencyUIModel(name: currency, value: computeValueForCurrency(currency))
        currencies.append(newCurrency)
        SettingsController.saveCurrencies(Set(currencies.map { $0.name }))
    }

    func onCurrencyDeleted(_ currency: String) {
        currencies.removeAll { $0.name == currency }
        SettingsController.saveCurrencies(Set(currencies.map { $0.name }))
    }

    private func loadRates() async {
        showLoading = true
        let result = await getCurrencyRatesUseCase.execute()
        showLoading = false
        switch result {
        case .success(let rates):
            ratesMap = rates
        case .failure:
            showError = true
        }
    }

    private func computeValueForCurrency(_ currency: String) -> String {
        guard let editedCurrency = lastEditedCurrency,
              let rateOfEdited = ratesMap[editedCurrency],
              let targetRate = ratesMap[currency] else { return "" }
        return formatValue((lastEditedValue / rateOfEdited) * targetRate)
    }

    private func formatValue(_ value: Double) -> String {
        if value >= 1.0 {
            let rounded = Int64(value * 100)
            let intPart = rounded / 100
            let fracPart = rounded % 100
            return "\(intPart).\(String(format: "%02d", fracPart))"
        } else {
            let scaled = Int64(value * 100_000_000)
            var str = String(scaled)
            while str.count < 8 { str = "0" + str }
            while str.hasSuffix("0") { str.removeLast() }
            return str.isEmpty ? "0" : "0.\(str)"
        }
    }
}
