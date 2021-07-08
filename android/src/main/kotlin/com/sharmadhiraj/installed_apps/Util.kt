package com.sharmadhiraj.installed_apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N_MR1
import android.os.Build.VERSION_CODES.P
import io.flutter.plugin.common.PluginRegistry
import java.io.ByteArrayOutputStream

class Util {

    companion object {

        fun convertAppToMap(
            packageManager: PackageManager,
            app: ApplicationInfo,
        ): HashMap<String, Any?> {
            val map = HashMap<String, Any?>()
            map["name"] = packageManager.getApplicationLabel(app)
            map["packageName"] = app.packageName
            val packageInfo = packageManager.getPackageInfo(app.packageName, 0)
            map["versionName"] = packageInfo.versionName
            map["versionCode"] = getVersionCode(packageInfo)
            map["uid"] = app.uid
            return map
        }

        private fun drawableToByteArray(drawable: Drawable): ByteArray {
            val bitmap = drawable.toBitmap()
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }

        private fun Drawable.toBitmap() =
            if (this is BitmapDrawable && bitmap != null) {
                bitmap
            } else {
                if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                } else {
                    Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
                }.also { bitmap ->
                    val canvas = Canvas(bitmap)
                    setBounds(0, 0, canvas.width, canvas.height)
                    draw(canvas)
                }
            }

        fun getContext(registrar: PluginRegistry.Registrar): Context {
            return registrar.context()
        }

        fun getPackageManager(registrar: PluginRegistry.Registrar): PackageManager {
            return getContext(registrar).packageManager
        }

        @Suppress("DEPRECATION")
        private fun getVersionCode(packageInfo: PackageInfo): Long {
            return if (SDK_INT < P) packageInfo.versionCode.toLong()
            else packageInfo.longVersionCode
        }

    }

}