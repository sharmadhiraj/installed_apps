/// Category an app declares (Android 8.0 and above).
enum AppCategory {
  game(0, "Game"),
  audio(1, "Audio"),
  video(2, "Video"),
  image(3, "Image"),
  social(4, "Social"),
  news(5, "News"),
  maps(6, "Maps"),
  productivity(7, "Productivity"),
  accessibility(8, "Accessibility"),
  undefined(-1, "Undefined");

  /// Android `ApplicationInfo.CATEGORY_*` value.
  final int value;

  /// Readable name.
  final String name;

  const AppCategory(this.value, this.name);

  /// Returns the category for an Android value, or [undefined] if unknown.
  static AppCategory fromValue(int? value) {
    return AppCategory.values.firstWhere(
      (e) => e.value == value,
      orElse: () => AppCategory.undefined,
    );
  }

  @override
  String toString() => name;
}
