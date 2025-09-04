import SwiftUI

struct CurrencyConverterView: View {
    @EnvironmentObject var currencyService: CurrencyService
    
    @State private var fromAmount: String = "100"
    @State private var fromCurrency = "USD"
    @State private var toCurrency = "EUR"
    @State private var convertedAmount: Double = 0
    @State private var showingResult = false
    @State private var conversionHistory: [ConversionRecord] = []
    
    private let popularCurrencies = ["USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY"]
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 24) {
                    // Conversion Card
                    ConversionCard(
                        fromAmount: $fromAmount,
                        fromCurrency: $fromCurrency,
                        toCurrency: $toCurrency,
                        convertedAmount: convertedAmount,
                        isLoading: currencyService.isLoading,
                        showingResult: showingResult,
                        onConvert: performConversion,
                        onSwapCurrencies: swapCurrencies
                    )
                    .padding(.horizontal)
                    
                    // Popular Currency Pairs
                    PopularPairsSection(
                        onPairSelected: { from, to in
                            fromCurrency = from
                            toCurrency = to
                        }
                    )
                    .padding(.horizontal)
                    
                    // Conversion History
                    if !conversionHistory.isEmpty {
                        ConversionHistorySection(history: conversionHistory)
                            .padding(.horizontal)
                    }
                    
                    // Exchange Rate Info
                    ExchangeRateInfoSection()
                        .padding(.horizontal)
                }
                .padding(.vertical)
            }
            .navigationTitle("Currency Converter")
            .background(Color(.systemGroupedBackground))
            .onAppear {
                loadConversionHistory()
            }
        }
    }
    
    private func performConversion() {
        guard let amount = Double(fromAmount), amount > 0 else { return }
        
        Task {
            do {
                let result = try await currencyService.convertCurrency(
                    from: fromCurrency,
                    to: toCurrency,
                    amount: amount
                )
                
                await MainActor.run {
                    convertedAmount = result
                    showingResult = true
                    
                    // Add to history
                    let record = ConversionRecord(
                        fromAmount: amount,
                        fromCurrency: fromCurrency,
                        toAmount: result,
                        toCurrency: toCurrency,
                        date: Date()
                    )
                    conversionHistory.insert(record, at: 0)
                    
                    // Keep only last 10 conversions
                    if conversionHistory.count > 10 {
                        conversionHistory = Array(conversionHistory.prefix(10))
                    }
                    
                    saveConversionHistory()
                }
                
            } catch {
                await MainActor.run {
                    showingResult = false
                }
            }
        }
    }
    
    private func swapCurrencies() {
        let temp = fromCurrency
        fromCurrency = toCurrency
        toCurrency = temp
        
        if showingResult {
            fromAmount = String(format: "%.2f", convertedAmount)
            showingResult = false
        }
    }
    
    private func saveConversionHistory() {
        // Save conversion history to UserDefaults
        if let encoded = try? JSONEncoder().encode(conversionHistory) {
            UserDefaults.standard.set(encoded, forKey: "conversionHistory")
        }
    }
    
    private func loadConversionHistory() {
        if let data = UserDefaults.standard.data(forKey: "conversionHistory"),
           let decoded = try? JSONDecoder().decode([ConversionRecord].self, from: data) {
            conversionHistory = decoded
        }
    }
}

struct ConversionCard: View {
    @Binding var fromAmount: String
    @Binding var fromCurrency: String
    @Binding var toCurrency: String
    let convertedAmount: Double
    let isLoading: Bool
    let showingResult: Bool
    let onConvert: () -> Void
    let onSwapCurrencies: () -> Void
    
    private let currencies = ["USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR", "BRL"]
    
