import 'dart:typed_data';

class PermissionStatus {
  final String permission;
  final bool granted;

  PermissionStatus({required this.permission, required this.granted});
}

class AppInfo {
  String name;
  Uint8List? icon;
  String packageName;
  String versionName;
  int versionCode;
  BuiltWith builtWith;
  int installedTimestamp;
  List<PermissionStatus> permissions;

  AppInfo({
    required this.name,
    required this.icon,
    required this.packageName,
    required this.versionName,
    required this.versionCode,
    required this.builtWith,
    required this.installedTimestamp,
    required this.permissions,
  });

  factory AppInfo.create(dynamic data) {
    return AppInfo(
      name: data["name"],
      icon: data["icon"],
      packageName: data["package_name"],
      versionName: data["version_name"] ?? "1.0.0",
      versionCode: data["version_code"] ?? 1,
      builtWith: parseBuiltWith(data["built_with"]),
      installedTimestamp: data["installed_timestamp"] ?? 0,
      permissions: (data["permissions"] as List<dynamic>?)
              ?.map((perm) => PermissionStatus(
                    permission: perm["permission"],
                    granted: perm["granted"],
                  ))
              .toList() ?? [],
    );
  }

  String getVersionInfo() {
    return "$versionName ($versionCode)";
  }

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

  static BuiltWith parseBuiltWith(String? builtWithRaw) {
    if (builtWithRaw == "flutter") {
      return BuiltWith.flutter;
    } else if (builtWithRaw == "react_native") {
      return BuiltWith.react_native;
    } else if (builtWithRaw == "xamarin") {
      return BuiltWith.xamarin;
    } else if (builtWithRaw == "ionic") {
      return BuiltWith.ionic;
    }
    return BuiltWith.native_or_others;
  }
}

enum BuiltWith {
  flutter,
  react_native,
  xamarin,
  ionic,
  native_or_others,
}
