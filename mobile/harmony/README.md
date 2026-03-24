# Ostorlab Insecure Harmony Notes App

This project is a simple notes application for HarmonyOS that includes a login page and basic note management flows.

## Purpose
This application is designed for security testing and research.  
It provides a controlled environment to evaluate vulnerability detection workflows, tools, and analysis pipelines.

## Planned Features

- Login page (username and password)
- Notes list screen
- Create note

## Tech Stack

- Platform: HarmonyOS (OpenHarmony app structure)
- Language: ArkTS / ETS
- Build system: Hvigor
- Packaging: HAP (Harmony Ability Package)
- Project config files: JSON5

## Project Structure

Key folders and files:

- `AppScope/`: App-level resources and app configuration
- `entry/`: Main application module
- `entry/src/main/ets/`: Main ArkTS/ETS source files (abilities and pages)
- `entry/src/main/resources/`: Module resources
- `hvigor/`: Build configuration
- `build-profile.json5`, `hvigorfile.ts`, `oh-package.json5`: Root project build and package settings

## Build and Run Options

For this project, you can use one of the following workflows:

- DevEco Studio for build and emulators
- CLI for build + Oniro emulator for runtime testing
- Oniro VS Code extension for build and emulators

## How to Build the App

Pick one of the workflows below depending on your tooling preference.

### Option 1: DevEco Studio (Build + Emulator/Device)

1. Open this project in DevEco Studio.
2. Wait for sync/indexing to finish.
3. Select the `entry` module.
4. Select a target emulator or device.
5. Build and run.

### Option 2: CLI Build (Hvigor) + Oniro Emulator Install

From the project root:

```bash
./hvigorw clean
./hvigorw assembleHap --mode module -p product=default -p buildMode=debug --no-daemon
```

Build artifacts are generated in:

- `entry/build/default/outputs/default/`

Expected artifact:

- `entry-default-signed.hap` (or unsigned variant depending on signing setup)

After building, deploy to Oniro emulator:

1. Install and set up the Oniro emulator:
   - https://docs.oniroproject.org/device-development/developer-boards/emulator/
2. Connect to the emulator:

```bash
hdc tconn 127.0.0.1:55555
```

3. Push the built HAP to the emulator:

```bash
hdc file send "entry/build/default/outputs/default/entry-default-signed.hap" "data/local/tmp/entry-default-signed.hap"
```

4. Install the app:

```bash
hdc shell bm install -p "data/local/tmp/entry-default-signed.hap"
```

### Option 3: Oniro VS Code Extension (Build + Emulator)

Use the Oniro VS Code extension to build and run directly from VS Code.

1. Install the Oniro extension in VS Code.
2. Open this workspace.
3. Use the extension commands to select emulator/device.
4. Build and deploy from VS Code.
