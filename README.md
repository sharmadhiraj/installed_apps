# Installed Apps

Get list of installed apps. (Currently only Android is supported)

## Getting Started
Import package
```
import 'package:installed_apps/installed_apps.dart';
import 'package:installed_apps/app_info.dart';
```
## Get list of installed apps 
```
List<AppInfo> apps = await InstalledApps.getInstalledApps();
```
## Start app with package name
```
InstalledApps.startApp("com.sharmadhiraj.installed_apps")
```
## Open app settings screen (App Info) with package name
```
InstalledApps.openSettings("com.sharmadhiraj.installed_apps")
```

## More updates coming soon !!!