import 'dart:async';

import 'package:flutter/services.dart';

class InstalledApps {
  static const MethodChannel _channel =
      const MethodChannel('installed_apps');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
