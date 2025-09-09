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

**Brief Explanation**: ShopZen processes promotional code validation requests without implementing rate limiting, account lockout mechanisms, or request throttling. This allows attackers to systematically test promotional codes using automated tools until valid codes are discovered.

## Exploitation

### Attack Scenarios

**1. Manual Pattern Testing**
- Access shopping cart with items
- Enter predictable promotional codes (SAVE10, WELCOME, VIP40, etc.)
- No rate limiting allows unlimited attempts

**2. Automated Brute Force**
- Use mobile automation tools (Appium, Frida)
- Script systematic testing of common promotional code patterns
- No authentication required for code validation attempts

**3. Common Valid Codes**
```
SAVE10, SAVE20, WELCOME, NEWUSER, STUDENT15, 
WEEKEND25, FLASH30, SUMMER20, AUTUMN15, WINTER10, 
SPRING25, HOLIDAY50, VIP40, FIRST10, RETURN20
```

### Testing Instructions

1. **Build and run the app** in iOS Simulator
2. **Add products to cart** from the main product list
3. **Navigate to shopping cart** 
4. **Test promotional codes** in the promo code field:
   - Try invalid codes: no rate limiting prevents rapid attempts
   - Try valid codes from the list above to confirm discounts apply
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
- **Predictable code patterns** aid enumeration attacks
