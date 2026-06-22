import SwiftUI

struct AddCurrencySheet: View {
    let availableCurrencies: [String]
    let onAdd: (String) -> Void
    var onDismiss: (() -> Void)? = nil

    @Environment(\.dismiss) private var dismiss
    @State private var query = ""

    private func dismissSheet() {
        onDismiss != nil ? onDismiss!() : dismiss()
    }

    private var filtered: [String] {
        let trimmed = query.trimmingCharacters(in: .whitespaces)
        return trimmed.isEmpty
            ? availableCurrencies
            : availableCurrencies.filter { $0.localizedCaseInsensitiveContains(trimmed) }
    }

    var body: some View {
        ZStack {
            AppColors.surfaceDark.ignoresSafeArea()
            VStack(spacing: 0) {
                header
                searchField
                    .padding(.horizontal, 20)
                    .padding(.bottom, 12)
                Rectangle()
                    .fill(AppColors.dividerColor)
                    .frame(height: 0.5)
                if filtered.isEmpty {
                    Spacer()
                    Text("Nothing was found")
                        .foregroundStyle(AppColors.textSecondary)
                        .font(.system(size: 15))
                    Spacer()
                } else {
                    List(filtered, id: \.self) { currency in
                        CurrencyRowItem(currency: currency) {
                            onAdd(currency)
                            dismissSheet()
                        }
                        .listRowBackground(AppColors.surfaceDark)
                        .listRowInsets(EdgeInsets())
                        .listRowSeparatorTint(AppColors.dividerColor)
                    }
                    .listStyle(.plain)
                    .scrollContentBackground(.hidden)
                }
            }
        }
    }

    private var header: some View {
        HStack {
            Text("Add Currency")
                .foregroundStyle(AppColors.textPrimary)
                .font(.system(size: 18, weight: .medium))
            Spacer()
            Button(action: { dismissSheet() }) {
                Image(systemName: "xmark")
                    .foregroundStyle(AppColors.textSecondary)
                    .font(.system(size: 16, weight: .medium))
            }
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 16)
    }

    private var searchField: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundStyle(AppColors.textSecondary)
            TextField("", text: $query, prompt: Text("Search").foregroundStyle(AppColors.textSecondary))
                .foregroundStyle(AppColors.textPrimary)
                .autocorrectionDisabled()
            if !query.isEmpty {
                Button(action: { query = "" }) {
                    Image(systemName: "xmark")
                        .foregroundStyle(AppColors.textSecondary)
                        .font(.system(size: 13))
                }
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 10)
        .background(AppColors.cardDark)
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .stroke(AppColors.dividerColor, lineWidth: 0.5)
        )
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}

struct CurrencyRowItem: View {
    let currency: String
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 14) {
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(AppColors.goldMuted.opacity(0.25))
                        .frame(width: 40, height: 40)
                    Text(String(currency.prefix(3)))
                        .foregroundStyle(AppColors.gold)
                        .font(.system(size: 11, weight: .medium))
                }
                Text(currency)
                    .foregroundStyle(AppColors.textPrimary)
                    .font(.system(size: 16))
                Spacer()
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 14)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
