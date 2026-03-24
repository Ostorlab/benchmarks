# oxo-harmony-ben1 Hardcoded Keys/Secrets -

## Challenge Details

### Description

This HarmonyOS app sample demonstrates a hardcoded secrets vulnerability:

- Hardcoded Keys/Secrets via embedding credentials directly in source code (for example, static username/password values in login logic).

This pattern makes secrets recoverable through source disclosure or reverse engineering and can lead to unauthorized access.

### Vulnerability Type and Category
- **Type:** Hardcoded Keys/Secrets
- **Category:** Insecure Cryptographic Storage / Sensitive Data Exposure
- **CWE:** CWE-798 (Use of Hard-coded Credentials)

### Difficulty
Easy

### Severity

High

### Severity Rationale

This vulnerability is considered high severity because:

- Credentials are directly exposed in the source code
- No protection or mitigation exists (e.g., encryption, secure storage)
- Attackers can easily extract secrets via:
    - Static analysis (reading source)
    - Reverse engineering the APK/HAP
    - Leads to unauthorized access to protected functionality