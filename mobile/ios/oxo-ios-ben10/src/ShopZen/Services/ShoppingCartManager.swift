//
//  ShoppingCartManager.swift
//  ShopZen
//
//  Created by ShopZen Team on 9/9/25.
//

import Foundation

class ShoppingCartManager: ObservableObject {
    static let shared = ShoppingCartManager()
    
    @Published var items: [CartItem] = []
    @Published var appliedPromoCode: String?
    @Published var discount: Double = 0.0
    
    private init() {}
    
    var subtotal: Double {
        return items.reduce(0) { $0 + $1.totalPrice }
    }
    
    var tax: Double {
        let taxableAmount = subtotal - discount
        return max(0, taxableAmount * 0.08875) // NY tax rate
    }
    
    var total: Double {
        return max(0, subtotal - discount + tax)
    }
    
    var itemCount: Int {
        return items.reduce(0) { $0 + $1.quantity }
    }
    
    func addItem(product: Product, quantity: Int = 1) {
        if let existingIndex = items.firstIndex(where: { $0.product.id == product.id }) {
            items[existingIndex].quantity += quantity
        } else {
            items.append(CartItem(product: product, quantity: quantity))
        }
    }
    
    func removeItem(at index: Int) {
        guard index < items.count else { return }
        items.remove(at: index)
    }
    
    func updateQuantity(for productId: String, quantity: Int) {
        guard let index = items.firstIndex(where: { $0.product.id == productId }) else { return }
        
        if quantity <= 0 {
            items.remove(at: index)
        } else {
            items[index].quantity = quantity
        }
    }
    
    func clearCart() {
        items.removeAll()
        appliedPromoCode = nil
        discount = 0.0
    }
    
    func applyPromoCode(_ code: String, completion: @escaping (Bool, String) -> Void) {
        PromoCodeService.shared.validatePromoCode(code, orderAmount: subtotal) { [weak self] result in
            switch result {
            case .success(let promoDiscount):
                self?.appliedPromoCode = code
                self?.discount = promoDiscount.calculateDiscount(for: self?.subtotal ?? 0)
                completion(true, "Promo code applied successfully!")
                
            case .failure(let error):
                completion(false, error)
            }
        }
    }
    
    func removePromoCode() {
        appliedPromoCode = nil
        discount = 0.0
    }
}