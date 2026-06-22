import SwiftUI

struct MainView: View {
    @StateObject private var viewModel: MainViewModel

    init(viewModel: MainViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    var body: some View {
        ZStack {
            AppColors.backgroundDark.ignoresSafeArea()

            if viewModel.showLoading {
                loadingView
            } else if viewModel.currencies.isEmpty {
                emptyStateView
            } else {
                currencyListView
            }

            addButton

            #if os(macOS)
            if viewModel.showAddSheet {
                Color.black.opacity(0.45)
                    .ignoresSafeArea()
                    .onTapGesture { withAnimation { viewModel.showAddSheet = false } }

                VStack {
                    Spacer()
                    AddCurrencySheet(
                        availableCurrencies: viewModel.availableCurrenciesForAdd,
                        onAdd: { viewModel.onCurrencyAdded($0) },
                        onDismiss: { withAnimation { viewModel.showAddSheet = false } }
                    )
                    .frame(maxWidth: .infinity, maxHeight: 480)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                }
            }
            #endif
        }
        #if os(macOS)
        .animation(.easeInOut(duration: 0.25), value: viewModel.showAddSheet)
        #endif
        #if os(iOS)
        .sheet(isPresented: $viewModel.showAddSheet) {
            AddCurrencySheet(
                availableCurrencies: viewModel.availableCurrenciesForAdd,
                onAdd: { viewModel.onCurrencyAdded($0) }
            )
        }
        #endif
        .alert("Error", isPresented: $viewModel.showError) {
            Button("OK") {}
        } message: {
            Text("Failed to load currency rates. Please check your internet connection.")
        }
    }

    private var loadingView: some View {
        VStack(spacing: 12) {
            ProgressView()
                .tint(AppColors.gold)
            Text("Downloading currency rates...")
                .foregroundStyle(AppColors.textSecondary)
                .font(.system(size: 14))
        }
    }

    private var emptyStateView: some View {
        VStack(spacing: 0) {
            Text("€ $ ¥")
                .font(.system(size: 32))
                .foregroundStyle(AppColors.gold.opacity(0.5))
            Text("No currencies added")
                .foregroundStyle(AppColors.textPrimary)
                .font(.system(size: 18, weight: .medium))
                .padding(.top, 16)
            Text("Press + to add currencies")
                .foregroundStyle(AppColors.textSecondary)
                .font(.system(size: 14))
                .padding(.top, 8)
        }
        .padding(32)
    }

    private var currencyListView: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Currency Converter")
                        .foregroundStyle(AppColors.textPrimary)
                        .font(.system(size: 22, weight: .medium))
                    Text("Enter an amount in any field")
                        .foregroundStyle(AppColors.textSecondary)
                        .font(.system(size: 14))
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.bottom, 8)

                ForEach(viewModel.currencies) { model in
                    CurrencyCard(
                        model: model,
                        onValueChanged: { viewModel.onCurrencyValueChanged($0) },
                        onDelete: { viewModel.onCurrencyDeleted(model.name) }
                    )
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 20)
            .padding(.bottom, 96)
        }
    }

    private var addButton: some View {
        VStack {
            Spacer()
            HStack {
                Spacer()
                Button(action: { viewModel.onCurrencyAddClicked() }) {
                    Image(systemName: "plus")
                        .foregroundStyle(AppColors.contentColor)
                        .font(.system(size: 20, weight: .medium))
                        .frame(width: 56, height: 56)
                        .background(AppColors.gold)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                }
                .padding(.trailing, 20)
                .padding(.bottom, 28)
            }
        }
    }
}

struct CurrencyCard: View {
    let model: CurrencyUIModel
    let onValueChanged: (CurrencyUIModel) -> Void
    let onDelete: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                HStack(spacing: 10) {
                    ZStack {
                        RoundedRectangle(cornerRadius: 10)
                            .fill(AppColors.goldMuted.opacity(0.35))
                            .frame(width: 36, height: 36)
                        Text(String(model.name.prefix(3)))
                            .foregroundStyle(AppColors.gold)
                            .font(.system(size: 11, weight: .medium))
                    }
                    Text(model.name)
                        .foregroundStyle(AppColors.textPrimary)
                        .font(.system(size: 16, weight: .medium))
                        .lineLimit(1)
                }
                Spacer()
                Button(action: onDelete) {
                    Image(systemName: "trash")
                        .foregroundStyle(AppColors.dangerRed.opacity(0.7))
                        .font(.system(size: 16))
                        .frame(width: 32, height: 32)
                }
            }

            Rectangle()
                .fill(AppColors.dividerColor)
                .frame(height: 0.5)
                .padding(.vertical, 12)

            TextField("0.00", text: Binding(
                get: { model.value },
                set: { raw in
                    let filtered = Self.filterInput(raw)
                    onValueChanged(CurrencyUIModel(name: model.name, value: filtered))
                }
            ))
            #if os(iOS)
            .keyboardType(.decimalPad)
            #endif
            .foregroundStyle(AppColors.textPrimary)
            .font(.system(size: 20, weight: .medium))
            .padding(.horizontal, 14)
            .padding(.vertical, 12)
            .background(AppColors.surfaceDark)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(AppColors.dividerColor, lineWidth: 0.5)
            )
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
        .padding(16)
        .background(AppColors.cardDark)
        .clipShape(RoundedRectangle(cornerRadius: 16))
    }

    private static func filterInput(_ raw: String) -> String {
        let replaced = raw.replacingOccurrences(of: ",", with: ".")
        let filtered = replaced.filter { $0.isNumber || $0 == "." }
        if let dotIndex = filtered.firstIndex(of: ".") {
            let afterDot = filtered[filtered.index(after: dotIndex)...].filter { $0.isNumber }
            return String(filtered[...dotIndex]) + afterDot
        }
        return filtered
    }
}
