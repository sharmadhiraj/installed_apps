import 'dart:async';

import 'package:flutter/services.dart';

class InstalledApps {
  static const MethodChannel _channel = const MethodChannel('installed_apps');

  static Future<List<dynamic>> get installedApps async {
    return await _channel.invokeMethod('getInstalledApps');
  }
}
