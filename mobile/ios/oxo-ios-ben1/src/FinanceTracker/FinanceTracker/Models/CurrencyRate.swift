//
//  CurrencyRate.swift
//  FinanceTracker
//
//  Created by Alaeddine Mesbahi on 9/4/25.
//

import Foundation

struct CurrencyRate: Codable {
    let baseCurrency: String
    let targetCurrency: String
    let rate: Double
    let lastUpdated: Date
}

struct CurrencyResponse: Codable {
    let success: Bool
    let rates: [String: Double]
    let base: String
    let date: String
}
