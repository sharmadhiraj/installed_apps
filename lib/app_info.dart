import 'dart:typed_data';

import 'package:installed_apps/app_category.dart';
import 'package:installed_apps/platform_type.dart';

/// Information about an installed app.
class AppInfo {
  /// Display name of the app.
  final String name;

  /// PNG bytes of the app icon, or null when icons were not requested or could not be loaded.
  final Uint8List? icon;

  /// Unique package name (application id), for example `com.example.app`.
  final String packageName;

  /// Human readable version, for example `1.2.3`.
  final String versionName;

  /// Internal version number of the app.
  final int versionCode;

  /// Framework the app is built with (detected heuristically).
  final PlatformType platformType;

  /// Last update time in milliseconds since epoch.
  final int installedTimestamp;

  /// Whether the app is part of the system image.
  final bool isSystemApp;

  /// Whether the app has a launcher activity and can be started.
  final bool isLaunchableApp;

  /// Store category. [AppCategory.undefined] below Android 8.0 or when not declared.
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
      name: data["name"] ?? "Unknown",
      icon: data["icon"],
      packageName: data["package_name"] ?? "unknown.package",
      versionName: data["version_name"] ?? "1.0.0",
      versionCode: data["version_code"] ?? 1,
      platformType: PlatformType.parse(data["platform_type"]),
      installedTimestamp: data["installed_timestamp"] ?? 0,
      isSystemApp: data["is_system_app"] ?? false,
      isLaunchableApp: data["is_launchable_app"] ?? true,
      category: AppCategory.fromValue(data["category"]),
    );
  }

  AppInfo copyWith({
    String? name,
    Uint8List? icon,
    String? packageName,
    String? versionName,
    int? versionCode,
    PlatformType? platformType,
    int? installedTimestamp,
    bool? isSystemApp,
    bool? isLaunchableApp,
    AppCategory? category,
  }) {
    return AppInfo(
      name: name ?? this.name,
      icon: icon ?? this.icon,
      packageName: packageName ?? this.packageName,
      versionName: versionName ?? this.versionName,
      versionCode: versionCode ?? this.versionCode,
      platformType: platformType ?? this.platformType,
      installedTimestamp: installedTimestamp ?? this.installedTimestamp,
      isSystemApp: isSystemApp ?? this.isSystemApp,
      isLaunchableApp: isLaunchableApp ?? this.isLaunchableApp,
      category: category ?? this.category,
    );
  }

  /// Version as `versionName (versionCode)`.
  String getVersionInfo() => "$versionName ($versionCode)";

  @override
  bool operator ==(Object other) =>
      other is AppInfo &&
      other.packageName == packageName &&
      other.versionCode == versionCode &&
      other.versionName == versionName;

  @override
  int get hashCode => Object.hash(packageName, versionCode, versionName);

  @override
  String toString() =>
      "AppInfo(name: $name, packageName: $packageName, version: ${getVersionInfo()}, "
      "platformType: $platformType, isSystemApp: $isSystemApp, category: $category)";

  /// Parses a list returned by the native side, skipping invalid entries. Sorted by name.
  static List<AppInfo> parseList(dynamic apps) {
    if (apps == null || apps is! List || apps.isEmpty) return [];
    final List<AppInfo> appInfoList = apps
        .where(
          (element) =>
              element is Map &&
              element.containsKey("name") &&
              element.containsKey("package_name"),
        )
        .map(AppInfo.create)
        .toList()
      ..sort((a, b) => a.name.compareTo(b.name));
    return appInfoList;
  }
}
