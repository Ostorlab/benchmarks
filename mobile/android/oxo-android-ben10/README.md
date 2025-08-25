# ThemeEngine Android App

This is a demonstration app that showcases a theme engine functionality allowing users to load custom themes. The app demonstrates a realistic use case that could lead to insecure class loading vulnerabilities.

## Structure

The app consists of several activities simulating a real-world application:
- Main Activity (Home screen with current theme preview)
- Theme Manager Activity (Browse and load themes)
- User Settings Activity (App preferences)
- Theme Store Activity (Browse available themes)
- Profile Activity (User profile management)

## Security Considerations

The app demonstrates how improper validation of external class loading can lead to security vulnerabilities. In a real-world scenario, proper signature verification and code source validation should be implemented.
