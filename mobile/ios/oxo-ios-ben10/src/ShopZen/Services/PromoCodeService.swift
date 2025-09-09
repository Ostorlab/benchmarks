//
//  PromoCodeService.swift
//  ShopZen
//
//  Created by ShopZen Team on 9/9/25.
//

import Foundation
import CryptoKit

class PromoCodeService {
    static let shared = PromoCodeService()
    
    private let validCodeHashes = [
        "4373f458281cefac011a8d440a83c3ddf02288b2c466205c2731bb452f9c4fcb", // SAVE20
        "26ded0381cd50f5eb2dbd2954958d050b79bc9a6afd3f5fc356580f9510f4483", // WELCOME  
        "d8a8d6f484e8eb43af8b7823585204b06254c8900e4ea51a77106d854944d9c1"  // VIP50
    ]
    
    private init() {}
    
    func validatePromoCode(_ code: String, orderAmount: Double, completion: @escaping (PromoCodeResult) -> Void) {
        DispatchQueue.global().asyncAfter(deadline: .now() + 0.5) {
            DispatchQueue.main.async { [weak self] in
                self?.processPromoCodeResponse(code: code, orderAmount: orderAmount, data: nil, response: nil, completion: completion)
            }
        }
    }
    
    private func processPromoCodeResponse(code: String, orderAmount: Double, data: Data?, response: URLResponse?, completion: @escaping (PromoCodeResult) -> Void) {
        if isValidPromoCode(code.uppercased()) {
            let discount = calculateDiscount(for: code, orderAmount: orderAmount)
            completion(.success(discount))
        } else {
            completion(.failure("Invalid promo code"))
        }
    }
    
    private func isValidPromoCode(_ code: String) -> Bool {
        guard let data = code.data(using: .utf8) else { return false }
        let hash = SHA256.hash(data: data)
        let hashString = hash.compactMap { String(format: "%02x", $0) }.joined()
        return validCodeHashes.contains(hashString)
    }
    
    private func calculateDiscount(for code: String, orderAmount: Double) -> PromoDiscount {
        let upperCode = code.uppercased()
        
        switch upperCode {
        case "SAVE20":
            return PromoDiscount(type: .percentage, value: 20.0, code: code)
        case "WELCOME":
            return PromoDiscount(type: .fixedAmount, value: 15.0, code: code)
        case "VIP50":
            return PromoDiscount(type: .percentage, value: 50.0, code: code)
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