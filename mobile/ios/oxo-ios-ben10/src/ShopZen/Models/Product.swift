//
//  Product.swift
//  ShopZen
//
//  Created by ShopZen Team on 9/9/25.
//

import Foundation

struct Product {
    let id: String
    let name: String
    let description: String
    let price: Double
    let imageURL: String
    let category: ProductCategory
    let rating: Double
    let reviewCount: Int
    
    var formattedPrice: String {
        return String(format: "$%.2f", price)
    }
}

enum ProductCategory: String, CaseIterable {
    case clothing = "Clothing"
    case accessories = "Accessories" 
    case shoes = "Shoes"
    case electronics = "Electronics"
    case home = "Home & Living"
    
    var displayName: String {
        return self.rawValue
    }
}

struct CartItem {
    let product: Product
    var quantity: Int
    
    var totalPrice: Double {
        return product.price * Double(quantity)
    }
}

struct PromoCode {
    let code: String
    let discountType: DiscountType
    let discountValue: Double
    let isActive: Bool
    let minimumOrderAmount: Double?
    
    enum DiscountType {
        case percentage
        case fixedAmount
    }
}

struct Order {
    let id: String
    let items: [CartItem]
    let subtotal: Double
    let discount: Double
    let tax: Double
    let total: Double
    let promoCode: String?
    let date: Date
}