# Installed Apps Example

Demo app for the `installed_apps` plugin (Android only).

- **App list**: search, pull to refresh, load time. The tune button opens every `getInstalledApps`
  option: system apps, launchable apps, icons, platform detection, platform filter, package name
  prefix and package names.
- **Find by package name**: `getAppInfo`, with a toast when the app is not installed.
- **App details**: `startApp`, `openSettings`, `uninstallApp`, `isAppInstalled`, `isSystemApp`,
  and `toast` (copy the package name).

```bash
flutter run
```

## Integration tests

```bash
flutter drive \
  --driver=integration_test/test_driver/integration_test.dart \
  --target=integration_test/app_test.dart
```
