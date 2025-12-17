import 'package:flutter_test/flutter_test.dart';
import 'package:installed_apps/installed_apps.dart';
import 'package:integration_test/integration_test.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  final String sampleAppPackageName = "com.sharmadhiraj.installed_apps_example";

  test(
    "getInstalledApps returns at least one app",
    () async {
      final apps = await InstalledApps.getInstalledApps();
      expect(apps.isNotEmpty, true);
    },
  );

  test(
    "getInstalledApps with system apps excluded works",
    () async {
      final userApps = await InstalledApps.getInstalledApps(
        excludeSystemApps: true,
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
      final launchableApps = await InstalledApps.getInstalledApps(
        excludeNonLaunchableApps: true,
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
      final filtered = await InstalledApps.getInstalledApps(
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
      final info = await InstalledApps.getAppInfo(sampleAppPackageName);
      expect(info, isNotNull);
      expect(info!.packageName, sampleAppPackageName);
    },
  );

  test("isAppInstalled returns true for installed app", () async {
    final installed = await InstalledApps.isAppInstalled(sampleAppPackageName);
    expect(installed, true);
  });

  test(
    "isSystemApp returns a boolean",
    () async {
      final result = await InstalledApps.isSystemApp(sampleAppPackageName);
      expect(result, false);
    },
  );

  test(
    "startApp does not throw",
    () async {
      final result = await InstalledApps.startApp(sampleAppPackageName);
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
