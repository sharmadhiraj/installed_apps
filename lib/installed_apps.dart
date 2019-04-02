import 'dart:async';

import 'package:flutter/services.dart';
import 'package:installed_apps/app_info.dart';

class InstalledApps {
  static const MethodChannel _channel = const MethodChannel('installed_apps');

  static Future<List<AppInfo>> getInstalledApps() async {
    List<dynamic> apps = await _channel.invokeMethod('getInstalledApps');
    List<AppInfo> appInfoList = apps.map((app) => AppInfo.create(app)).toList();
    appInfoList.sort((a, b) => a.name.compareTo(b.name));
    return appInfoList;
  }

  static startApp(String packageName) {
    _channel.invokeMethod("startApp", {"package_name": packageName});
  }

  static openSettings(String packageName) {
    _channel.invokeMethod("openSettings", {"package_name": packageName});
  }
}
