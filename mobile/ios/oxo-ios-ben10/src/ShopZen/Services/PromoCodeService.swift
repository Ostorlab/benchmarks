//
//  PromoCodeService.swift
//  ShopZen
//
//  Created by ShopZen Team on 9/9/25.
//

import Foundation

class PromoCodeService {
    static let shared = PromoCodeService()
    
    private let baseURL = "https://api.shopzen.com/v1"
    private let validCodes = DataManager.shared.loadValidPromoCodes()
    
    private init() {}
    
    func validatePromoCode(_ code: String, orderAmount: Double, completion: @escaping (PromoCodeResult) -> Void) {
        DispatchQueue.global().asyncAfter(deadline: .now() + 0.5) {
            DispatchQueue.main.async { [weak self] in
                self?.processPromoCodeResponse(code: code, orderAmount: orderAmount, data: nil, response: nil, completion: completion)
            }
        }
    }
    
    private func processPromoCodeResponse(code: String, orderAmount: Double, data: Data?, response: URLResponse?, completion: @escaping (PromoCodeResult) -> Void) {
        if validCodes.contains(code.uppercased()) {
            let discount = calculateDiscount(for: code, orderAmount: orderAmount)
            completion(.success(discount))
        } else {
            completion(.failure("Invalid promo code"))
        }
    }
    
    private func calculateDiscount(for code: String, orderAmount: Double) -> PromoDiscount {
        let upperCode = code.uppercased()
        
        switch upperCode {
        case "SAVE10", "WINTER10", "FIRST10":
            return PromoDiscount(type: .percentage, value: 10.0, code: code)
        case "STUDENT15", "AUTUMN15":
            return PromoDiscount(type: .percentage, value: 15.0, code: code)
        case "SAVE20", "SUMMER20", "RETURN20":
            return PromoDiscount(type: .percentage, value: 20.0, code: code)
        case "WEEKEND25", "SPRING25":
            return PromoDiscount(type: .percentage, value: 25.0, code: code)
        case "FLASH30":
            return PromoDiscount(type: .percentage, value: 30.0, code: code)
        case "VIP40":
            return PromoDiscount(type: .percentage, value: 40.0, code: code)
        case "HOLIDAY50":
            return PromoDiscount(type: .percentage, value: 50.0, code: code)
        case "WELCOME", "NEWUSER":
            return PromoDiscount(type: .fixedAmount, value: 15.0, code: code)
        default:
            return PromoDiscount(type: .percentage, value: 10.0, code: code)
        }
    }
}

enum PromoCodeResult {
    case success(PromoDiscount)
    case failure(String)
}

struct PromoDiscount {
    let type: DiscountType
    let value: Double
    let code: String
    
    enum DiscountType {
        case percentage
        case fixedAmount
    }
    
    func calculateDiscount(for amount: Double) -> Double {
        switch type {
        case .percentage:
            return amount * (value / 100.0)
        case .fixedAmount:
            return min(value, amount)
        }
    }
}