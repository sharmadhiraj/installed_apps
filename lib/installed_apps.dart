import 'dart:async';

import 'package:flutter/services.dart';
import 'package:installed_apps/app_info.dart';

class InstalledApps {
  static const MethodChannel _channel = MethodChannel('installed_apps');

  static Future<List<AppInfo>> getInstalledApps({
    bool excludeSystemApps = true,
  }) async {
    final appsInfoJson = (await _channel.invokeListMethod<Map>(
      'getInstalledApps',
      {
        'exclude_system_apps': excludeSystemApps,
      },
    ))!;
    return appsInfoJson
        .map((appInfoJson) =>
            AppInfo.fromJson(appInfoJson.cast<String, dynamic>()))
        .toList(growable: false);
  }

  static Future<bool?> startApp(String packageName) => _channel.invokeMethod(
        'startApp',
        {'package_name': packageName},
      );

  static Future<void> openSettings(String packageName) async {
    await _channel.invokeMethod(
      'openSettings',
      {'package_name': packageName},
    );
  }

  static Future<void> toast(
    String message, {
    required bool isShortLength,
  }) async {
    await _channel.invokeMethod(
      'toast',
      {
        'message': message,
        'short_length': isShortLength,
      },
    );
  }

  static Future<AppInfo?> getAppInfo(String packageName) async {
    final appInfoJson = await _channel.invokeMapMethod<String, dynamic>(
      'getAppInfo',
      {'package_name': packageName},
    );
    return appInfoJson == null ? null : AppInfo.fromJson(appInfoJson);
  }

  static Future<bool?> isSystemApp(String packageName) => _channel.invokeMethod(
        'isSystemApp',
        {'package_name': packageName},
      );
}
