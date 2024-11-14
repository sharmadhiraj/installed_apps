package com.sharmadhiraj.installed_apps


enum class PlatformType {
    FLUTTER,
    REACT_NATIVE,
    XAMARIN,
    IONIC,
    NATIVE_OR_OTHERS;

    companion object {
        fun fromString(platform: String): PlatformType {
            return when (platform.lowercase()) {
                "flutter" -> FLUTTER
                "react_native" -> REACT_NATIVE
                "xamarin" -> XAMARIN
                "ionic" -> IONIC
                else -> NATIVE_OR_OTHERS
            }
        }

        fun toString(platform: PlatformType): String {
            return when (platform) {
                FLUTTER -> "flutter"
                REACT_NATIVE -> "react_native"
                XAMARIN -> "xamarin"
                IONIC -> "ionic"
                NATIVE_OR_OTHERS -> "native_or_others"
            }
        }
    }
}