# oxo-android-ben20 Path Traversal in ZIP Extraction Allows Overwriting Internal Files

## Challenge Details

### Description

This Android app demonstrates a **path traversal vulnerability during ZIP file extraction**:

- The app allows users to **upload ZIP files**, which are then extracted to internal storage.
- File names inside the ZIP archive are **not sanitized**, meaning entries like `../` can **escape the target directory**.
- This allows an attacker to **overwrite arbitrary files** in the app’s internal storage, including **trusted resources** such as JavaScript files used in a WebView.

This vulnerability can be exploited to **inject malicious content** or modify app behavior.

### Vulnerability Type and Category
- **Type:** Path Traversal
- **Category:** Path traversal processing ZIP file

### Difficulty
Medium

### How to Exploit

1. Create a malicious ZIP file with file entries using `../` to break out of the intended extraction directory:

    ```python
    import zipfile

    with zipfile.ZipFile("vulnerable.zip", "w") as z:
        z.writestr("../webview/script.js", "function showAlert() { alert('malicious javascript'); }\n")
    ```

2. Upload the `vulnerable.zip` file using the app’s ZIP upload feature.

3. Upon extraction, the file `script.js` is placed into the `webview` directory outside the intended ZIP extraction folder overwritten the original `script.js`:

    ```
    /data/data/com.ostorlab.unzipper/files/
    ├── extracted/
    │   └── [zip extracted files]
    └── webview/
        └── index.html
        └── style.css
        └── script.js
    ```

4. The app **uses the `script.js` file in a WebView**, so when it is overwritten, the **malicious JavaScript is executed**.


### Impact

- Can **overwrite application files** within internal storage.
- May lead to **JavaScript injection** or modification of trusted content.

---

## Build Instructions

This project uses **Android Studio**, written in **Java**.

### Requirements
- **compileSdkVersion**: 31 or higher

### Steps

1. Open the project in **Android Studio**.
2. Connect a physical device or launch an emulator with **API 31 or above**.
3. Build and run the app to begin testing the ZIP upload functionality.
