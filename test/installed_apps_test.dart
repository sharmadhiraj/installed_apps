import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:installed_apps/installed_apps.dart';

void main() {
  const MethodChannel channel = MethodChannel('installed_apps');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await InstalledApps.platformVersion, '42');
  });
}
