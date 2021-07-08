import 'dart:async';

import 'package:flutter/services.dart';
import 'package:installed_apps/app_info.dart';

class InstalledApps {
  static const MethodChannel _channel = const MethodChannel('installed_apps');

  static Future<List<AppInfo>> getInstalledApps([
    bool excludeSystemApps = true,
    bool withIcon = false,
    String packageNamePrefix = "",
  ]) async {
    List<dynamic> appsInfoJson = await (_channel.invokeMethod(
      'getInstalledApps',
      {
        "exclude_system_apps": excludeSystemApps,
        "with_icon": withIcon,
        "package_name_prefix": packageNamePrefix,
      },
    ));
    return appsInfoJson
        .map((appInfoJson) =>
            AppInfo.fromJson(appInfoJson.cast<String, dynamic>()))
        .toList(growable: false);
  }

  static Future<bool?> startApp(String packageName) async {
    return _channel.invokeMethod(
      "startApp",
      {"package_name": packageName},
    );
  }

  static openSettings(String packageName) {
    _channel.invokeMethod(
      "openSettings",
      {"package_name": packageName},
    );
  }

  static toast(String message, bool isShortLength) {
    _channel.invokeMethod(
      "toast",
      {
        "message": message,
        "short_length": isShortLength,
      },
    );
  }

  static Future<AppInfo?> getAppInfo(String packageName) async {
    var appInfoJson = await _channel.invokeMethod(
      "getAppInfo",
      {"package_name": packageName},
    );
    return appInfoJson == null ? null : AppInfo.fromJson(appInfoJson);
  }

  static Future<bool?> isSystemApp(String packageName) async {
    return _channel.invokeMethod(
      "isSystemApp",
      {"package_name": packageName},
    );
  }
}
