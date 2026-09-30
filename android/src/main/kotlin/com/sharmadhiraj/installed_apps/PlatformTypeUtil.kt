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

            val metaData = try {
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.GET_META_DATA
                ).metaData
            } catch (_: PackageManager.NameNotFoundException) {
                null
            }

            metaData?.let {
                if (it.containsKey("io.flutter.app.FlutterApplication")) return "flutter"
                if (it.containsKey("com.getcapacitor.BridgeActivity")) return "ionic"
            }

            return scanApkForPlatform(applicationInfo.sourceDir)
        }

        private fun scanApkForPlatform(apkPath: String?): String {
            if (apkPath.isNullOrEmpty()) return "unknown"
            var zipFile: ZipFile? = null
            return try {
                val zip = ZipFile(apkPath)
                zipFile = zip
                run {
                    var flutter = false
                    var reactNative = false
                    var xamarin = false
                    var ionic = false
                    for (entry in zip.entries()) {
                        val name = entry.name
                        when {
                            name.contains("/flutter_assets/") -> flutter = true
                            name.contains("react_native_routes.json") ||
                                    name.contains("libs_reactnativecore_components") ||
                                    name.contains("node_modules_reactnative") -> reactNative = true

                            name.contains("libaot-Xamarin") -> xamarin = true
                            name.contains("node_modules_ionic") -> ionic = true
                        }
                        if (flutter) break
                    }
                    when {
                        flutter -> "flutter"
                        reactNative -> "react_native"
                        xamarin -> "xamarin"
                        ionic -> "ionic"
                        else -> "native_or_others"
                    }
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
