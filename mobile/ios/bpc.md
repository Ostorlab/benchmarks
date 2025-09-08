# iOS Security Benchmark Development Prompt - Brute Force Promotion Codes

## Project Overview

You are tasked with developing a realistic iOS security benchmark application that demonstrates a brute force promotion codes vulnerability. This app will be used for defensive security research and testing purposes.

## Context and Purpose

We are creating security benchmark applications that simulate real-world vulnerabilities for educational and defensive security analysis. These benchmarks help security researchers, penetration testers, and developers understand common iOS security flaws in realistic application contexts.

The goal is to create a production-quality iOS application with intentional security vulnerabilities that can be discovered and exploited through standard security testing methodologies.

## Target Vulnerability

**iOS Brute Force Promotion Codes (CWE-307, CWE-521)** - where the application fails to properly implement rate limiting, account lockout, or secure validation mechanisms for promotional codes, discount codes, or coupon systems, allowing attackers to enumerate valid codes through automated brute force attacks.

**Reference Examples:**
- Lack of rate limiting on promo code validation endpoints
- Insufficient complexity requirements for promotional codes
- No account lockout mechanisms after failed attempts
- Client-side validation that can be bypassed
- Predictable or sequential promotion code generation patterns

## Research Requirements (MANDATORY - Complete Before Implementation)

### 1. Existing Benchmark Analysis
- **Read ALL existing iOS benchmark README files** in the repository to understand:
  - Documentation standards and formatting requirements
  - Vulnerability implementation approaches
  - Professional presentation requirements
  - Naming conventions and project structure

### 2. Cross-Platform Consistency Study  
- **Read 4 representative Android benchmark README files** to understand:
  - Cross-platform benchmark consistency standards
  - Quality benchmarks and documentation depth
  - Technical implementation patterns

### 3. Real-World Vulnerability Research
Investigate the following areas:
- **Promotion Code Attack Vectors**: Study common brute force techniques against discount systems
- **iOS Rate Limiting Failures**: Research iOS apps that failed to implement proper rate limiting
- **E-commerce Security Patterns**: Understand promotional code validation in mobile commerce
- **Authentication Bypass Techniques**: Methods used to circumvent promo code restrictions

### 4. iOS Promotion Code Security Research
Study these technical areas:
- **iOS Input Validation Patterns**: Common validation failures in promotional systems
- **Network Request Rate Limiting**: iOS-specific approaches to rate limiting and their failures
- **Client-Server Validation**: Security implications of client-side vs server-side validation
- **Code Generation Patterns**: Insecure promotion code generation and validation methods

## Application Requirements

### App Concept Selection
Choose an appropriate app type that naturally handles promotion codes:
- **E-commerce/Shopping app** with discount codes
- **Food delivery app** with promo codes and coupons
- **Streaming service** with subscription discount codes
- **Gaming app** with in-app purchase promo codes
- **Retail loyalty app** with reward codes
- **Subscription service** with promotional trial codes

**Requirements:**
- Must appear as a **legitimate, production-quality application**
- Clean, native iOS design following **Human Interface Guidelines**
- Realistic promotion code workflows and user interactions
- Professional commerce features and discount application systems
- Natural integration of promo code functionality that justifies brute force vulnerabilities

### Vulnerability Integration Requirements

**Implementation Standards:**
- Implement flawed rate limiting that allows brute force attacks on promotion codes
- Make the vulnerability discoverable through standard security testing techniques
- Ensure the flaw demonstrates **real security impact** (unauthorized discounts, financial loss, code enumeration)
- Provide **clear exploitation path** that security researchers can follow
- Include multiple attack vectors and validation bypass scenarios

**Realistic Integration:**
- Vulnerability must feel natural within the app's intended functionality
- Should not appear as obviously intentional security flaws
- Must be discoverable through normal penetration testing methodology
- Include both simple and complex exploitation scenarios
- Demonstrate realistic business impact of promotion code abuse

### Technical Implementation Standards

**Development Requirements:**
- **Native Swift/UIKit implementation** (no hybrid frameworks)
- **iOS 15.0+ compatibility**
- Professional code quality with intentional rate limiting and validation security gaps
- Realistic error handling and professional user experience
- Integration with iOS networking APIs and form validation patterns
- Follow iOS development best practices except for intentional vulnerabilities

