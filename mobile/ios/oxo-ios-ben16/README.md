# oxo-ios-ben16: iOS Login Brute Force

## Challenge Details

### Description

WealthPilot is a professional investment portfolio management iOS application that demonstrates login brute force vulnerabilities through missing rate limiting controls. The app allows users to authenticate with email/password credentials but fails to implement proper attempt restrictions or account lockout mechanisms.

The vulnerability occurs when attackers systematically attempt credential combinations against the authentication system without being blocked, allowing unlimited login attempts until valid credentials are discovered.

### Vulnerability Type and Category
- **Type:** Login Brute Force / Missing Rate Limiting
- **Category:** Improper Restriction of Excessive Authentication Attempts (CWE-307)

### Difficulty
Easy to Medium

## Technical Details

### Vulnerable Code Location
- **File:** `AuthenticationService.swift:22`
- **Method:** `authenticateUser(email:password:completion:)`
- **Vulnerable line:** No rate limiting, attempt counting, or delays implemented

### Attack Vectors

**Password Spraying:**
1. **Common Passwords:** Test frequently used passwords against known email addresses
2. **Credential Stuffing:** Use leaked credential combinations from data breaches
3. **Targeted Enumeration:** Systematic testing with unlimited attempts per account

**Exploitation Method:**

1. **Identify target emails** (alex.morgan@wealthpilot.com, sarah.chen@investor.com, etc.)
2. **Attempt common passwords** systematically (password, 123456, admin, etc.)
3. **No lockout occurs** - unlimited attempts allowed
4. **Eventually discover** valid credentials through systematic enumeration

**Valid Credentials:**
- alex.morgan@wealthpilot.com / wealth2024
- sarah.chen@investor.com / invest123  
- michael.rodriguez@finance.net / portfolio

### Impact
- Complete account takeover with full access to investment portfolio data
- Financial data exposure including holdings, balances, and performance metrics
- Identity theft through personal financial information access
- Regulatory compliance violations for financial data protection

## Real-World Attack Scenario

**Attack:** Systematic password spraying against alex.morgan@wealthpilot.com
**Method:** Automated testing of common passwords (password, 123456, admin, wealth2024...)
**Result:** No rate limiting allows unlimited attempts until "wealth2024" succeeds
**Outcome:** Complete access to $675,000 investment portfolio and sensitive financial data

The vulnerability enables attackers to gain unauthorized access to users' complete financial portfolios through systematic credential enumeration, bypassing authentication through persistence rather than sophistication.