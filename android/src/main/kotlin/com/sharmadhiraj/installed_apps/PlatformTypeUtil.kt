package com.sharmadhiraj.installed_apps

import android.content.pm.ApplicationInfo
import android.util.Log
import java.util.zip.ZipFile

class PlatformTypeUtil {

    companion object {

        fun getPlatform(applicationInfo: ApplicationInfo?): String {
            val apkPath = applicationInfo?.sourceDir ?: return "unknown"
            var zipFile: ZipFile? = null
            return try {
                zipFile = try {
                    ZipFile(apkPath)
                } catch (e: java.util.zip.ZipException) {
                    Log.w("InstalledAppsPlugin", "Invalid APK zip: ${e.message}")
                    return "unknown"
                }
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
            } catch (e: Exception) {
                Log.w("InstalledAppsPlugin", "getPlatform: ${e.message}")
                "unknown"
            } finally {
                try {
                    zipFile?.close()
                } catch (_: Exception) {
                }
            }
        }

    }
}
