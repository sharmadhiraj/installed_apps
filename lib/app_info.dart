import 'dart:typed_data';

class AppInfo {
  final String name;
  final Uint8List? icon;
  final String packageName;
  final String versionName;
  final int versionCode;
  final PlatformType platformType;
  final int installedTimestamp;

  const AppInfo({
    required this.name,
    required this.icon,
    required this.packageName,
    required this.versionName,
    required this.versionCode,
    required this.platformType,
    required this.installedTimestamp,
  });

  factory AppInfo.create(dynamic data) {
    return AppInfo(
      name: data["name"],
      icon: data["icon"],
      packageName: data["package_name"],
      versionName: data["version_name"] ?? "1.0.0",
      versionCode: data["version_code"] ?? 1,
      platformType: PlatformType.parse(data["built_with"]),
      installedTimestamp: data["installed_timestamp"] ?? 0,
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

enum PlatformType {
  flutter('flutter', 'Flutter'),
  reactNative('react_native', 'React Native'),
  xamarin('xamarin', 'Xamarin'),
  ionic('ionic', 'Ionic'),
  nativeOrOthers('native_or_others', 'Native or Others');

  final String slug;
  final String name;

  const PlatformType(this.slug, this.name);

  static PlatformType parse(String? raw) {
    return values.firstWhere(
      (e) => e.slug == raw,
      orElse: () => PlatformType.nativeOrOthers,
    );
  }
}
