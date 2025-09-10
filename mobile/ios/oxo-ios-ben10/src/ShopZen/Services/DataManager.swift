//
//  DataManager.swift
//  ShopZen
//
//  Created by ShopZen Team on 9/9/25.
//

import Foundation

class DataManager {
    static let shared = DataManager()
    
    private init() {}
    
    func loadSampleProducts() -> [Product] {
        return [
            Product(
                id: "1",
                name: "Classic Denim Jacket",
                description: "Timeless denim jacket perfect for any season. Made with premium cotton blend for comfort and durability.",
                price: 89.99,
                imageURL: "denim-jacket",
                category: .clothing,
                rating: 4.5,
                reviewCount: 234
            ),
            Product(
                id: "2", 
                name: "Wireless Bluetooth Headphones",
                description: "High-quality wireless headphones with noise cancellation and 20-hour battery life.",
                price: 159.99,
                imageURL: "headphones",
                category: .electronics,
                rating: 4.8,
                reviewCount: 1456
            ),
            Product(
                id: "3",
                name: "Leather Crossbody Bag",
                description: "Elegant leather crossbody bag with adjustable strap. Perfect for daily use or special occasions.",
                price: 129.99,
                imageURL: "crossbody-bag",
                category: .accessories,
                rating: 4.6,
                reviewCount: 678
            ),
            Product(
                id: "4",
                name: "Running Sneakers",
                description: "Lightweight running sneakers with advanced cushioning technology for maximum comfort.",
                price: 119.99,
                imageURL: "running-shoes",
                category: .shoes,
                rating: 4.7,
                reviewCount: 892
            ),
            Product(
                id: "5",
                name: "Minimalist Table Lamp",
                description: "Modern minimalist table lamp with adjustable brightness. Perfect for any workspace or bedroom.",
                price: 69.99,
                imageURL: "table-lamp",
                category: .home,
                rating: 4.4,
                reviewCount: 345
            ),
            Product(
                id: "6",
                name: "Cotton T-Shirt",
                description: "Soft organic cotton t-shirt available in multiple colors. Comfortable fit for everyday wear.",
                price: 24.99,
                imageURL: "cotton-tshirt",
                category: .clothing,
                rating: 4.3,
                reviewCount: 567
            ),
            Product(
                id: "7",
                name: "Smartphone Case",
                description: "Protective smartphone case with shock absorption technology. Available for most popular phone models.",
                price: 29.99,
                imageURL: "phone-case",
                category: .electronics,
                rating: 4.5,
                reviewCount: 789
            ),
            Product(
                id: "8",
                name: "Silk Scarf",
                description: "Luxurious silk scarf with beautiful pattern. Perfect accessory for any outfit.",
                price: 79.99,
                imageURL: "silk-scarf",
                category: .accessories,
                rating: 4.6,
                reviewCount: 234
            )
        ]
    }
    
}