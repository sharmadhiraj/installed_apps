import 'dart:typed_data';

class AppInfo {
  final String name;
  final Uint8List? icon;
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
          name: json['name'] as String,
          icon: json['icon'] as Uint8List?,
          packageName: json['packageName'] as String,
          versionName: json['versionName'] as String,
          versionCode: json['versionCode'] as int,
          uid: json['uid'] as int,
        );

  @Deprecated('Use `versionInfo` instead. Will be removed in the next version.')
  String getVersionInfo() => versionInfo;

  String get versionInfo => '$versionName ($versionCode)';

  @override
  String toString() =>
      'AppInfo{name: $name, icon: <byte list>, packageName: $packageName, versionName: $versionName, versionCode: $versionCode, uid: $uid}';
}
