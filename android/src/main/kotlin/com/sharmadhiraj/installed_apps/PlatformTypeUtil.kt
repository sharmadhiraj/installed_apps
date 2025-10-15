package com.sharmadhiraj.installed_apps

import android.content.pm.ApplicationInfo
import java.util.zip.ZipFile

class PlatformTypeUtil {

    companion object {

        fun getPlatform(applicationInfo: ApplicationInfo): String {
            val apkPath = applicationInfo.sourceDir
            var zipFile: ZipFile? = null
            return try {
                zipFile = ZipFile(apkPath)
                val entries = zipFile.entries()
                    .asSequence()
                    .map { it.name }
                    .toList()

                when {
                    entries.any { it.contains("/flutter_assets/") } -> "flutter"
                    entries.any {
                        it.contains("react_native_routes.json") || it.contains("libs_reactnativecore_components") || it.contains(
                            "node_modules_reactnative"
                        )
                    } -> "react_native"

                    entries.any { it.contains("libaot-Xamarin") } -> "xamarin"
                    entries.any { it.contains("node_modules_ionic") } -> "ionic"
                    else -> "native_or_others"
                }
            } finally {
                try {
                    zipFile?.close()
                } catch (_: Exception) {
                }
            }
        }

    }
}
