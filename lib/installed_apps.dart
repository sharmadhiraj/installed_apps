import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';
import 'package:installed_apps/app_info.dart';

const MethodChannel _channel = MethodChannel('installed_apps');

Future<List<AppInfo>> getInstalledApps({
  bool excludeSystemApps = true,
}) async {
  final appsInfoJson = (await _channel.invokeListMethod<Map>(
    'getInstalledApps',
    {
      'excludeSystemApps': excludeSystemApps,
    },
  ))!;
  return appsInfoJson
      .map((appInfoJson) =>
          AppInfo.fromJson(appInfoJson.cast<String, dynamic>()))
      .toList(growable: false);
}

Future<bool?> startApp(String packageName) => _channel.invokeMethod(
      'startApp',
      {'packageName': packageName},
    );

Future<void> openAppSettings(String packageName) async {
  await _channel.invokeMethod(
    'openSettings',
    {'packageName': packageName},
  );
}

Future<void> toast(
  String message, {
  required bool isShortLength,
}) async {
  await _channel.invokeMethod(
    'toast',
    {
      'message': message,
      'shortSength': isShortLength,
    },
  );
}

Future<AppInfo?> getAppInfo(String packageName) async {
  final appInfoJson = await _channel.invokeMapMethod<String, dynamic>(
    'getAppInfo',
    {'packageName': packageName},
  );
  return appInfoJson == null ? null : AppInfo.fromJson(appInfoJson);
}

Future<bool?> isSystemApp(String packageName) => _channel.invokeMethod(
      'isSystemApp',
      {'packageName': packageName},
    );

Future<Uint8List?> getAppIconPng(String packageName) =>
    _channel.invokeMethod('getAppIconPng', {'packageName': packageName});

Future<List<Uint8List?>> getAppIconsPng(List<String> packageNames) async =>
    (await _channel.invokeListMethod<Uint8List?>(
        'getAppIconsPng', {'packageNames': packageNames}))!;
