package com.sharmadhiraj.installed_apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import android.util.Log

class Util {
    companion object {
        fun convertAppToMap(
            packageManager: PackageManager,
            packageInfo: PackageInfo,
            withIcon: Boolean,
            isSystemAppOverride: Boolean? = null,
            isLaunchableOverride: Boolean? = null,
            platformTypeOverride: String? = null
        ): HashMap<String, Any?> {
            val app: ApplicationInfo = packageInfo.applicationInfo
            val map = HashMap<String, Any?>()
            map["name"] = packageManager.getApplicationLabel(app)
            map["package_name"] = app.packageName
            map["icon"] =
                if (withIcon) DrawableUtil.drawableToByteArray(app.loadIcon(packageManager))
                else null

            map["version_name"] = packageInfo.versionName
            map["version_code"] = getVersionCode(packageInfo)
            map["platform_type"] =
                platformTypeOverride
                    ?: PlatformTypeUtil.getPlatform(packageManager, app)
            map["installed_timestamp"] = packageInfo.lastUpdateTime
            map["is_system_app"] = isSystemAppOverride ?: isSystemApp(packageInfo)
            map["is_launchable_app"] = isLaunchableOverride
                ?: isLaunchableApp(packageManager, packageInfo.packageName)
//                map["has_multiple_signers"] =
//                    hasMultipleSigners(packageManager, packageInfo.packageName)
//                map["certificate_hashes"] =
//                    getCertificateHashes(packageInfo)
            if (SDK_INT >= Build.VERSION_CODES.O && app.category != ApplicationInfo.CATEGORY_UNDEFINED) {
                map["category"] = app.category
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

        fun isSystemApp(packageInfo: PackageInfo?): Boolean {
            if (packageInfo == null) return false
            return (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        }

        fun isLaunchableApp(packageManager: PackageManager, packageName: String): Boolean {
            return try {
                packageManager.getLaunchIntentForPackage(packageName) != null
            } catch (e: PackageManager.NameNotFoundException) {
                Log.w("InstalledAppsPlugin", "isLaunchableApp: ${e.message}")
                false
            }
        }

//        fun hasMultipleSigners(packageManager: PackageManager, packageName: String): Boolean {
//            return if (SDK_INT >= P) {
//                packageManager
//                    .getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
//                    .signingInfo
//                    .hasMultipleSigners()
//            } else {
//                return false
//            }
//        }
//
//        fun getCertificateHashes(
//            packageInfo: PackageInfo
//        ): List<String> {
//            if (SDK_INT < P) return emptyList()
//            val signingInfo = packageInfo.signingInfo ?: return emptyList()
//            val signatures = if (signingInfo.hasMultipleSigners()) {
//                signingInfo.apkContentsSigners
//            } else {
//                signingInfo.signingCertificateHistory
//            }
//            val hashes = signatures.map { signature ->
//                MessageDigest
//                    .getInstance("SHA-256")
//                    .digest(signature.toByteArray())
//                    .joinToString(":") {
//                        "%02X".format(it)
//                    }
//            }
//            return hashes
//        }

        fun getPackageInfo(context: Context, packageName: String): PackageInfo? {
            return try {
                getPackageManager(context).getPackageInfo(packageName, 0)
            } catch (_: PackageManager.NameNotFoundException) {
                null
            }
        }

        fun getLaunchablePackageNames(packageManager: PackageManager): Set<String> {
            val launchableApps = packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                },
                0
            )
            return launchableApps.map { it.activityInfo.packageName }.toSet()
        }
    }
}
