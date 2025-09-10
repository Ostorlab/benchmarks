//
//  Portfolio.swift
//  WealthPilot
//
//  Created by Ostorlab Ostorlab on 9/10/25.
//

import Foundation

struct Portfolio {
    let accountName: String
    let accountNumber: String
    let investments: [Investment]
    
    var totalValue: Double {
        return investments.reduce(0) { $0 + $1.currentValue }
    }
    
    var totalGainLoss: Double {
        return investments.reduce(0) { $0 + $1.gainLoss }
    }
    
    var totalGainLossPercentage: Double {
        let totalCost = investments.reduce(0) { $0 + ($1.purchasePrice * $1.shares) }
        return totalCost > 0 ? (totalGainLoss / totalCost) * 100 : 0
    }
}

class PortfolioManager {
    static let shared = PortfolioManager()
    
    private init() {}
    
    lazy var samplePortfolio: Portfolio = {
        let investments = [
            Investment(symbol: "AAPL", name: "Apple Inc.", shares: 50, currentPrice: 175.23, purchasePrice: 145.60, type: .stock),
            Investment(symbol: "GOOGL", name: "Alphabet Inc.", shares: 25, currentPrice: 138.45, purchasePrice: 125.30, type: .stock),
            Investment(symbol: "VTI", name: "Vanguard Total Stock Market ETF", shares: 100, currentPrice: 235.67, purchasePrice: 220.15, type: .etf),
            Investment(symbol: "BTC-USD", name: "Bitcoin", shares: 0.5, currentPrice: 43250.00, purchasePrice: 35800.00, type: .crypto),
            Investment(symbol: "VNQ", name: "Vanguard Real Estate ETF", shares: 75, currentPrice: 87.32, purchasePrice: 82.50, type: .reit),
            Investment(symbol: "TLT", name: "iShares 20+ Year Treasury Bond ETF", shares: 30, currentPrice: 92.15, purchasePrice: 98.20, type: .bond)
        ]
        
        return Portfolio(
            accountName: "WealthPilot Premium Account",
            accountNumber: "WP-2024-789456",
            investments: investments
        )
    }()
}