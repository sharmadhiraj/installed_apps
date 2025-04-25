package com.sharmadhiraj.installed_apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import java.io.File

class Util {
    companion object {

        // Convert ApplicationInfo to a map of app details
        fun convertAppToMap(
            packageManager: PackageManager,
            app: ApplicationInfo?,
            withIcon: Boolean,
            platformType: PlatformType?,
        ): HashMap<String, Any?> {
            val map = HashMap<String, Any?>()

            // Ensure app is not null
            if (app != null) {
                map["name"] = packageManager.getApplicationLabel(app) ?: "Unknown"
                map["package_name"] = app.packageName
                map["icon"] =
                    if (withIcon) DrawableUtil.drawableToByteArray(app.loadIcon(packageManager))
                    else ByteArray(0)

                val packageInfo = packageManager.getPackageInfo(app.packageName, 0)
                map["version_name"] = packageInfo.versionName ?: "Unknown"
                map["version_code"] = getVersionCode(packageInfo)
                map["built_with"] = platformType?.value ?: BuiltWithUtil.getPlatform(app)
                map["installed_timestamp"] = File(app.sourceDir).lastModified()
            } else {
                // Handle the case where app is null, returning a map with "null" values
                map["name"] = "Unknown"
                map["package_name"] = "Unknown"
                map["icon"] = ByteArray(0)
                map["version_name"] = "Unknown"
                map["version_code"] = -1
                map["built_with"] = "Unknown"
                map["installed_timestamp"] = -1
            }

            return map
        }

        // Retrieve PackageManager from the context
        fun getPackageManager(context: Context): PackageManager {
            return context.packageManager
        }

        @Suppress("DEPRECATION")
        private fun getVersionCode(packageInfo: PackageInfo): Long {
            return if (SDK_INT < P) packageInfo.versionCode.toLong()
            else packageInfo.longVersionCode
        }
    }
}
