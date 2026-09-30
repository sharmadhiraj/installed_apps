## 2.2.0

* Added `packageNames` option to `getInstalledApps` to restrict results to specific packages.
* Added `detectPlatformType` option to `getInstalledApps` to skip the slower platform detection.
* Added `withIcon` option to `getAppInfo`.
* Added `AppInfo.copyWith`, `==` and `hashCode`.
* Fixed Activity leak and stale context after the Activity detaches.
* `getInstalledApps` no longer hangs on native errors and now replies on the main thread.
* `openSettings` and `toast` now complete their native calls.
* Launching settings and uninstall screens works when no Activity is attached.
* Apps whose icon cannot be rendered now return a null icon instead of empty bytes.
* Modernized Android Gradle setup: no pinned AGP/Kotlin, Java 17, minSdk 21.
* Faster platform detection (single pass over APK entries).
* Models and enums are now exported from `installed_apps.dart`.
* Added `AppInfo.toString`, unit tests and a CI workflow.

## 2.1.1

* Internal fixes and improvements

## 2.1.0

* Added app category information to app info
* Improved performance of getInstalledApps
* Internal fixes and improvements

## 2.0.1

* Internal & build issues fixes

## 2.0.0

* `getInstalledApps` now uses named arguments instead of positional.
* Added `excludeNonLaunchableApps` argument.
* Multiple bug fixes and stability improvements.
* Updated README and internal cleanup.

## 1.6.0

* Fixed unresolved reference issue.
* Fixed random ANR

## 1.5.2

* Updated the compile SDK and Gradle versions.

## 1.5.1

* Upgraded Kotlin and compile options versions.
* Return null if app is not found by package name.
* Improved system app detection filter.
* Miscellaneous internal and performance improvements.

## 1.5.0

* Include built with and installed timestamp
* Internal improvements

## 1.4.0

* Fixed issues (13,14) reported on GitHub
* Implemented uninstallApp & isAppInstalled method

## 1.3.1

* Fixed BinaryMessenger Nullability

## 1.3.0

* Android V2 embedding migration
* Fix UI freeze issue during getting installed apps

## 1.2.0

* Null Safety migration

## 1.1.1

* Add getAppInfo() & isSystemApp() method.
* Refactor existing methods

## 1.0.0

* Fixes & Optimization

## 0.0.1

* Get list of installed app
* Start app with package name
