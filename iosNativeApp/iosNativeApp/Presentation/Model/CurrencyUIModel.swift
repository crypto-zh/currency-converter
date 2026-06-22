struct CurrencyUIModel: Identifiable, Equatable {
    let id: String
    var value: String

    init(name: String, value: String) {
        self.id = name
        self.value = value
    }

    var name: String { id }
}
