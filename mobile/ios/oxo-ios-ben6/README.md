# oxo-ios-ben6 WeatherNow - iOS TLS Certificate Validation Bypass

## Challenge Details

### Description
WeatherNow is a clean and modern iOS weather application that provides real-time weather information, forecasts, and weather alerts for locations worldwide. The app features an intuitive interface with beautiful weather animations and detailed meteorological data. However, the application contains a critical security flaw in its SSL/TLS certificate validation implementation, allowing man-in-the-middle attacks and potential interception of API communications.

### Vulnerability Type and Category
- **Type:** Improper Certificate Validation
- **Category:** Cryptographic Issues / Man-in-the-Middle

### Weakness (CWE)
- **CWE-295:** Improper Certificate Validation
- **CWE-319:** Cleartext Transmission of Sensitive Information
- **CWE-940:** Improper Verification of Source of a Communication Channel

### Platform
iOS (Swift/SwiftUI)

### Difficulty
Medium

## Vulnerability Overview
The WeatherNow application implements a custom URLSessionDelegate that bypasses standard SSL/TLS certificate validation checks when communicating with weather API endpoints. The app accepts any certificate presented by the server, including self-signed, expired, or certificates with mismatched hostnames. This vulnerability allows attackers to intercept HTTPS communications and potentially serve malicious weather data or capture location information.

## Exploitation Vectors
1. **API Interception:** Weather API calls can be intercepted with invalid certificates
2. **Data Manipulation:** Malicious weather data can be injected into the app
3. **Location Tracking:** User location requests can be captured on compromised networks
4. **Network Spoofing:** Fraudulent weather services can impersonate legitimate APIs

## Build Instructions
This is a standard Xcode project. Open `WeatherNow.xcodeproj` and build for the iOS Simulator or a physical device.

1. **Run the app** in the simulator (`Cmd + R`).
2. **Set up a proxy** like Charles Proxy or Burp Suite with invalid/self-signed certificates.
3. **Configure device** to route traffic through the proxy.
4. **Use the weather app** to fetch weather data for different locations.
5. **Observe** that the app accepts invalid certificates and retrieves data.

## Proof of Concept
1. Launch WeatherNow app
2. Set up network proxy with self-signed certificate
3. Configure iOS device to use the proxy
4. Search for weather in various cities
5. The app will accept the invalid certificate and display weather data
6. API communications will be transmitted through the compromised connection

## Impact
- **Data Integrity Loss:** Weather information can be manipulated
- **Location Privacy:** User location data can be intercepted
- **Service Disruption:** Malicious weather alerts could be injected
- **Network Trust Compromise:** Opens door for broader network attacks

## Remediation
- Remove custom certificate validation bypass logic
- Implement proper SSL certificate pinning for weather APIs
- Use URLSession with default certificate validation
- Add certificate transparency verification
- Implement proper error handling for certificate failures
