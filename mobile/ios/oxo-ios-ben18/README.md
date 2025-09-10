# oxo-ios-ben18: Weak Hash Algorithms for Security Validation

## Description

SecureDocuments is an iOS application designed for professionals who need to securely store, share, and validate document integrity. The app provides document management with digital signatures, integrity verification, and secure document sharing capabilities for legal firms, healthcare providers, and corporate environments.

The application demonstrates **weak hash algorithm vulnerabilities** where security-critical operations rely on deprecated and cryptographically broken hash functions:

- **Document Integrity Verification**: Uses MD5 hashing to verify document integrity and detect tampering
- **Digital Signature Validation**: Employs SHA-1 for digital signature verification of important documents
- **Password Storage**: Stores user passwords using unsalted MD5 hashes
- **File Deduplication**: Relies on MD5 hashes for identifying duplicate documents
- **Backup Verification**: Uses SHA-1 to verify backup file integrity

These weak hash implementations create multiple attack vectors where malicious actors can exploit hash collisions, rainbow table attacks, and forge digital signatures to compromise document security and user authentication.

### Vulnerability Type and Category
- **Type:** Use of Cryptographically Weak Hash Functions
- **Category:** Cryptographic Issues / Insecure Cryptographic Storage
- **CWE:** CWE-327 (Use of a Broken or Risky Cryptographic Algorithm)

### Difficulty
Medium