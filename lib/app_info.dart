import 'dart:typed_data';

class AppInfo {
  final String name;
  final Uint8List icon;
  final String packageName;
  final String versionName;
  final int versionCode;
  final int uid;

  const AppInfo({
    required this.name,
    required this.icon,
    required this.packageName,
    required this.versionName,
    required this.versionCode,
    required this.uid,
  });

  AppInfo.fromJson(Map<String, dynamic> json)
      : this(
          name: json['name'],
          icon: json['icon'],
          packageName: json['packageName'],
          versionName: json['versionName'],
          versionCode: json['versionCode'],
          uid: json['uid'],
        );

  @Deprecated('Use `versionInfo` instead. Will be removed in the next version.')
  String getVersionInfo() => versionInfo;

  String get versionInfo => '$versionName ($versionCode)';

  @override
  String toString() =>
      'AppInfo{name: $name, icon: <byte list>, packageName: $packageName, versionName: $versionName, versionCode: $versionCode, uid: $uid}';
}
