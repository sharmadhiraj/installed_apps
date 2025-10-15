# Installed Apps

**Installed Apps** is a Flutter plugin that provides utilities to interact with installed apps on a
device. You can list installed apps, get app info, launch apps, open settings, and more.

> ⚠️ Currently, only Android is supported. iOS methods return default/fallback values.

---

## Version 2.0.0 Breaking Changes

Version 2.0.0 has some breaking changes, for example, `getInstalledApps` now uses **named arguments
** instead of positional arguments as before. Additionally, some other argument names have been
updated.

## Features

* List installed apps with optional filters:
  * Exclude system apps
  * Exclude non-launchable apps
  * Filter by package name prefix
  * Filter by platform type
* Get detailed app info
* Launch apps by package name
* Open app settings
* Check if an app is system or installed
* Uninstall apps
* Show toast messages

---

## Installation

```bash
flutter pub add installed_apps
```

Or check the [Installation Guide](https://pub.dev/packages/installed_apps/install).

Example project: [GitHub](https://github.com/sharmadhiraj/installed_apps/tree/master/example)

---

## Usage

### Get Installed Apps

```
List<AppInfo> apps = await InstalledApps.getInstalledApps(
  // Optional: whether to exclude system apps from the list. Default is true.
  excludeSystemApps: true,
  
  // Optional: whether to exclude apps that cannot be launched (no launch intent). Default is true.
  excludeNonLaunchableApps: true,
  
  // Optional: whether to include app icons in the result. Default is false.
  withIcon: false,
  
  // Optional: filter apps whose package names start with this prefix. Default is null (no filtering).
  packageNamePrefix: "com.example",
  
  // Optional: filter apps by platform type (Flutter, React Native, etc.). Default is null (no filtering).
  platformType: PlatformType.flutter,
);
```

### Get App Info

```
AppInfo? app = await InstalledApps.getAppInfo("com.example.myapp");
```

### AppInfo Model

```
class AppInfo {
  String name;
  Uint8List? icon;
  String packageName;
  String versionName;
  int versionCode;
  PlatformType platformType;
  int installedTimestamp;
  bool isSystemApp;
  bool isLaunchableApp;
}
```

### Launch App

```
await InstalledApps.startApp("com.example.myapp");
```

### Open App Settings

```
InstalledApps.openSettings("com.example.myapp");
```

### Check System App

```
bool? isSystem = await InstalledApps.isSystemApp("com.example.myapp");
```

### Uninstall App

```
bool? success = await InstalledApps.uninstallApp("com.example.myapp");
```

### Check if Installed

```
bool? installed = await InstalledApps.isAppInstalled("com.example.myapp");
```

---

## PlatformType Enum

```dart
enum PlatformType {
  flutter('flutter', 'Flutter'),
  reactNative('react_native', 'React Native'),
  xamarin('xamarin', 'Xamarin'),
  ionic('ionic', 'Ionic'),
  nativeOrOthers('native_or_others', 'Native or Others');
}
```

---

## Notes

* Android requires `QUERY_ALL_PACKAGES` permission for full app visibility. Ensure compliance with
  Play Store policies.
* All methods catch exceptions and return default/fallback values to prevent crashes.

---

## Support

If you encounter any issues or have suggestions,
please [open an issue on GitHub](https://github.com/sharmadhiraj/installed_apps/issues).
Contributions and feedback are welcome!
