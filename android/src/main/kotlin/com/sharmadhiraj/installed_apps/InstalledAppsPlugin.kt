package com.sharmadhiraj.installed_apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N_MR1
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.ByteArrayOutputStream


class InstalledAppsPlugin(private val registrar: Registrar) : MethodCallHandler {
    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "installed_apps")
            channel.setMethodCallHandler(InstalledAppsPlugin(registrar))
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "getInstalledApps") {
            val packageManager = registrar.context().packageManager
            val installedApps = packageManager.getInstalledApplications(0)
            result.success(installedApps.map { app -> appToMap(packageManager, app) })
        } else {
            result.notImplemented()
        }
    }

    private fun appToMap(packageManager: PackageManager, app: ApplicationInfo): HashMap<String, Any?> {
        val map = HashMap<String, Any?>()
        map["name"] = packageManager.getApplicationLabel(app)
        map["package_name"] = app.packageName
        map["icon"] = drawableToByteArray(app.loadIcon(packageManager))
        val packageInfo = packageManager.getPackageInfo(app.packageName, 0)
        map["version_name"] = packageInfo.versionName
        map["version_code"] = packageInfo.versionCode
        return map
    }

    private fun drawableToByteArray(drawable: Drawable): ByteArray {
        val bitmap = drawableToBitmap(drawable)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (SDK_INT <= N_MR1)
            return (drawable as BitmapDrawable).bitmap
        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}