    var body: some View {
        VStack(spacing: 20) {
            // From Currency
            VStack(spacing: 8) {
                HStack {
                    Text("From")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Spacer()
                }
                
                HStack(spacing: 12) {
                    Picker("From Currency", selection: $fromCurrency) {
                        ForEach(currencies, id: \.self) { currency in
                            Text(currency).tag(currency)
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                    .frame(width: 80)
                    
                    TextField("Amount", text: $fromAmount)
                        .keyboardType(.decimalPad)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .font(.title2)
                        .fontWeight(.semibold)
                }
            }
            
            // Swap Button
            Button(action: onSwapCurrencies) {
                Image(systemName: "arrow.up.arrow.down.circle.fill")
                    .font(.title2)
                    .foregroundColor(.blue)
                    .background(Color.white)
                    .clipShape(Circle())
            }
            
            // To Currency
            VStack(spacing: 8) {
                HStack {
                    Text("To")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Spacer()
                }
                
                HStack(spacing: 12) {
                    Picker("To Currency", selection: $toCurrency) {
                        ForEach(currencies, id: \.self) { currency in
                            Text(currency).tag(currency)
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                    .frame(width: 80)
                    
                    if showingResult {
                        Text(String(format: "%.2f", convertedAmount))
                            .font(.title2)
                            .fontWeight(.semibold)
                            .foregroundColor(.green)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 8)
                            .background(Color(.systemGray6))
                            .cornerRadius(8)
                    } else {
                        Rectangle()
                            .fill(Color(.systemGray6))
                            .frame(height: 36)
                            .cornerRadius(8)
                            .overlay(
                                Text("Result will appear here")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            )
                    }
                }
            }
            
            // Convert Button
            Button(action: onConvert) {
                HStack {
                    if isLoading {
                        ProgressView()
                            .scaleEffect(0.8)
                        Text("Converting...")
                    } else {
                        Image(systemName: "arrow.triangle.2.circlepath")
                        Text("Convert")
                    }
                }
                .font(.headline)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .cornerRadius(10)
            }
            .disabled(isLoading || fromAmount.isEmpty)
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(16)
        .shadow(color: .black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

struct PopularPairsSection: View {
    let onPairSelected: (String, String) -> Void
    
    private let popularPairs = [
        ("USD", "EUR"), ("EUR", "USD"), ("USD", "GBP"),
        ("GBP", "USD"), ("USD", "JPY"), ("EUR", "GBP")
    ]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Popular Pairs")
                .font(.headline)
                .foregroundColor(.primary)
            
            LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 2), spacing: 8) {
                ForEach(Array(popularPairs.enumerated()), id: \.offset) { index, pair in
                    Button(action: {
                        onPairSelected(pair.0, pair.1)
                    }) {
                        HStack {
                            Text("\(pair.0) → \(pair.1)")
                                .font(.subheadline)
                                .fontWeight(.medium)
                            Spacer()
                            Image(systemName: "arrow.right")
                                .font(.caption)
                        }
                        .foregroundColor(.blue)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(Color.blue.opacity(0.1))
                        .cornerRadius(8)
                    }
                }
            }
        }
    }
}

struct ConversionHistorySection: View {
    let history: [ConversionRecord]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Recent Conversions")
                .font(.headline)
                .foregroundColor(.primary)
            
            LazyVStack(spacing: 8) {
                ForEach(history.prefix(5)) { record in
                    ConversionHistoryRow(record: record)
                }
            }
        }
    }
}

struct ConversionHistoryRow: View {
    let record: ConversionRecord
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text("\(record.fromAmount, specifier: "%.2f") \(record.fromCurrency)")
                    .font(.subheadline)
                    .fontWeight(.medium)
                
                Text(record.date, style: .relative)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Image(systemName: "arrow.right")
                .font(.caption)
                .foregroundColor(.secondary)
            
            VStack(alignment: .trailing, spacing: 2) {
                Text("\(record.toAmount, specifier: "%.2f") \(record.toCurrency)")
                    .font(.subheadline)
                    .fontWeight(.medium)
                
                Text("Rate: \(record.toAmount / record.fromAmount, specifier: "%.4f")")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(8)
        .shadow(color: .black.opacity(0.05), radius: 1, x: 0, y: 1)
    }
}

struct ExchangeRateInfoSection: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Exchange Rate Information")
                .font(.headline)
                .foregroundColor(.primary)
            
            VStack(alignment: .leading, spacing: 8) {
                InfoRow(
                    icon: "clock.fill",
                    title: "Updated",
                    value: "Real-time rates",
                    color: .green
                )
                
                InfoRow(
                    icon: "chart.line.uptrend.xyaxis",
                    title: "Source",
                    value: "FX API Service",
                    color: .blue
                )
                
                InfoRow(
                    icon: "shield.fill",
                    title: "Accuracy",
                    value: "Bank-grade precision",
                    color: .purple
                )
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.05), radius: 2, x: 0, y: 1)
    }
}

struct InfoRow: View {
    let icon: String
    let title: String
    let value: String
    let color: Color
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .foregroundColor(color)
                .frame(width: 20)
            
            Text(title)
                .font(.subheadline)
                .foregroundColor(.primary)
            
            Spacer()
            
            Text(value)
                .font(.subheadline)
                .fontWeight(.medium)
                .foregroundColor(.secondary)
        }
    }
}

struct ConversionRecord: Identifiable, Codable {
    let id = UUID()
    let fromAmount: Double
    let fromCurrency: String
    let toAmount: Double
    let toCurrency: String
    let date: Date
}

#Preview {
    CurrencyConverterView()
        .environmentObject(CurrencyService())
}
