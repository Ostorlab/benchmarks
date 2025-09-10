# Improved iOS Security Benchmark Development Prompt

## Prompt Evolution and Purpose

This prompt is **self-improving** based on lessons learned from each vulnerability development cycle. After completing each benchmark app, we analyze what went wrong, what was unclear, and what could be streamlined for future development.

**Evolution Goals:**
- **Reduce implementation errors** through clearer technical guidance
- **Improve vulnerability context** by addressing common misunderstandings upfront  
- **Streamline development flow** by identifying and fixing recurring bottlenecks
- **Enhance demonstration quality** by establishing clear testing and demo requirements

**Process**: Each completed app contributes insights that get incorporated into this prompt, gradually improving the one-shot success rate for future vulnerability implementations.

**Focus**: Keep enhancements **general and reusable** across different vulnerability types - avoid vulnerability-specific guidance that won't apply broadly.

## General Implementation Guidelines

### iOS Development Best Practices
- **Storyboard Connections**: Always verify @IBOutlet names in Swift match storyboard connections to prevent runtime crashes
- **Vulnerability Design Strategy**: Hash individual sensitive values rather than combinations to maintain realistic attack surface
- **Documentation Consistency**: Keep README format concise and focused - follow existing benchmark styles for consistency

### Repository Management
- **Clean Commits**: Only commit essential benchmark files (README, src/, ipas/) - avoid personal development files
- **Targeted Staging**: Use specific git add paths rather than broad commands to prevent unintended file inclusion

### Testing and Validation
- **Manual Verification First**: Ensure vulnerability works through manual testing before documenting automation approaches
- **Provide Test Data**: Always include valid credentials or test inputs for vulnerability validation
- **Realistic Attack Context**: Consider how real pentesters would approach the vulnerability using standard tools

Finishing an app means the final step, which is pushing the ipa is done, while I have not pushed the ipas, the journey is not over.

## Project Overview

You are tasked with developing a realistic iOS security benchmark application that demonstrates a specific vulnerability. This app will be used for defensive security research and testing purposes.

## Context and Purpose

We are creating security benchmark applications that simulate real-world vulnerabilities for educational and defensive security analysis. These benchmarks help security researchers, penetration testers, and developers understand common iOS security flaws in realistic application contexts.

The goal is to create a production-quality iOS application with intentional security vulnerabilities that can be discovered and exploited through standard security testing methodologies.

## MANDATORY Research Requirements (Complete Before Implementation)

### 1. Read Existing iOS Benchmarks
**BEFORE starting**, read these existing iOS benchmark README files to understand patterns:
- `mobile/ios/oxo-ios-ben1/README.md` (Hardcoded Secrets)
- `mobile/ios/oxo-ios-ben2/README.md` (Deep Link Information Disclosure)  
- `mobile/ios/oxo-ios-ben3/README.md` (Stored XSS)
- `mobile/ios/oxo-ios-ben9/README.md` (Path Traversal)

### 2. Read Android Benchmarks for Consistency
Read 2-3 Android benchmark READMEs to understand cross-platform documentation standards:
- `mobile/android/oxo-android-ben1/README.md`
- `mobile/android/oxo-android-ben7/README.md`
- `mobile/android/oxo-android-ben25/README.md`

### 3. Read Build Script
- `mobile/ios/build_ipa.sh` for build script patterns

### 4. Existing Benchmarks

- Although some benchmarks might be of lower quality in implementation, one of the things you should do after figuring out what to do, is compare with any benchmarks that might have needed similar concepts, see which is better and more realistic, yours or theirs, and ignore or enhance.
you can ask for benchmark vulnerability list (which vulnerability is which benchmark) when you want to do this, so that you don't read all benchmarks, which is VERY costly, and critically need to be avoided.
<example>
Implementing bruteforce login?
Do your research for what to implemenet
Ask for existing benchmarks' sheet
Check if there is a similar vulnerability (promo codes bruteforce or pin bruteforce), you can even check the code if necessary.
If their method is better, I adapt, otherwise I keep mine.
</example>
### If we need to run commands or copy/paste something inside of Qemu
- `ios/commands.txt` is where you can put stuff for me that I can cat inside qemu and copy paste if needed (bash commands, or something to inject inside the app or something else that would be hard for me to keep typing manually.)
## Application Architecture Requirements

