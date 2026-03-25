# oxo-harmony-ben3 Implicit Want Interception

## Challenge Details

### Description

This HarmonyOS app sample demonstrates an implicit Want interception vulnerability:

- Implicit Want Interception via calling `startAbility()` with an implicit Want
	that defines only `action` and payload parameters, without an explicit
	`bundleName` and `abilityName`, allowing malicious apps to register matching
	handlers and intercept sensitive data.

This pattern enables unauthorized handling of sensitive actions through
cross-app Want resolution by untrusted applications.

### Vulnerability Type and Category
- **Type:** Implicit Want Interception
- **Category:** Insecure Inter-Application Communication / Broken Access Control
- **CWE:** CWE-927 (Use of Implicit Intent for Sensitive Communication)
	> **Note:** CWE-927 is Android-specific by definition, but is applied here by
	> analogy as there is currently no HarmonyOS-equivalent CWE. The underlying
	> weakness — using implicit inter-app messaging for sensitive operations — is
	> conceptually identical across both platforms.

### Difficulty
Easy

### Severity
High

### Severity Rationale

This vulnerability is considered high severity because:

- Sensitive note data is sent through an implicit Want that may be resolved by
	any third-party app declaring a matching action filter
- No explicit trust boundary is enforced for the recipient ability before
	cross-app dispatch
- Attackers can exploit the weakness via:
		- Publishing a malicious HarmonyOS app that handles
			`ohos.want.action.sendData`
		- Intercepting note content passed in Want `parameters`
		- Leading to unauthorized access to private user data and potential misuse

### Root Cause

The data-sharing flow uses implicit Want resolution for sensitive content and
does not constrain the destination ability identity.

### Example Vulnerable Code (`Notes.ets`)

```ts
Button('Share Notes')
	.width('100%')
	.height(48)
	.onClick(() => {
		let context = getContext(this) as common.UIAbilityContext;
		let want: Want = {
			action: 'ohos.want.action.sendData',
			parameters: {
				notes: this.notes.join('\n')
			}
		};
		context.startAbility(want);
	})
```

### Mitigation

- Use an explicit Want by setting trusted destination fields such as
	`bundleName` and `abilityName`
- Protect sensitive share targets with custom permissions and caller validation
- Avoid sending highly sensitive data through broad action-based implicit routing