**Security Flaw Integration:**
- **No rate limiting** on promotion code validation requests
- **Weak validation logic** that can be bypassed or exploited
- **Predictable code generation patterns** that aid enumeration
- **Client-side validation** that can be manipulated
- **Insufficient error handling** that reveals valid vs invalid codes
- **No account lockout** mechanisms after multiple failed attempts

### User Interface Requirements

**Design Standards:**
- Follow **Human Interface Guidelines** strictly
- Professional visual design with consistent styling
- Intuitive navigation and user flows for commerce applications
- Realistic app iconography and branding for e-commerce/retail
- Production-quality animations and transitions
- Professional checkout and discount application flows

**Functionality Requirements:**
- Working core e-commerce or service features
- Realistic product catalog or service offerings
- Professional cart/checkout system with discount application
- Authentic promotion code entry and validation workflows
- Native iOS integration (payments, notifications, etc.)
- Realistic user account management and purchase history

## Expected Deliverables

### 1. Implementation Plan
Before coding, provide:
- **Specific app concept** with justification for promotion code requirements
- **Technical architecture** overview including vulnerable validation components
- **Vulnerability implementation strategy** with specific brute force attack vectors
- **Professional UI/UX specifications** that support commerce workflows
- **Promotion code system design** with intentional security flaws

### 2. Vulnerability Documentation
- **Exploitation methodology** with step-by-step brute force procedures
- **Security impact demonstration** showing financial impact and code enumeration
- **Testing procedures** for validating vulnerability effectiveness with automated tools
- **Multiple attack scenarios** including manual and automated brute force techniques

### 3. Professional Documentation
- **README file** following established benchmark patterns
- **Technical specifications** and architecture overview
- **Installation and testing instructions**
- **Educational content** explaining the vulnerability and its prevention

### 4. Complete Application
- **Full iOS application** meeting all technical and design requirements
- **Working e-commerce/service features** with intentional promotion code security flaws
- **Professional user interface** indistinguishable from legitimate commerce apps
- **Comprehensive vulnerability integration** with multiple brute force exploitation paths

## Quality Standards

### Realism Requirements
- Application must be **indistinguishable from legitimate App Store commerce applications**
- All features must work as expected by end users
- Professional branding, copy, and user experience typical of e-commerce apps
- Realistic product/service data and pricing structures
- Natural integration of vulnerable promotion code functionality

### Security Research Value
- Vulnerability must demonstrate **real-world financial security risks**
- Clear educational value for understanding e-commerce security failures
- Multiple brute force techniques and automation possibilities
- Realistic impact scenarios that mirror actual financial security incidents
- Discoverable through standard penetration testing tools and automated scanners

### Technical Excellence
- **Production-quality code** with intentional validation and rate limiting gaps
- Professional iOS development patterns and practices
- Comprehensive error handling and edge case management (except in vulnerable areas)
- Optimal performance and resource utilization
- Proper iOS ecosystem integration including payment frameworks

## Success Criteria

The final application should:
1. **Pass as a legitimate production e-commerce application** to casual users
2. **Contain discoverable promotion code brute force vulnerabilities** for security researchers
3. **Demonstrate real financial security impact** through unauthorized discount abuse
4. **Provide educational value** for understanding iOS commerce security
5. **Follow professional development standards** while containing intentional rate limiting flaws
6. **Include comprehensive documentation** following established benchmark patterns
7. **Support automated security testing** with clear brute force exploitation paths

## Specific Vulnerability Focus Areas

### Rate Limiting Failures
- No request throttling on promotion code validation endpoints
- Missing exponential backoff mechanisms
- Absence of IP-based or device-based rate limiting
- No session-based attempt tracking

### Validation Weaknesses  
- Predictable promotion code patterns (sequential, date-based, simple algorithms)
- Client-side validation that can be bypassed
- Insufficient server-side verification
- Error messages that reveal valid vs invalid code patterns

### Authentication Bypass
- Missing user authentication requirements for code validation
- Session management failures that allow unlimited attempts
- Device identification bypass possibilities
- Account lockout mechanism absence

Focus on creating a benchmark that effectively teaches iOS promotion code security concepts while maintaining the realism that makes these tools valuable for security education and demonstrating the risks of inadequate rate limiting and validation in mobile commerce applications.