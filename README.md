# Installed Apps

The **Installed Apps** plugin for Flutter provides utility methods related to installed apps on a
device.

Currently, only Android is supported.

## Getting Started

1. [Installation Guide](https://pub.dev/packages/installed_apps/install)
2. [Example Project](https://github.com/sharmadhiraj/installed_apps/tree/master/example)

## Usage

#### Get List of Installed Apps

``` dart
List<AppInfo> apps = await InstalledApps.getInstalledApps(
	bool excludeSystemApps,
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

I'm always working on making improvements. If you have any feedback, issues, or suggestions, feel
free to reach out. Happy coding!