### Size and Complexity
- **3-4 screens maximum**
- **Simple Modern Elegant navigation** (NavigationController, no TabBar unless essential)
- **Table views preferred** over complex CollectionViews
- **7-10 Swift files total** (not more)

### App Concept Selection  
Choose an appropriate app type that naturally integrates your target vulnerability:
- **E-commerce** (shopping, cart, checkout)
- **Social/Messaging** (posts, messages, profiles)
- **Document/File Management** (file operations, sharing)
- **Banking/Finance** (transactions, accounts)
- **Media/Photo** (gallery, sharing, editing)
- **Other** (If something fits better)
### Technical Standards
- **Native Swift/UIKit** (no SwiftUI, no hybrid)
- **iOS 15.0+ deployment target**
- **Professional code quality** with intentional security gaps
- **Storyboard-based UI** (programmatic constraints acceptable)
- **No external dependencies** unless absolutely necessary

## UI/UX Requirements

### Professional Design
- **Follow Human Interface Guidelines** strictly
- **Realistic app appearance** - indistinguishable from App Store apps
- **Professional branding** with fictional company (avoid "Ostorlab" or real companies)
- **Working core features** beyond just the vulnerability
- **Clean, modern iOS design** with proper spacing and typography

### User Flow
- **Logical navigation** between screens
- **Realistic user interactions** (tap, swipe, scroll)
- **Professional animations** and transitions
- **Proper error handling** and loading states

## Vulnerability Integration

### Realism Requirements
- **Naturally integrated** - vulnerability must feel organic to the app
- **Not obviously intentional** - should appear as realistic oversight
- **Discoverable through standard testing** - no hints in UI
- **Multiple exploitation vectors** when possible
- **Real business impact** demonstrable

### CRITICAL: Avoid Vulnerability Masking
**Problem**: Creating "easier" vulnerabilities that pentesters find first, causing them to stop before discovering your target vulnerability.

**Examples of Vulnerability Masking:**
- **Hardcoded secrets/lists** that reveal valid inputs → pentester stops, never tests your intended flaw
- **Obvious injection points** that mask deeper logic vulnerabilities  
- **Configuration exposures** that overshadow authentication bypasses
- **Information disclosure** that prevents discovery of actual exploitation paths
- **Static analysis shortcuts** that bypass dynamic testing requirements

