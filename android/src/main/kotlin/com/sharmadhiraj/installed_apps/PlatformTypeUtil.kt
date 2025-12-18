package com.sharmadhiraj.installed_apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import java.util.zip.ZipFile

class PlatformTypeUtil {

    companion object {

        fun getPlatform(packageManager: PackageManager, applicationInfo: ApplicationInfo?): String {
            if (applicationInfo == null) return "unknown"
            val packageName = applicationInfo.packageName.lowercase()

            when {
                packageName.contains("flutter") -> return "flutter"
                packageName.contains("react") -> return "react_native"
                packageName.contains("xamarin") -> return "xamarin"
                packageName.contains("ionic") || packageName.contains("capacitor") -> return "ionic"
            }

            val packageInfo = try {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            } catch (_: PackageManager.NameNotFoundException) {
                return "unknown"
            }

            packageInfo.activities?.forEach { activity ->
                val name = activity.name.lowercase()
                when {
                    name.contains("io.flutter.embedding") -> return "flutter"
                    name.contains("com.facebook.react") -> return "react_native"
                    name.contains("mono.android") -> return "xamarin"
                    name.contains("capacitor") || name.contains("cordova") -> return "ionic"
                }
            }

            val appInfo = try {
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            } catch (_: PackageManager.NameNotFoundException) {
                return "unknown"
            }

            appInfo.metaData?.let { meta ->
                if (meta.containsKey("io.flutter.app.FlutterApplication")) return "flutter"
                if (meta.containsKey("com.getcapacitor.BridgeActivity")) return "ionic"
            }


            val apkPath = applicationInfo?.sourceDir ?: return "unknown"
            var zipFile: ZipFile? = null
            return try {
                zipFile = try {
                    ZipFile(apkPath)
                } catch (e: java.util.zip.ZipException) {
                    Log.w("InstalledAppsPlugin", "Invalid APK zip: ${e.message}")
                    return "unknown"
                }
                val entries = zipFile?.entries()
                    ?.asSequence()
                    ?.map { it.name }
                    ?.toList() ?: emptyList<String>()
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
