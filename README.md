# Installed Apps

Flutter <a href="https://pub.dev/packages/installed_apps" target="_blank">plugin</a> with utility methods related to installed apps on device.
(Currently only Android is supported.)

## Getting Started
1. <a href="https://pub.dev/packages/installed_apps#-installing-tab-" target="_blank">Installation Guide</a>
2. <a href="https://pub.dev/packages/installed_apps#-example-tab-" target="_blank">Example</a>
<hr/>

#### Get list of installed apps 
```
List<AppInfo> apps = await InstalledApps.getInstalledApps(bool excludeSystemApps, bool withIcon, String packageNamePrefix);
```
*Use packageNamePrefix to filter for apps that have package name starting with certain prefix.

#### Get app info with package name 
```
AppInfo app = await InstalledApps.getAppInfo(String packageName);
```

#### Start app with package name
```
InstalledApps.startApp(String packageName)
```
#### Open app settings screen (App Info) with package name
```
InstalledApps.openSettings(String packageName)
```
#### Check if app is system app
```
bool isSystemApp = await InstalledApps.isSystemApp(String packageName)
```

