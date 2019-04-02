package com.sharmadhiraj.installed_apps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N_MR1
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.widget.Toast
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
        when {
            call.method == "getInstalledApps" -> result.success(getInstalledApps())
            call.method == "startApp" -> {
                val packageName: String? = call.argument("package_name")
                startApp(packageName)
            }
            call.method == "openSettings" -> {
                val packageName: String? = call.argument("package_name")
                openSettings(packageName)
            }
            else -> result.notImplemented()
        }
    }

    private fun getInstalledApps(): List<Map<String, Any?>> {
        val packageManager = getPackageManager()
        val installedApps = packageManager.getInstalledApplications(0)
        return installedApps
                .filter { app -> !isSystemApp(app.packageName) }
                .map { app -> appToMap(packageManager, app) }
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

    @SuppressLint("NewApi")
    private fun startApp(packageName: String?) {
        if (packageName.isNullOrBlank()) {
            toast("Empty or no package name.")
            return
        }
        try {
            val appName = getAppName(packageName)
            toast("Starting app $appName")
            val launchIntent = getPackageManager().getLaunchIntentForPackage(packageName)
            registrar.context().startActivity(launchIntent)
        } catch (e: Exception) {
            toast("Unable to find app with package name : $packageName")
        }
    }

    private fun toast(text: String) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun getAppName(packageName: String?): String {
        return getPackageManager()
                .getApplicationLabel(getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA))
                .toString()
    }

    private fun getContext(): Context {
        return registrar.context()
    }

    private fun getPackageManager(): PackageManager {
        return getContext().packageManager
    }

    @SuppressLint("NewApi")
    private fun isSystemApp(packageName: String): Boolean {
        return getPackageManager().getLaunchIntentForPackage(packageName) == null
    }

    @SuppressLint("InlinedApi")
    private fun openSettings(packageName: String?) {
        val intent = Intent()
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        getContext().startActivity(intent)
    }

}
