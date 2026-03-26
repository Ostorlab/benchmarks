# oxo-harmony-ben6 DataAbility Injection Vulnerabilities

## Challenge Details

### Description

This HarmonyOS app sample demonstrates two injection vulnerabilities in custom DataAbility providers:

1. **SQL Injection** — A DataAbility exposing SQLite database queries vulnerable to SQL injection through unsanitized predicate parameters
2. **Path Traversal** — A DataAbility exposing file system operations vulnerable to directory traversal through unsanitized file path parameters

Both DataAbilities are declared with `exported: true` in `module.json5`, making them accessible to all applications on the device. The vulnerabilities stem from direct concatenation of user-supplied input into SQL queries and file paths without sanitization or parameterization.

This pattern demonstrates the HarmonyOS analog of Android ContentProvider vulnerabilities, where improperly secured data-sharing components expose backend resources to unauthorized access and manipulation.

### Vulnerability Types and Categories

#### 1. SQL Injection via DataAbility
- **Type:** SQL Injection
- **Category:** Improper Input Validation / CWE-89
- **CWE:** CWE-89 (Improper Neutralization of Special Elements used in an SQL Command)
- **Severity:** High

#### 2. Path Traversal via DataAbility
- **Type:** Path Traversal / Directory Traversal
- **Category:** Improper Input Validation / CWE-22
- **CWE:** CWE-22 (Improper Limitation of a Pathname to a Restricted Directory)
- **Severity:** High

### Difficulty
Medium

### Overall Severity
High

### Severity Rationale

These vulnerabilities are considered high severity because:

#### SQL Injection
- DataAbility exposes SQLite database queries to all applications without input validation
- Attackers can craft malicious predicates to:
  - Extract sensitive data using boolean-based or UNION-based injection
  - Modify or delete database records
  - Execute arbitrary SQL commands through comment-based injection
- Example malicious predicate: `1' OR '1'='1` (bypass authentication/authorization)
- No authentication or signature verification on the ContentProvider caller

#### Path Traversal
- DataAbility exposes file system operations without path boundary validation
- Attackers can traverse the sandbox using `../` sequences to:
  - Read arbitrary files outside the intended directory
  - Write malicious files to sensitive locations
  - Delete critical files
  - Access application private data
- Example malicious path: `../../../secret/flag.txt` (escape sandbox boundaries)
- No caller verification or path sanitization implemented

---

## Vulnerability Details

### 1. SQL Injection in UserDataProvider

#### Location
- **File:** `entry/src/main/ets/datashare/UserDataProvider.ets`
- **URI:** `content://com.example.vulnerable/data`

#### Vulnerable Code

```typescript
query(uri: string, predicates: string, columns: string[], callback: ...): void {
  const table = uri.split('/').pop() as string;
  
  // VULNERABLE: Direct concatenation of predicates into SQL query
  const sql = `SELECT * FROM ${table} WHERE ${predicates}`;
  
  this.rdbStore!.querySql(sql, [], (err, rs) => {
    // ... returns results
  });
}
```

#### Attack Scenarios

**Scenario 1: Authentication Bypass**
```
Malicious predicate: "1' OR '1'='1"
Resulting SQL: SELECT * FROM users WHERE 1' OR '1'='1
Result: Returns all user records regardless of login credentials
```

**Scenario 2: Data Exfiltration (UNION-based)**
```
Malicious predicate: "1 UNION SELECT flag FROM secret_table"
Resulting SQL: SELECT * FROM users WHERE 1 UNION SELECT flag FROM secret_table
Result: Exfiltrates data from other tables
```

**Scenario 3: Database Manipulation**
```
Malicious insert values: "admin', 'injected@domain.com', 'admin'); DELETE FROM users; --"
Resulting SQL: INSERT INTO users (username, email, role) VALUES ('admin', 'injected@domain.com', 'admin'); DELETE FROM users; --
Result: Injects new record and deletes all user records
```

#### Affected Methods
- `query()` — Direct predicates concatenation
- `insert()` — Unsanitized value concatenation
- `update()` — Unsanitized SET clause and predicates
- `delete()` — Direct predicates concatenation

---

### 2. Path Traversal in CacheStorageProvider

#### Location
- **File:** `entry/src/main/ets/datashare/CacheStorageProvider.ets`
- **URI:** `content://com.example.vulnerable/files`

