//
//  Investment.swift
//  WealthPilot
//
//  Created by Ostorlab Ostorlab on 9/10/25.
//

import Foundation

struct Investment {
    let symbol: String
    let name: String
    let shares: Double
    let currentPrice: Double
    let purchasePrice: Double
    let type: InvestmentType
    
    var currentValue: Double {
        return shares * currentPrice
    }
    
    var gainLoss: Double {
        return (currentPrice - purchasePrice) * shares
    }
    
    var gainLossPercentage: Double {
        return ((currentPrice - purchasePrice) / purchasePrice) * 100
    }
}

enum InvestmentType: String, CaseIterable {
    case stock = "Stock"
    case etf = "ETF"
    case bond = "Bond"
    case crypto = "Crypto"
    case reit = "REIT"
}