/// Framework an app is built with.
enum PlatformType {
  flutter("flutter", "Flutter"),
  reactNative("react_native", "React Native"),
  xamarin("xamarin", "Xamarin"),
  ionic("ionic", "Ionic"),
  nativeOrOthers("native_or_others", "Native or Others");

  /// Identifier used on the native side.
  final String slug;

  /// Readable name.
  final String name;

  const PlatformType(this.slug, this.name);

  /// Returns the type for a slug, or [nativeOrOthers] if unknown.
  static PlatformType parse(String? raw) {
    return values.firstWhere(
      (e) => e.slug == raw,
      orElse: () => PlatformType.nativeOrOthers,
    );
  }
}
