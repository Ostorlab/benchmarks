# oxo-ios-ben10: iOS Brute Force Promotion Codes

## Challenge Details

### Description

ShopZen is a modern iOS e-commerce application that provides users with product browsing, shopping cart functionality, and promotional discount features. The app demonstrates a critical **Brute Force Promotion Codes** vulnerability where the promo code validation system fails to implement proper rate limiting and security controls.

The application includes:
- Product catalog with realistic e-commerce items
- Shopping cart with checkout functionality  
- Promotional code system with discount application
- Professional iOS interface following Human Interface Guidelines

### Vulnerability Type and Category
- **Type:** Brute Force Promotion Codes
- **Category:** Insufficient Rate Limiting / Weak Authentication (CWE-307, CWE-521)
- **Platform:** iOS (Swift/UIKit)

### Difficulty
Medium

## Vulnerability Overview

The application's promotional code validation system lacks essential security controls, allowing attackers to enumerate valid promotional codes through automated brute force attacks. The vulnerability affects the `PromoCodeService.swift` component where promo code validation occurs without rate limiting or attempt tracking.

**Attack Vector: Automated Promotion Code Enumeration**

**Brief Explanation**: ShopZen processes promotional code validation requests without implementing rate limiting, account lockout mechanisms, or request throttling. The app uses SHA256 hashes to validate codes, preventing static analysis discovery but forcing attackers to systematically brute force codes through dynamic testing.


### Testing Instructions

1. **Build and run the app** in iOS Simulator
2. **Add products to cart** from the main product list
3. **Navigate to shopping cart** 
4. **Test promotional codes** in the promo code field:
   - Try invalid codes: no rate limiting prevents rapid attempts  
   - Try common patterns to discover valid codes (SAVE20, WELCOME, VIP50 are valid)
5. **Demonstrate automation potential** using mobile testing frameworks

**Expected Result:** Successfully apply promotional discounts through brute force enumeration without encountering rate limiting or account lockout mechanisms.

## Technical Details

### Vulnerable Code Locations

**Primary Vulnerability - PromoCodeService.swift:18-24**
```swift
func validatePromoCode(_ code: String, orderAmount: Double, completion: @escaping (PromoCodeResult) -> Void) {
    // VULNERABLE: No rate limiting, no attempt tracking
    DispatchQueue.global().asyncAfter(deadline: .now() + 0.5) {
        DispatchQueue.main.async { [weak self] in
            self?.processPromoCodeResponse(code: code, orderAmount: orderAmount, data: nil, response: nil, completion: completion)
        }
    }
}
```

### Security Failures
- **No rate limiting** on promotional code validation requests
- **No attempt tracking** per user session or device
- **No account lockout** after multiple failed attempts  
- **No authentication required** for code validation
- **Hash-based validation** prevents static analysis shortcuts but enables brute force attacks
