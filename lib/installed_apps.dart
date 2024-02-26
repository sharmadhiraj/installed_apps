import 'package:flutter/services.dart';
import 'package:installed_apps/app_info.dart';

/// A utility class for interacting with installed apps on the device.
class InstalledApps {
  static const MethodChannel _channel = const MethodChannel('installed_apps');

  /// Retrieves a list of installed apps on the device.
  ///
  /// [excludeSystemApps] specifies whether to exclude system apps from the list.
  /// [withIcon] specifies whether to include app icons in the list.
  /// [packageNamePrefix] is an optional parameter to filter apps with package names starting with a specific prefix.
  ///
  /// Returns a list of [AppInfo] objects representing the installed apps.
  static Future<List<AppInfo>> getInstalledApps([
    bool excludeSystemApps = true,
    bool withIcon = false,
    String packageNamePrefix = "",
  ]) async {
    dynamic apps = await _channel.invokeMethod(
      "getInstalledApps",
      {
        "exclude_system_apps": excludeSystemApps,
        "with_icon": withIcon,
        "package_name_prefix": packageNamePrefix,
      },
    );
    return AppInfo.parseList(apps);
  }

  /// Launches an app with the specified package name.
  ///
  /// [packageName] is the package name of the app to launch.
  ///
  /// Returns a boolean indicating whether the operation was successful.
  static Future<bool?> startApp(String packageName) async {
    return _channel.invokeMethod(
      "startApp",
      {"package_name": packageName},
    );
  }

  /// Opens the settings screen (App Info) of an app with the specified package name.
  ///
  /// [packageName] is the package name of the app whose settings screen should be opened.
  static openSettings(String packageName) {
    _channel.invokeMethod(
      "openSettings",
      {"package_name": packageName},
    );
  }

  /// Displays a toast message on the device.
  ///
  /// [message] is the message to display.
  /// [isShortLength] specifies whether the toast should be short or long in duration.
  static toast(String message, bool isShortLength) {
    _channel.invokeMethod(
      "toast",
      {
        "message": message,
        "short_length": isShortLength,
      },
    );
  }

  /// Retrieves information about an app with the specified package name.
  ///
  /// [packageName] is the package name of the app to retrieve information for.
  ///
  /// Returns an [AppInfo] object representing the app.
  static Future<AppInfo> getAppInfo(String packageName) async {
    var app = await _channel.invokeMethod(
      "getAppInfo",
      {"package_name": packageName},
    );
    if (app == null) {
      throw ("App not found with provided package name $packageName");
    } else {
      return AppInfo.create(app);
    }
  }

  /// Checks if an app with the specified package name is a system app.
  ///
  /// [packageName] is the package name of the app to check.
  ///
  /// Returns a boolean indicating whether the app is a system app.
  static Future<bool?> isSystemApp(String packageName) async {
    return _channel.invokeMethod(
      "isSystemApp",
      {"package_name": packageName},
    );
  }

  /// Uninstalls an app with the specified package name.
  ///
  /// [packageName] is the package name of the app to uninstall.
  ///
  /// Returns a boolean indicating whether the uninstallation was successful.
  static Future<bool?> uninstallApp(String packageName) async {
    return _channel.invokeMethod(
      "uninstallApp",
      {"package_name": packageName},
    );
  }

  /// Checks if an app with the specified package name is installed on the device.
  ///
  /// [packageName] is the package name of the app to check.
  ///
  /// Returns a boolean indicating whether the app is installed.
  static Future<bool?> isAppInstalled(String packageName) async {
    return _channel.invokeMethod(
      "isAppInstalled",
      {"package_name": packageName},
    );
  }
}
