import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:installed_apps/app_info.dart';
import 'package:installed_apps/platform_type.dart';

export 'package:installed_apps/app_category.dart';
export 'package:installed_apps/app_info.dart';
export 'package:installed_apps/platform_type.dart';

/// A utility class for interacting with installed apps on the device.
class InstalledApps {
  static const MethodChannel _channel = MethodChannel('installed_apps');

  /// Retrieves a list of installed apps on the device.
  ///
  /// [excludeSystemApps] — whether to exclude system apps from the list. Default is true.
  /// [excludeNonLaunchableApps] — whether to exclude apps that cannot be launched (no launch intent). Default is true.
  /// [withIcon] — whether to include app icons in the list. Default is false.
  /// [packageNamePrefix] — optional prefix to filter apps whose package names start with this value. Default is null.
  /// [platformType] — optional parameter to specify the app platform type. Default is null.
  /// [detectPlatformType] — whether to detect each app's platform type. Default is true.
  /// When false, detection is skipped (faster) and [AppInfo.platformType] is
  /// [PlatformType.nativeOrOthers], unless [platformType] is set, which always needs detection.
  /// [packageNames] — optional list of package names to restrict the result to. Packages that
  /// are not installed are ignored, and an empty list returns an empty result. Default is null.
  ///
  /// Returns a list of [AppInfo] objects representing the installed apps.
  static Future<List<AppInfo>> getInstalledApps({
    bool excludeSystemApps = true,
    bool excludeNonLaunchableApps = true,
    bool withIcon = false,
    String? packageNamePrefix,
    PlatformType? platformType,
    bool detectPlatformType = true,
    List<String>? packageNames,
  }) async {
    if (packageNames != null && packageNames.isEmpty) return [];
    try {
      final dynamic apps = await _channel.invokeMethod(
        "getInstalledApps",
        {
          "exclude_system_apps": excludeSystemApps,
          "exclude_non_launchable_apps": excludeNonLaunchableApps,
          "with_icon": withIcon,
          "package_name_prefix": packageNamePrefix,
          "platform_type": platformType?.slug,
          "detect_platform_type": detectPlatformType,
          "package_names": packageNames,
        },
      );
      return AppInfo.parseList(apps);
    } catch (e) {
      debugPrint("Error fetching installed apps: $e");
      return [];
    }
  }

  /// Launches an app with the specified package name.
  ///
  /// [packageName] is the package name of the app to launch.
  ///
  /// Returns a boolean indicating whether the operation was successful.
  static Future<bool?> startApp(String packageName) async {
    try {
      return await _channel.invokeMethod(
        "startApp",
        {"package_name": packageName},
      );
    } catch (e) {
      debugPrint("Error starting app: $e");
      return null;
    }
  }

  /// Opens the settings screen (App Info) of an app with the specified package name.
  ///
  /// [packageName] is the package name of the app whose settings screen should be opened.
  static void openSettings(String packageName) {
    try {
      _channel.invokeMethod(
        "openSettings",
        {"package_name": packageName},
      );
    } catch (e) {
      debugPrint("Error opening settings: $e");
    }
  }

  /// Displays a toast message on the device.
  ///
  /// [message] is the message to display.
  /// [isShortLength] specifies whether the toast should be short or long in duration.
  static void toast(String message, bool isShortLength) {
    try {
      _channel.invokeMethod(
        "toast",
        {
          "message": message,
          "short_length": isShortLength,
        },
      );
    } catch (e) {
      debugPrint("Error showing toast: $e");
    }
  }

  /// Retrieves information about an app with the specified package name.
  ///
  /// [packageName] is the package name of the app to retrieve information for.
  /// [withIcon] specifies whether to include the app icon. Default is true.
  ///
  /// Returns [AppInfo] for the given package name, or null if not found.
  static Future<AppInfo?> getAppInfo(
    String packageName, {
    bool withIcon = true,
  }) async {
    try {
      final dynamic app = await _channel.invokeMethod(
        "getAppInfo",
        {
          "package_name": packageName,
          "with_icon": withIcon,
        },
      );
      if (app == null) {
        return null;
      } else {
        return AppInfo.create(app);
      }
    } catch (e) {
      debugPrint("Error getting app info: $e");
      return null;
    }
  }

  /// Checks if an app with the specified package name is a system app.
  ///
  /// [packageName] is the package name of the app to check.
  ///
  /// Returns a boolean indicating whether the app is a system app.
  static Future<bool?> isSystemApp(String packageName) async {
    try {
      return await _channel.invokeMethod(
        "isSystemApp",
        {"package_name": packageName},
      );
    } catch (e) {
      debugPrint("Error checking system app: $e");
      return null;
    }
  }

  /// Uninstalls an app with the specified package name.
  ///
  /// [packageName] is the package name of the app to uninstall.
  ///
  /// Returns a boolean indicating whether the uninstallation was successful.
  static Future<bool?> uninstallApp(String packageName) async {
    try {
      return await _channel.invokeMethod(
        "uninstallApp",
        {"package_name": packageName},
      );
    } catch (e) {
      debugPrint("Error uninstalling app: $e");
      return null;
    }
  }

  /// Checks if an app with the specified package name is installed on the device.
  ///
  /// [packageName] is the package name of the app to check.
  ///
  /// Returns a boolean indicating whether the app is installed.
  static Future<bool?> isAppInstalled(String packageName) async {
    try {
      return await _channel.invokeMethod(
        "isAppInstalled",
        {"package_name": packageName},
      );
    } catch (e) {
      debugPrint("Error checking installed app: $e");
      return null;
    }
  }
}
