package com.sharmadhiraj.installed_apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import android.util.Log
import java.io.File

class Util {
    companion object {
        fun convertAppToMap(
            packageManager: PackageManager,
            app: ApplicationInfo?,
            withIcon: Boolean,
        ): HashMap<String, Any?> {
            val map = HashMap<String, Any?>()
            if (app != null) {
                map["name"] = packageManager.getApplicationLabel(app)
                map["package_name"] = app.packageName
                map["icon"] =
                    if (withIcon) DrawableUtil.drawableToByteArray(app.loadIcon(packageManager))
                    else ByteArray(0)
                val packageInfo = try {
                    packageManager.getPackageInfo(app.packageName, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.w("InstalledAppsPlugin", "convertAppToMap: ${e.message}")
                    null
                }
                if (packageInfo != null) {
                    map["version_name"] = packageInfo.versionName
                    map["version_code"] = getVersionCode(packageInfo)
                    map["platform_type"] = PlatformTypeUtil.getPlatform(packageInfo.applicationInfo)
                    map["installed_timestamp"] =
                        File(packageInfo.applicationInfo?.sourceDir).lastModified()
                    map["is_system_app"] = isSystemApp(packageManager, packageInfo.packageName)
                    map["is_launchable_app"] =
                        isLaunchableApp(packageManager, packageInfo.packageName)
                }
            } else {
                map["name"] = "Unknown"
                map["package_name"] = "Unknown"
                map["icon"] = ByteArray(0)
            }
            return map
        }

        fun getPackageManager(context: Context): PackageManager {
            return context.packageManager
        }

        @Suppress("DEPRECATION")
        private fun getVersionCode(packageInfo: PackageInfo): Long {
            return if (SDK_INT < P) packageInfo.versionCode.toLong()
            else packageInfo.longVersionCode
        }

        fun isSystemApp(packageManager: PackageManager, packageName: String): Boolean {
            return try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            } catch (e: PackageManager.NameNotFoundException) {
                Log.w("InstalledAppsPlugin", "isSystemApp: ${e.message}")
                false
            }
        }

        fun isLaunchableApp(packageManager: PackageManager, packageName: String): Boolean {
            return try {
                packageManager.getLaunchIntentForPackage(packageName) != null
            } catch (e: PackageManager.NameNotFoundException) {
                Log.w("InstalledAppsPlugin", "isLaunchableApp: ${e.message}")
                false
            }
        }
    }
}
