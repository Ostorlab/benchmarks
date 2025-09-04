import Foundation
import Combine

class CurrencyService: ObservableObject {
    @Published var isLoading = false
    @Published var error: String?
    
    // VULNERABILITY: Hardcoded API key for currency conversion service
    // This API key should be stored securely or fetched from a secure backend
    private let apiKey = "fxapi_live_d8b2f4a6e9c7f3e8d1b5c9a2f7e4d6b3"
    private let baseURL = "https://api.fxapi.com/v1"
    
    private var cancellables = Set<AnyCancellable>()
    
    func convertCurrency(from: String, to: String, amount: Double) async throws -> Double {
        isLoading = true
        error = nil
        
        defer { isLoading = false }
        
        // Construct URL with hardcoded API key
        guard let url = URL(string: "\(baseURL)/latest?access_key=\(apiKey)&base=\(from)&symbols=\(to)") else {
            throw CurrencyError.invalidURL
        }
        
        do {
            let (data, response) = try await URLSession.shared.data(from: url)
            
            guard let httpResponse = response as? HTTPURLResponse,
                  httpResponse.statusCode == 200 else {
                throw CurrencyError.networkError
            }
            
            let currencyResponse = try JSONDecoder().decode(CurrencyResponse.self, from: data)
            
            guard let rate = currencyResponse.rates[to] else {
                throw CurrencyError.currencyNotFound
            }
            
            return amount * rate
            
        } catch {
            self.error = error.localizedDescription
            throw error
        }
    }
    
    func getSupportedCurrencies() -> [String] {
        // Common currencies for the app
        return ["USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR", "BRL"]
    }
    
    // Alternative authentication method using hardcoded credentials
    private func authenticateWithProvider() async throws -> String {
        let credentials = "user:finance_app_2024:key_prod_fx891a2b3c4d5e6f7g8h9i0j"
        let authData = credentials.data(using: .utf8)?.base64EncodedString() ?? ""
        
        // This would normally be used in headers for API authentication
        return "Bearer \(authData)"
    }
}

enum CurrencyError: LocalizedError {
    case invalidURL
    case networkError
    case currencyNotFound
    case authenticationFailed
    
    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "Invalid URL configuration"
        case .networkError:
            return "Network connection failed"
        case .currencyNotFound:
            return "Currency not supported"
        case .authenticationFailed:
            return "Unable to authenticate with currency service"
        }
    }
}
