import SwiftUI

@main
struct CurrencyConverterApp: App {
    var body: some Scene {
        WindowGroup {
            MainView(viewModel: DIContainer.shared.makeMainViewModel())
        }
        #if os(macOS)
        .defaultSize(width: 420, height: 720)
        #endif
    }
}
