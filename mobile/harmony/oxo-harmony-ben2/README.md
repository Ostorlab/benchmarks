# oxo-harmony-ben2 Exported Ability Hijacking

## Challenge Details

### Description

This HarmonyOS app sample demonstrates an exported ability hijacking vulnerability:

- Exported Ability Hijacking via UIAbility or ExtensionAbility components declared 
  with `exported: true` in `module.json5`, without a `permissions` field or 
  programmatic caller verification, allowing external applications to invoke 
  sensitive functionality through crafted Want objects.

This pattern enables unauthorized function execution through cross-app invocation
by malicious applications using the HarmonyOS Ability framework.

### Vulnerability Type and Category
- **Type:** Exported Ability Hijacking
- **Category:** Improper Component Exposure / Broken Access Control
- **CWE:** CWE-926 (Improper Export of Android Application Components)
  > **Note:** CWE-926 is Android-specific by definition, but is applied here by
  > analogy as there is currently no HarmonyOS-equivalent CWE. The underlying
  > weakness — improper exposure of application components to external callers —
  > is conceptually identical across both platforms.

### Difficulty
Easy

### Severity
High

### Severity Rationale

This vulnerability is considered high severity because:

- UIAbility or ExtensionAbility components are exposed to all applications on the 
  device without sufficient access control
- Sensitive actions can be triggered externally without trusted caller validation 
  via `CallerInfo` or `bundleName` verification
- Attackers can exploit the weakness via:
    - Crafting a malicious HarmonyOS app that constructs a Want object targeting 
      the vulnerable ability by its explicit `bundleName` and `abilityName`
    - Invoking the exposed ability entry point (`onCreate` / `onStart`) through 
      `startAbility()` or `connectServiceExtensionAbility()`
    - Leading to unauthorized execution of protected functionality without 
      holding the required permissions or trust level

### Root Cause

The absence of:
1. A `permissions` field in the ability declaration within `module.json5`
2. Programmatic caller identity verification using `CallerInfo` inside the 
   ability lifecycle callbacks

### Example Vulnerable Declaration (`module.json5`)
```
{
  "abilities": [
    {
      "name": "SensitiveAbility",
      "srcEntry": "./ets/abilities/SensitiveAbility.ets",
      "exported": true
      // ❌ Missing: "permissions": ["com.example.permission.ACCESS_SENSITIVE"]
    }
  ]
}
```
### Mitigation

- Set a custom permission in the ability declaration:
  `"permissions": ["com.example.permission.ACCESS_SENSITIVE"]`
- Verify caller identity programmatically using `AbilityContext.callerInfo` 
  and validate `bundleName` or signature trust level inside `onCreate()`/`onStart()`
- Set `exported: false` if cross-app invocation is not required
