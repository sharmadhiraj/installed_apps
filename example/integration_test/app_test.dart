import 'package:flutter_test/flutter_test.dart';
import 'package:installed_apps/installed_apps.dart';
import 'package:integration_test/integration_test.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  const String sampleAppPackageName = "com.sharmadhiraj.installed_apps_example";

  test(
    "getInstalledApps returns at least one app",
    () async {
      final List<AppInfo> apps = await InstalledApps.getInstalledApps();
      expect(apps.isNotEmpty, true);
    },
  );

  test(
    "getInstalledApps with system apps excluded works",
    () async {
      final List<AppInfo> userApps = await InstalledApps.getInstalledApps(
        
      );
      expect(
        userApps.every((a) => !a.isSystemApp),
        true,
      );
    },
  );

  test(
    "getInstalledApps with non-launchable apps excluded works",
    () async {
      final List<AppInfo> launchableApps = await InstalledApps.getInstalledApps(
        
      );
      expect(
        launchableApps.every((a) => a.isLaunchableApp),
        true,
      );
    },
  );

  test(
    "getInstalledApps supports packageNamePrefix",
    () async {
      final List<AppInfo> filtered = await InstalledApps.getInstalledApps(
        packageNamePrefix: "com",
      );
      expect(
        filtered.every((a) => a.packageName.startsWith("com")),
        true,
      );
    },
  );

  test(
    "getAppInfo returns correct app info",
    () async {
      final AppInfo? info = await InstalledApps.getAppInfo(sampleAppPackageName);
      expect(info, isNotNull);
      expect(info!.packageName, sampleAppPackageName);
    },
  );

  test("isAppInstalled returns true for installed app", () async {
    final bool? installed = await InstalledApps.isAppInstalled(sampleAppPackageName);
    expect(installed, true);
  });

  test(
    "isSystemApp returns a boolean",
    () async {
      final bool? result = await InstalledApps.isSystemApp(sampleAppPackageName);
      expect(result, false);
    },
  );

  test(
    "startApp does not throw",
    () async {
      final bool? result = await InstalledApps.startApp(sampleAppPackageName);
      expect(result, true);
    },
  );

  test(
    "openSettings does not throw",
    () async {
      expect(
        () => InstalledApps.openSettings(sampleAppPackageName),
        returnsNormally,
      );
    },
  );

  test(
    "toast does not throw",
    () async {
      expect(
        () => InstalledApps.toast("InstalledApps test", true),
        returnsNormally,
      );
    },
  );
}
