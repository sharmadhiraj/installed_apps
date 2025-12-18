enum PlatformType {
  flutter("flutter", "Flutter"),
  reactNative("react_native", "React Native"),
  xamarin("xamarin", "Xamarin"),
  ionic("ionic", "Ionic"),
  nativeOrOthers("native_or_others", "Native or Others");

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
