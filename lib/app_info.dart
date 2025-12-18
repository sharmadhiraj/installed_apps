import 'dart:typed_data';

import 'package:installed_apps/app_category.dart';
import 'package:installed_apps/platform_type.dart';

class AppInfo {
  final String name;
  final Uint8List? icon;
  final String packageName;
  final String versionName;
  final int versionCode;
  final PlatformType platformType;
  final int installedTimestamp;
  final bool isSystemApp;
  final bool isLaunchableApp;
  final AppCategory category;

  const AppInfo({
    required this.name,
    required this.icon,
    required this.packageName,
    required this.versionName,
    required this.versionCode,
    required this.platformType,
    required this.installedTimestamp,
    required this.isSystemApp,
    required this.isLaunchableApp,
    required this.category,
  });

  factory AppInfo.create(dynamic data) {
    return AppInfo(
      name: data["name"],
      icon: data["icon"],
      packageName: data["package_name"],
      versionName: data["version_name"] ?? "1.0.0",
      versionCode: data["version_code"] ?? 1,
      platformType: PlatformType.parse(data["platform_type"]),
      installedTimestamp: data["installed_timestamp"] ?? 0,
      isSystemApp: data["is_system_app"] ?? false,
      isLaunchableApp: data["is_launchable_app"] ?? true,
      category: AppCategory.fromValue(data["category"]),
    );
  }

  String getVersionInfo() => "$versionName ($versionCode)";

  static List<AppInfo> parseList(dynamic apps) {
    if (apps == null || apps is! List || apps.isEmpty) return [];
    final List<AppInfo> appInfoList = apps
        .where((element) =>
            element is Map &&
            element.containsKey("name") &&
            element.containsKey("package_name"))
        .map((app) => AppInfo.create(app))
        .toList();
    appInfoList.sort((a, b) => a.name.compareTo(b.name));
    return appInfoList;
  }
}
