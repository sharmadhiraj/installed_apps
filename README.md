# Installed Apps

The **Installed Apps** plugin for Flutter provides utility methods related to installed apps on a
device.

Currently, only Android is supported.

### Version Compatibility

If any functionality in the new version of the plugin doesn't work as expected, you can revert to a
previous version by specifying the exact version without using the caret (^) before the version
number. If you encounter any issues, please raise an issue on GitHub, and I'll address it as soon as
possible.

## Getting Started

1. [Installation Guide](https://pub.dev/packages/installed_apps/install)
2. [Example Project](https://github.com/sharmadhiraj/installed_apps/tree/master/example)

## Usage

#### Get List of Installed Apps

``` dart
List<AppInfo> apps = await InstalledApps.getInstalledApps(
	bool excludeSystemApps,
	bool excludeUnlaunchable,
	bool withIcon,
	String packageNamePrefix
);
```

Use `packageNamePrefix` to filter apps with package names starting with a specific prefix.

#### Get App Info with Package Name

``` dart
AppInfo app = await InstalledApps.getAppInfo(String packageName);
```

#### AppInfo model class

``` dart
class AppInfo {
  String name;
  Uint8List? icon;
  String packageName;
  String versionName;
  int versionCode;
  BuiltWith builtWith;
  int installedTimestamp;
}
```

#### Start App with Package Name

``` dart
InstalledApps.startApp(String packageName);
```

#### Open App Settings Screen with Package Name

``` dart
InstalledApps.openSettings(String packageName);
```

#### Check if App is a System App

``` dart
bool isSystemApp = await InstalledApps.isSystemApp(String packageName);
```

#### Uninstall App

``` dart
bool uninstallIsSuccessful = await InstalledApps.uninstallApp(String packageName);
```

#### Check if App is Installed

``` dart 
bool appIsInstalled = await InstalledApps.isAppInstalled(String packageName);
```

<hr/>

I'm continuously improving the plugin. If you have any feedback, issues, or suggestions, don't
hesitate to reach out. Happy coding!
