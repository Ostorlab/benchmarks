# Ostorlab Security Testing Benchmarks

[![License: Apache-2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Android Apps](https://img.shields.io/badge/Android%20Apps-75-green.svg)](benchmarks/mobile/android)
[![iOS Apps](https://img.shields.io/badge/iOS%20Apps-23-blue.svg)](benchmarks/mobile/ios)
[![Twitter Follow](https://img.shields.io/twitter/follow/OstorlabSec?style=social)](https://twitter.com/OstorlabSec)

**Real vulnerabilities. Real impact. Real testing.**

The first open-source benchmark suite featuring 98 realistic vulnerable mobile applications that mirror actual CVE and bug bounty findings - not theoretical textbook examples.

![Ostorlab Benchmarks Demo](docs/images/benchmark-overview.gif)
*Testing security tools against real-world mobile vulnerabilities*

## **Overview**

Ostorlab Security Testing Benchmarks provides a comprehensive collection of vulnerable mobile applications based on **actual security incidents**, **CVE reports**, and **bug bounty findings**. Unlike academic exercises, these benchmarks reflect the vulnerabilities that security teams encounter in production environments.

### **Key Features**

- ✨ **93 Vulnerable Applications** - 72 Android and 21 iOS apps with realistic functionality
- 🎯 **70+ Vulnerability Classes** - From authentication bypasses to complex logic flaws
- 💰 **Bug Bounty Inspired** - Every vulnerability based on real findings worth actual bounties
- 🔧 **Automation Challenges** - Includes "impossible to automate" logical bugs
- 📊 **Comprehensive Documentation** - Detailed exploitation guides and detection strategies

## **Why These Benchmarks?**

Traditional vulnerable app collections serve educational purposes but fail to represent modern mobile security challenges. After analyzing thousands of bug bounty reports, we identified a critical gap: **security teams need benchmarks that reflect actual production vulnerabilities**.

**The Reality:** A security scanner might catch 100% of SQL injections in test apps but miss critical logic flaws that constitute 60% of actual bug bounty payouts. These benchmarks measure what truly matters.

## **Quick Start**

### **Prerequisites**

- Git
- Android Studio 4.0+ (for Android apps)
- Xcode 12+ (for iOS apps)
- Java 8+ / Swift 5+

### **Installation**

1. **Clone the repository**
   ```bash
   git clone https://github.com/Ostorlab/benchmarks.git
   cd benchmarks
   ```

2. **Navigate to your platform of choice**
   ```bash
   cd mobile/android  # For Android applications
   # or
   cd mobile/ios      # For iOS applications
   ```

3. **Choose an application and follow its README**
   ```bash
   cd banking-app
   # Each app contains:
   # - Source code
   # - Build instructions
   # - Vulnerability documentation
   # - Exploitation guides
   ```

## **Vulnerability Coverage**

### **Authentication & Authorization**
- PIN/Passcode bypass mechanisms
- Two-factor authentication circumvention
- OAuth account takeover (missing PKCE)
- Biometric authentication bypasses
- Session persistence after password changes

### **Data Exposure**
- Firebase database takeover scenarios
- Cleartext storage of sensitive data
- Google Advertising ID misuse
- Location data exposure vulnerabilities
- Hardcoded production secrets

### **Complex Logic Flaws**
- Intent redirection vulnerabilities
- Task hijacking attack vectors
- Broadcast injection scenarios
- WebView JavaScript bridge exploitation
- Path traversal in archive processing

### **Platform-Specific Vulnerabilities**

**Android-Specific:**
- Tapjacking vulnerabilities
- Unprotected critical activities
- Provider SQL injection
- Grant URI permission escalation

**iOS-Specific:**
- Deeplink CSRF attacks
- WebKit internal file access
- URL link spoofing
- Promotion code brute force
- Unencrypted session exposure

## **Contributing**

We welcome contributions that enhance the realism and coverage of our benchmarks! Whether you're a developer, security researcher, or tool vendor, there's a way for you to contribute.

### **How to Contribute**

1. **Fork the repository**
2. **Create your feature branch** (`git checkout -b feature/new-vulnerability`)
3. **Follow our coding standards** (see [CONTRIBUTING.md](CONTRIBUTING.md))
4. **Add tests and documentation**
5. **Commit your changes** (`git commit -m 'Add realistic OAuth bypass scenario'`)
6. **Push to the branch** (`git push origin feature/new-vulnerability`)
7. **Open a Pull Request**

### **Contribution Ideas**

- 🆕 **Add new vulnerable applications** following real-world patterns
- 🔄 **Port vulnerabilities** to different platforms
- 📝 **Improve documentation** with clearer exploitation guides
- 🧪 **Share vulnerability patterns** from your bug bounty findings
- 🛠️ **Enhance detection logic** for existing vulnerabilities

See our [Contribution Guide](CONTRIBUTING.md) for detailed instructions.

## **Community & Support**

Join our growing community of security professionals working toward more realistic security testing:

- 💬 **Discussions**: [GitHub Discussions](https://github.com/Ostorlab/benchmarks/discussions) - Ask questions and share ideas
- 🐛 **Issues**: [Report bugs](https://github.com/Ostorlab/benchmarks/issues) or request features
- 🐦 **Twitter**: Follow [@OstorlabSec](https://twitter.com/OstorlabSec) for updates

## **License**

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## **Acknowledgments**

Special thanks to:
- The security research community for sharing vulnerability insights
- Bug bounty hunters whose findings inspired these benchmarks
- Contributors who help maintain and expand this project
- Tool vendors who use these benchmarks to improve their products

## **Disclaimer**

> **⚠️ Important**: These applications contain intentional security vulnerabilities for testing purposes only. Do not deploy in production environments. Use responsibly in isolated testing environments.

## **Citation**

If you use these benchmarks in your research, please cite:

```bibtex
@misc{ostorlab-benchmarks-2025,
  title={Ostorlab Security Testing Benchmarks},
  author={Ostorlab Team},
  year={2025},
  url={https://github.com/Ostorlab/benchmarks}
}
```

---

<p align="center">
  Made with ❤️ and 🔥 by the <a href="https://ostorlab.co">Ostorlab</a> team
</p>

<p align="center">
  <a href="#ostorlab-security-testing-benchmarks">Back to top ↑</a>
</p>