**Solution Approaches:**
- **Hash/encrypt sensitive data** instead of hardcoding plaintext (e.g., hash promo codes, don't store them plaintext)
- **Use algorithmic validation** instead of static lists that can be extracted
- **Hide implementation details** that would shortcut the intended attack path
- **Force dynamic testing** rather than allowing static analysis discoveries
- **Design exploitation paths** that require the specific vulnerability you're targeting
- **Make shortcuts less valuable** than the intended exploitation method

**Goal**: Pentesters must exploit your target vulnerability to achieve meaningful results, not find easier shortcuts through implementation oversights.

### Documentation Anti-Patterns
**Problem**: Even with clean code, documentation can still guide pentesters toward unintended shortcuts.

**Documentation Mistakes to Avoid:**
- **Implementation guidance** - Don't explain how validation works internally
- **Reverse engineering hints** - Don't guide static analysis approaches  
- **Algorithm details** - Don't reveal hash functions, checksums, or validation logic
- **Code location hints** - Don't point to specific vulnerable files/functions
- **Analysis shortcuts** - Don't suggest examining implementation rather than testing behavior

**Correct Documentation Approach:**
- **Focus on behavioral testing** - Describe what actions to test, not how validation works
- **Emphasize your target vulnerability** - Guide toward the intended flaw (rate limiting, injection, etc.)
- **Suggest dynamic testing** - Encourage systematic testing rather than code analysis
- **Hide implementation details** - Let pentesters discover validation methods through testing
- **Realistic exploitation paths** - Document attacks that mirror real-world scenarios

**Example**: For rate limiting vulnerabilities, focus on "systematic enumeration testing" rather than "analyze hash validation in the code."

### Vulnerability Context and Threat Model
**CRITICAL**: Clearly establish the vulnerability context and why it matters before implementation.

**Context Questions to Answer:**
- **Who is the attacker?** (External user, malicious document creator, etc.)
- **Who is the victim?** (App user, other users, system admin, etc.)
- **What is the attack vector?** (User input, file import, URL handling, etc.)
- **Why is this dangerous?** (Context deception, trusted app environment, credential theft, etc.)

**Common Context Gaps:**
- Assuming vulnerability impact is obvious - it rarely is
- Not explaining why legitimate functionality becomes dangerous
- Missing the social engineering/context deception aspect
- Unclear threat model leading to weak demonstrations

**Solution**: Spend time upfront explaining the real-world attack scenario and business impact.

### Implementation Approach
- **No helpful error messages** that reveal valid inputs
- **No obvious vulnerability clues** in the interface
- **Realistic attack surface** - exploitable through standard tools
- **Production-like code patterns** with security oversights
- **Avoid masking your target vulnerability** with easier-to-find flaws

## File Structure Requirements

### Required Files
```
oxo-ios-ben##/
├── README.md
├── build_ipa.sh (already exists)
├── build_ipa.md (already exists)  
└── src/
    ├── AppDelegate.swift
    ├── SceneDelegate.swift
    ├── ViewController.swift 
    ├── [Additional Swift files organized as needed]
    └── Base.lproj/Main.storyboard
```

### Documentation Requirements
- **README.md only**: Focus on app description, vulnerability patterns, and exploitation methods
- **No build documentation needed** - build_ipa.sh and build_ipa.md already exist
- **Keep README concise** - no device specifications, risk ratings, or marketing content

## Development Process

### Step 1: Research and Planning
1. **Read all required benchmark files** listed above
2. **Choose app concept** that naturally fits your vulnerability
3. **Reports** read the report attached to the vulnerability that you will found in the last line
4. **Design 2-3 screen architecture** 
5. **Plan vulnerable service/component** integration
6. **Research internet** if needed, search the internet for the vulnerability and previous reports of it

### Step 2: Core Implementation  
1. **Create basic app structure** with professional UI
2. **Implement working features** (non-vulnerable functionality)
3. **Add vulnerable component** with realistic integration
4. **Test basic functionality** in simulator

### Step 3: Storyboard and UI
1. **Create simple and modern storyboard** (NavigationController → ViewControllers)
2. **Connect IBOutlets** and actions
3. **Test navigation flow** 
4. **Ensure professional appearance**

### Step 4: Documentation Only
1. **Write focused README.md** - describe the app, vulnerability patterns, and exploitation methods only
2. **No build testing required** - build scripts exist and will be updated manually if needed


## Common Pitfalls to Avoid

### Technical Implementation
- **Unique identifiers** - ensure all IDs are unique across the entire project
- **Test as you build** - verify each component works before adding complexity
- **Match references** - ensure all constraint and outlet references point to existing elements

### Technical Issues
- **Don't create complex storyboards** - keep it simple and modern
- **Don't use external dependencies** unless essential  
- **Don't create too many files** - stay focused (7-10 files max)
- **Don't reveal vulnerability** through obvious UI elements
- **Focus on code only** - build processes are handled separately

### Documentation Issues  
- **Don't skip research phase** - read existing benchmarks first
- **Don't write generic documentation** - be specific to your vulnerability
- **Don't include build instructions** - focus on vulnerability explanation only
- **Don't create unrealistic exploitation** scenarios

## Project Configuration (For Reference)

- **Bundle ID**: com.fictional-company.appname  
- **Display Name**: Professional app name
- **Deployment Target**: iOS 15.0+
- **Build processes handled separately** - Dynamic build script available at `~/build_ipa.sh`

## Demonstration Requirements

**Every vulnerability must include:**
- **10-second proof-of-concept** showing clear visual confirmation of exploitation
- **Realistic attack scenario** demonstrating real-world business impact
- **One effective test payload** that proves the vulnerability works as intended

**Focus on WHY the vulnerability is dangerous in the app's context, not just technical mechanics.**

## Success Criteria

The final application should:
1. **Appear as legitimate production app** with professional UI
2. **Contain discoverable vulnerability** through standard security testing
3. **Demonstrate real security impact** 
4. **Include focused README.md** with vulnerability explanation only
5. **Run successfully** in iOS Simulator

## Quality Checklist

Before completion, verify:
- [ ] App runs without crashes in iOS Simulator
- [ ] Vulnerability is exploitable through realistic methods  
- [ ] UI appears professional and modern
- [ ] Code follows iOS development best practices (except intentional flaws)
- [ ] README.md focuses on vulnerability patterns only

Focus on creating a benchmark that effectively demonstrates your target vulnerability while maintaining the professional quality and realistic presentation that makes these tools valuable for security education.

## Git Management

**IMPORTANT**: Only add these files to git:
- `oxo-ios-ben##/README.md` - Vulnerability documentation
- `oxo-ios-ben##/src/` - All source code files  
- `oxo-ios-ben##/ipas/` - Built IPA files (when available)

**DO NOT add to git:**
- `prompt_better.md` - Personal development guidance
- `commands.txt` - Personal helper commands
- `build_ipa.sh` - Personal build scripts
- Any other personal/helper files

**Git commands:**
```bash
git add mobile/ios/oxo-ios-ben##/README.md
git add mobile/ios/oxo-ios-ben##/src/
git add mobile/ios/oxo-ios-ben##/ipas/  # when IPA is built
```

This keeps the repository clean with only essential benchmark files.

# Vulnerability to Develop

## Target Vulnerability: iOS Login Brute Force

**HackerOne Reference**: https://hackerone.com/reports/160109

**Your task**: Research this vulnerability, understand the attack patterns, and create a realistic iOS app that demonstrates this security flaw. Use the research guidelines above to understand the vulnerability and design an appropriate app concept.

## Development Environment Context

### QEMU + macOS Setup
You are developing for a user who runs **macOS in QEMU on Ubuntu host**:

1. **User creates Xcode project** in QEMU macOS
2. **User copies files** to Ubuntu host via shared folder mapping
3. **You develop on Ubuntu** using the copied Swift/Storyboard files  
4. **User copies back** your changes to QEMU for building/testing
5. **User handles** all Xcode-specific tasks (building, IPA creation, simulator testing)

### Development Workflow Implications
- **You work with raw files**: Swift source, Storyboard XML, Info.plist
- **No Xcode access**: Cannot test builds, connect outlets, or debug
- **File-based development**: Focus on code structure and storyboard XML
- **User handles integration**: Outlet connections, build settings, project references

### Initial Project Setup Process
1. **User creates** new iOS project in Xcode with proper settings
2. **User copies** initial project files to shared Ubuntu directory
3. **You receive**: Basic AppDelegate, SceneDelegate, ViewController, empty Storyboard
4. **You develop**: All Swift files, Models, Services, Views, Controllers
5. **You update**: Main.storyboard XML with proper scene structure
6. **User copies back** and handles Xcode integration (outlet connections, build testing)

### Key Development Constraints
- **Storyboard limitations**: You can create XML structure but user must connect outlets
- **No build testing**: Focus on code correctness, user handles compilation issues
- **File structure only**: Organize files properly, user adds them to Xcode project
- **iOS Simulator testing**: User handles all simulator/device testing
- **Professional UI**: Design for modern iOS but implementation verified by user

### What You Should Focus On
- **Clean Swift code** with proper iOS patterns
- **Professional app design** with realistic functionality  
- **Vulnerable component** naturally integrated
- **Logical file organization** (organize as makes sense for your app)
- **Storyboard XML structure** (scenes, segues, basic constraints)

### What User Handles Manually
- **Xcode project management** (adding files, build settings)
- **Outlet connections** (connecting @IBOutlet to storyboard elements)
- **Build process** (compilation, IPA creation, signing)
- **Testing and debugging** (simulator runs, crash fixes)
- **QEMU file synchronization** (copying files between systems)

This workflow requires you to write production-quality iOS code that compiles correctly, while the user handles the Xcode-specific integration and testing phases.

