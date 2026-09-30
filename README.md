# Installed Apps

[![pub package](https://img.shields.io/pub/v/installed_apps.svg)](https://pub.dev/packages/installed_apps)

A Flutter plugin to list installed apps and work with them: get app info, launch, open settings,
uninstall, and more.

> **Android only.** On other platforms the methods return their fallback values (`[]` or `null`).

## Features

- List installed apps, filtered by system/launchable status, package prefix, package names or
  platform type (Flutter, React Native, Xamarin, Ionic)
- Get app info: name, icon, version, install time, category and more
- Launch an app, open its settings screen, uninstall it
- Check if an app is installed or is a system app

## Use cases

- App launchers and app drawers
- VPN split tunneling and per-app network rules
- Parental control, kiosk and app blocker tools
- Security and inventory tools that audit installed apps
- Checking whether a companion app is installed before deep linking to it

## Installation

```bash
flutter pub add installed_apps
```

```dart
import 'package:installed_apps/installed_apps.dart';
```

One import gives you `InstalledApps`, `AppInfo`, `AppCategory` and `PlatformType`.

Requires Android `minSdk` 21 and Java 17. See the
[example app](https://github.com/sharmadhiraj/installed_apps/tree/master/example) for a full demo.

## Quick start

```

final apps = await InstalledApps.getInstalledApps(withIcon: true);

for (final app in apps) {
  print("${app.name} (${app.packageName}) ${app.getVersionInfo()}");
}

await InstalledApps.startApp(apps.first.packageName);
```

## API

| Method                                | Returns                 | Description                             |
|---------------------------------------|-------------------------|-----------------------------------------|
| `getInstalledApps(...)`               | `Future<List<AppInfo>>` | List installed apps, sorted by name     |
| `getAppInfo(packageName, {withIcon})` | `Future<AppInfo?>`      | App details, or `null` if not installed |
| `startApp(packageName)`               | `Future<bool?>`         | Launch an app                           |
| `openSettings(packageName)`           | `void`                  | Open the app's system settings screen   |
| `uninstallApp(packageName)`           | `Future<bool?>`         | Show the uninstall prompt               |
| `isAppInstalled(packageName)`         | `Future<bool?>`         | Whether the app is installed            |
| `isSystemApp(packageName)`            | `Future<bool?>`         | Whether the app is a system app         |
| `toast(message, isShortLength)`       | `void`                  | Show an Android toast                   |

### `getInstalledApps` options

All options are optional.

| Option                     | Default | Description                                                                                                                          |
|----------------------------|---------|--------------------------------------------------------------------------------------------------------------------------------------|
| `excludeSystemApps`        | `true`  | Hide system apps                                                                                                                     |
| `excludeNonLaunchableApps` | `true`  | Hide apps without a launcher activity                                                                                                |
| `withIcon`                 | `false` | Include icons (`AppInfo.icon`, PNG bytes). Slower, enable only when needed                                                           |
| `packageNamePrefix`        | `null`  | Only apps whose package name starts with this (case-insensitive)                                                                     |
| `packageNames`             | `null`  | Only these packages. Not-installed ones are ignored, an empty list returns `[]`                                                      |
| `platformType`             | `null`  | Only apps built with this platform, e.g. `PlatformType.flutter`                                                                      |
| `detectPlatformType`       | `true`  | Set `false` to skip platform detection (faster). `AppInfo.platformType` is then `nativeOrOthers`. Ignored when `platformType` is set |

```
// Flutter apps only, including system apps
final flutterApps = await InstalledApps.getInstalledApps(
  excludeSystemApps: false,
  platformType: PlatformType.flutter,
);

// Look up specific apps, fast
final known = await InstalledApps.getInstalledApps(
  packageNames: ["com.whatsapp", "com.google.android.gm"],
  detectPlatformType: false,
);
```

### `AppInfo`

| Field                         | Type             | Notes                                                                                                                                                                  |
|-------------------------------|------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `name`                        | `String`         | Display name                                                                                                                                                           |
| `packageName`                 | `String`         | Unique app id                                                                                                                                                          |
| `icon`                        | `Uint8List?`     | Only when requested with `withIcon`                                                                                                                                    |
| `versionName` / `versionCode` | `String` / `int` | `getVersionInfo()` gives `"1.2.3 (45)"`                                                                                                                                |
| `platformType`                | `PlatformType`   | `flutter`, `reactNative`, `xamarin`, `ionic`, `nativeOrOthers`                                                                                                         |
| `installedTimestamp`          | `int`            | Last update time, milliseconds since epoch                                                                                                                             |
| `isSystemApp`                 | `bool`           |                                                                                                                                                                        |
| `isLaunchableApp`             | `bool`           | Has a launcher activity                                                                                                                                                |
| `category`                    | `AppCategory`    | `game`, `audio`, `video`, `image`, `social`, `news`, `maps`, `productivity`, `accessibility`, `undefined`. Needs Android 8.0 (API 26) or higher, otherwise `undefined` |

`AppInfo` also has `copyWith`, and two apps are equal when package name and version match.

## Android notes

### Permissions

The plugin declares two permissions in its manifest:

- `QUERY_ALL_PACKAGES`: needed to list all apps on Android 11+
- `REQUEST_DELETE_PACKAGES`: needed for `uninstallApp`

Google Play restricts `QUERY_ALL_PACKAGES` and may reject apps that cannot justify it. Either
declare the use in your Play Console listing, or remove it in your app manifest (you will then only
see a limited set of apps):

```xml

<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" tools:node="remove" />
```

With multiple flavors, keep the permission for dev builds and remove it in the Play Store flavor's
`AndroidManifest.xml`.

### Good to know

- Methods never throw. On failure, they return `[]` or `null`, and log the error with `debugPrint`.
- Platform detection is heuristic and can misclassify some apps.
- Upgrading from 1.x? Since 2.0.0 `getInstalledApps` takes named arguments.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

Issues and pull requests are welcome on
[GitHub](https://github.com/sharmadhiraj/installed_apps/issues).

```bash
flutter pub get
flutter analyze
flutter test
```
