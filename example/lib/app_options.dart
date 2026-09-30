import 'package:installed_apps/installed_apps.dart';

class AppOptions {
  final bool excludeSystemApps;
  final bool excludeNonLaunchableApps;
  final bool withIcon;
  final bool detectPlatformType;
  final PlatformType? platformType;
  final String packageNamePrefix;
  final String packageNames;

  const AppOptions({
    this.excludeSystemApps = true,
    this.excludeNonLaunchableApps = true,
    this.withIcon = true,
    this.detectPlatformType = true,
    this.platformType,
    this.packageNamePrefix = "",
    this.packageNames = "",
  });

  List<String>? get packageNameList {
    final List<String> names = packageNames
        .split(",")
        .map((name) => name.trim())
        .where((name) => name.isNotEmpty)
        .toList();
    return names.isEmpty ? null : names;
  }

  Future<List<AppInfo>> load() {
    return InstalledApps.getInstalledApps(
      excludeSystemApps: excludeSystemApps,
      excludeNonLaunchableApps: excludeNonLaunchableApps,
      withIcon: withIcon,
      packageNamePrefix:
          packageNamePrefix.trim().isEmpty ? null : packageNamePrefix.trim(),
      platformType: platformType,
      detectPlatformType: detectPlatformType,
      packageNames: packageNameList,
    );
  }
}
