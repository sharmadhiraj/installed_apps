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

        fun convertAppToMap(
            packageManager: PackageManager,
            app: ApplicationInfo,
            withIcon: Boolean
        ): HashMap<String, Any?> {
            val map = HashMap<String, Any?>()
            map["name"] = packageManager.getApplicationLabel(app)
            map["package_name"] = app.packageName
            map["icon"] =
                if (withIcon) DrawableUtil.drawableToByteArray(app.loadIcon(packageManager))
                else ByteArray(0)
        
            val packageInfo = packageManager.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS)
            map["version_name"] = packageInfo.versionName
            map["version_code"] = getVersionCode(packageInfo)
            map["built_with"] = BuiltWithUtil.getPlatform(packageInfo.applicationInfo)
            map["installed_timestamp"] = File(packageInfo.applicationInfo.sourceDir).lastModified()
        
            // Uygulamanın izinlerini ekleyelim
            if (packageInfo.requestedPermissions != null) {
                // İzinleri ve durumlarını kontrol et
                val permissionsStatus = packageInfo.requestedPermissions.map { permission ->
                    val isGranted = checkPermissionStatus(packageManager, app.packageName, permission)
                    mapOf("permission" to permission, "granted" to isGranted)
                }
                map["permissions"] = permissionsStatus
            } else {
                map["permissions"] = emptyList<Map<String, Any?>>()
            }
        
            return map
        }
        
        fun checkPermissionStatus(packageManager: PackageManager, packageName: String, permission: String): Boolean {
            // İzni kontrol et
             val permissionCheck = packageManager.checkPermission(permission, packageName)
            return permissionCheck == PackageManager.PERMISSION_GRANTED
        }


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