#### Vulnerable Code

```typescript
query(uri: string, ...): void {
  const file = uri.split('/').pop() as string;
  
  // VULNERABLE: Direct path concatenation without boundary validation
  const path = this.basePath + file;  // No ../ validation!
  
  try {
    result.content = this.read(path);
    callback(null, [result]);
  } catch (err) {
    callback(err);
  }
}
```

#### Attack Scenarios

**Scenario 1: Sandbox Escape (Read)**
```
Malicious file parameter: "../secret/flag.txt"
Resulting path: /data/app/files/../secret/flag.txt → /data/app/secret/flag.txt
Result: Reads sensitive files outside sandbox
```

**Scenario 2: Sandbox Escape (Write)**
```
Malicious file parameter: "../../../../system/sensitive/inject.txt"
Malicious content: "Injected payload"
Result: Creates files outside app sandbox
```

**Scenario 3: Sensitive File Deletion**
```
Malicious file parameter: "../../../database.db"
Result: Deletes application database
```

#### Affected Methods
- `query()` — No path validation on file parameter
- `insert()` — No boundary check on destination path
- `delete()` — Allows deletion of arbitrary files
- `openFile()` — Unrestricted file access

---

## Root Causes

### SQL Injection Root Cause
1. **No Input Validation:** Predicates are concatenated directly into SQL queries
2. **No Parameterized Queries:** Using string concatenation instead of prepared statements
3. **No Escaping:** Special SQL characters (quotes, semicolons) are not escaped

### Path Traversal Root Cause
1. **No Path Normalization:** File paths are not normalized to detect `../` sequences
2. **No Path Boundary Validation:** No verification that final path stays within `basePath`
3. **No Canonicalization:** Symbolic links and relative paths not resolved before access

---

## Exploitation Requirements

- **App Bundle:** `co.ostorlab.insecure_harmony_app`
- **SDK Version:** API Level 10
- **Permissions:** None required (DataAbilities are exported)
- **Attack Surface:** Remote exploitation via crafted Intent/Want from attacker app

---

## Example Vulnerable Declarations (`module.json5`)

```json
{
  "extensionAbilities": [
    {
      "name": "UserDataProvider",
      "srcEntry": "./ets/datashare/UserDataProvider.ets",
      "type": "dataShare",
      "exported": true,  // ❌ Exposed to all apps
      "uri": "content://com.example.vulnerable/data"
      // ❌ Missing: Input validation, parameterized queries, permissions
    },
    {
      "name": "CacheStorageProvider",
      "srcEntry": "./ets/datashare/CacheStorageProvider.ets",
      "type": "dataShare",
      "exported": true,  // ❌ Exposed to all apps
      "uri": "content://com.example.vulnerable/files"
      // ❌ Missing: Path validation, boundary checks, permissions
    }
  ]
}
```

---

## Mitigation Strategies

### For SQL Injection
1. **Use Parameterized Queries:**
   ```typescript
   // Instead of:
   const sql = `SELECT * FROM users WHERE id = ${userId}`;
   
   // Do this:
   const sql = `SELECT * FROM users WHERE id = ?`;
   this.rdbStore.query(this.context, sql, [userId], ...);
   ```

2. **Input Validation:**
   - Whitelist allowed values for table/column names
   - Validate predicate format using regex patterns
   - Reject unexpected input types

3. **Permissions & Caller Verification:**
   - Define custom permissions for DataAbility access
   - Verify caller identity using `CallerInfo` in lifecycle methods
   - Implement signature-based trust verification

### For Path Traversal
1. **Path Normalization & Validation:**
   ```typescript
   // Resolve path to absolute form
   const canonicalPath = fs.getRealPath(fullPath);
   const canonicalBase = fs.getRealPath(basePath);
   
   // Verify path stays within boundary
   if (!canonicalPath.startsWith(canonicalBase)) {
     throw new SecurityException("Path traversal detected");
   }
   ```

2. **Blacklist Dangerous Patterns:**
   - Reject `../`, `..\\`, encoded variants (`%2e%2e/`)
   - Reject absolute paths
   - Reject symbolic links that escape sandbox

3. **Permissions & Caller Verification:**
   - Define custom permissions for file access DataAbility
   - Verify caller identity and trust level
   - Set `exported: false` if cross-app access is not required

