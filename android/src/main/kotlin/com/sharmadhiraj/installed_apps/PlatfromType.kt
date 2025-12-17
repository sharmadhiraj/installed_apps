package com.sharmadhiraj.installed_apps
enum class PlatformType(val value: String) {
    FLUTTER("flutter"),
    REACT_NATIVE("react_native"),
    XAMARIN("xamarin"),
    IONIC("ionic"),
    NATIVE_OR_OTHERS("native_or_others");

    companion object {
        fun fromString(platform: String): PlatformType? {
            if (platform.isEmpty()) return null;
            return when (platform.lowercase()) {
                "flutter" -> FLUTTER
                "react_native" -> REACT_NATIVE
                "xamarin" -> XAMARIN
                "ionic" -> IONIC
                else -> NATIVE_OR_OTHERS
            }
        }
    }
}