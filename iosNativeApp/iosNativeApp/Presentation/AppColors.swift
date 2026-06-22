import SwiftUI

enum AppColors {
    static let backgroundDark = Color(hex: "0F1117")
    static let surfaceDark    = Color(hex: "1A1D27")
    static let contentColor   = Color(hex: "1A1400")
    static let cardDark       = Color(hex: "222636")
    static let gold           = Color(hex: "F0C040")
    static let goldMuted      = Color(hex: "7A6020")
    static let textPrimary    = Color(hex: "F0EFE8")
    static let textSecondary  = Color(hex: "888EA8")
    static let dividerColor   = Color(hex: "2C3050")
    static let dangerRed      = Color(hex: "E05050")
}

extension Color {
    init(hex: String) {
        var rgb: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&rgb)
        self.init(
            red:   Double((rgb >> 16) & 0xFF) / 255,
            green: Double((rgb >> 8)  & 0xFF) / 255,
            blue:  Double( rgb        & 0xFF) / 255
        )
    }
}
