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
    List<dynamic> apps = await _channel.invokeMethod(
      'getInstalledApps',
      {
        "exclude_system_apps": excludeSystemApps,
        "with_icon": withIcon,
        "package_name_prefix": packageNamePrefix,
      },
    );
    List<AppInfo> appInfoList = apps.map((app) => AppInfo.create(app)).toList();
    appInfoList.sort((a, b) => a.name!.compareTo(b.name!));
    return appInfoList;
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

  static Future<bool?> isSystemApp(String packageName) async {
    return _channel.invokeMethod(
      "isSystemApp",
      {"package_name": packageName},
    );
  }

  static Future<bool?> uninstallApp(String packageName) async {
    return _channel.invokeMethod(
      "uninstallApp",
      {"package_name": packageName},
    );
  }
}
