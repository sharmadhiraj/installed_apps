import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:installed_apps/installed_apps.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const MethodChannel channel = MethodChannel("installed_apps");
  final List<MethodCall> calls = [];
  Object? Function(MethodCall call) handler = (_) => null;

  setUp(() {
    calls.clear();
    handler = (_) => null;
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (call) async {
      calls.add(call);
      return handler(call);
    });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  group("AppInfo", () {
    test("create maps all fields", () {
      final AppInfo app = AppInfo.create({
        "name": "Demo",
        "package_name": "com.demo",
        "version_name": "2.0",
        "version_code": 7,
        "platform_type": "flutter",
        "installed_timestamp": 123,
        "is_system_app": true,
        "is_launchable_app": false,
        "category": 4,
      });
      expect(app.name, "Demo");
      expect(app.packageName, "com.demo");
      expect(app.getVersionInfo(), "2.0 (7)");
      expect(app.platformType, PlatformType.flutter);
      expect(app.installedTimestamp, 123);
      expect(app.isSystemApp, true);
      expect(app.isLaunchableApp, false);
      expect(app.category, AppCategory.social);
      expect(app.icon, isNull);
    });

    test("create falls back to defaults for missing fields", () {
      final AppInfo app = AppInfo.create({});
      expect(app.name, "Unknown");
      expect(app.packageName, "unknown.package");
      expect(app.platformType, PlatformType.nativeOrOthers);
      expect(app.category, AppCategory.undefined);
    });

    test("parseList skips invalid entries and sorts by name", () {
      final List<AppInfo> apps = AppInfo.parseList([
        {"name": "Zed", "package_name": "z"},
        {"name": "Alpha", "package_name": "a"},
        {"name": "NoPackage"},
        "not a map",
      ]);
      expect(apps.map((a) => a.name), ["Alpha", "Zed"]);
    });

    test("parseList handles null and non-list input", () {
      expect(AppInfo.parseList(null), isEmpty);
      expect(AppInfo.parseList("x"), isEmpty);
      expect(AppInfo.parseList([]), isEmpty);
    });
  });

  group("enums", () {
    test("PlatformType.parse", () {
      expect(PlatformType.parse("react_native"), PlatformType.reactNative);
      expect(PlatformType.parse("unknown"), PlatformType.nativeOrOthers);
      expect(PlatformType.parse(null), PlatformType.nativeOrOthers);
    });

    test("AppCategory.fromValue", () {
      expect(AppCategory.fromValue(0), AppCategory.game);
      expect(AppCategory.fromValue(8), AppCategory.accessibility);
      expect(AppCategory.fromValue(null), AppCategory.undefined);
      expect(AppCategory.fromValue(99), AppCategory.undefined);
    });
  });

  group("InstalledApps", () {
    test("getInstalledApps sends arguments and parses result", () async {
      handler = (_) => [
            {"name": "Demo", "package_name": "com.demo"},
          ];
      final List<AppInfo> apps = await InstalledApps.getInstalledApps(
        excludeSystemApps: false,
        withIcon: true,
        packageNamePrefix: "com.",
        platformType: PlatformType.flutter,
      );
      expect(apps.single.packageName, "com.demo");
      expect(calls.single.method, "getInstalledApps");
      expect(calls.single.arguments, {
        "exclude_system_apps": false,
        "exclude_non_launchable_apps": true,
        "with_icon": true,
        "package_name_prefix": "com.",
        "platform_type": "flutter",
      });
    });

    test("getInstalledApps returns empty list on error", () async {
      handler = (_) => throw PlatformException(code: "ERROR");
      expect(await InstalledApps.getInstalledApps(), isEmpty);
    });

    test("getAppInfo returns null when not found", () async {
      expect(await InstalledApps.getAppInfo("com.missing"), isNull);
      expect(calls.single.arguments, {"package_name": "com.missing"});
    });

    test("getAppInfo parses result", () async {
      handler = (_) => {"name": "Demo", "package_name": "com.demo"};
      final AppInfo? app = await InstalledApps.getAppInfo("com.demo");
      expect(app?.name, "Demo");
    });

    test("boolean methods return native value", () async {
      handler = (_) => true;
      expect(await InstalledApps.startApp("a"), true);
      expect(await InstalledApps.isSystemApp("a"), true);
      expect(await InstalledApps.uninstallApp("a"), true);
      expect(await InstalledApps.isAppInstalled("a"), true);
    });

    test("boolean methods return null on error", () async {
      handler = (_) => throw PlatformException(code: "ERROR");
      expect(await InstalledApps.startApp("a"), isNull);
      expect(await InstalledApps.isSystemApp("a"), isNull);
      expect(await InstalledApps.uninstallApp("a"), isNull);
      expect(await InstalledApps.isAppInstalled("a"), isNull);
    });

    test("openSettings and toast invoke channel", () async {
      InstalledApps.openSettings("com.demo");
      InstalledApps.toast("hi", true);
      await Future<void>.delayed(Duration.zero);
      expect(calls.map((c) => c.method), ["openSettings", "toast"]);
      expect(calls.last.arguments, {"message": "hi", "short_length": true});
    });
  });
}
