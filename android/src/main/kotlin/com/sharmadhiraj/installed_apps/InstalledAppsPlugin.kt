package com.sharmadhiraj.installed_apps

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import com.sharmadhiraj.installed_apps.Util.Companion.convertAppToMap
import com.sharmadhiraj.installed_apps.Util.Companion.getAppIconPng
import com.sharmadhiraj.installed_apps.Util.Companion.getAppIconsPng
import com.sharmadhiraj.installed_apps.Util.Companion.getContext
import com.sharmadhiraj.installed_apps.Util.Companion.getPackageManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class InstalledAppsPlugin(private val registrar: Registrar) : MethodCallHandler {

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "installed_apps")
            channel.setMethodCallHandler(InstalledAppsPlugin(registrar))
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getInstalledApps" -> {
                val includeSystemApps = call.argument("excludeSystemApps") ?: true
                Thread {
                    val appsInfo = getInstalledApps(includeSystemApps)
                    Handler(Looper.getMainLooper()).post { result.success(appsInfo) }
                }.start()
            }
            "startApp" -> {
                val packageName: String? = call.argument("packageName")
                result.success(startApp(packageName))
            }
            "openSettings" -> {
                val packageName: String? = call.argument("packageName")
                openSettings(packageName)
            }
            "getAppInfo" -> {
                val packageName: String = call.argument("packageName") ?: ""
                Thread {
                    val appInfo = getAppInfo(getPackageManager(registrar), packageName)
                    Handler(Looper.getMainLooper()).post { result.success(appInfo) }
                }.start()
            }
            "isSystemApp" -> {
                val packageName: String = call.argument("packageName") ?: ""
                Thread {
                    val isSystemApp = isSystemApp(getPackageManager(registrar), packageName)
                    Handler(Looper.getMainLooper()).post { result.success(isSystemApp) }
                }.start()
            }
            "getAppIconPng" -> {
                val packageName: String = call.argument("packageName") ?: ""
                Thread {
                    val icon = getAppIconPng(getPackageManager(registrar), packageName)
                    Handler(Looper.getMainLooper()).post { result.success(icon) }
                }.start()
            }
            "getAppIconsPng" -> {
                val packageNames: List<String> = call.argument("packageNames") ?: emptyList<String>()
                Thread {
                    val icons = getAppIconsPng(getPackageManager(registrar), packageNames)
                    Handler(Looper.getMainLooper()).post { result.success(icons) }
                }.start()
            }
            else -> result.notImplemented()
        }
    }

    private fun getInstalledApps(excludeSystemApps: Boolean): List<Map<String, Any?>> {
        val packageManager = getPackageManager(registrar)
        return packageManager.getInstalledApplications(0).run {
            if (excludeSystemApps) filterNot { isSystemApp(packageManager, it.packageName) }
            else this
        }.map { convertAppToMap(packageManager, it) }
    }

    private fun startApp(packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return false
        return try {
            val launchIntent = getPackageManager(registrar).getLaunchIntentForPackage(packageName)
            registrar.context().startActivity(launchIntent)
            true;
        } catch (e: Exception) {
            print(e)
            false
        }
    }

    private fun isSystemApp(packageManager: PackageManager, packageName: String) =
        packageManager.getLaunchIntentForPackage(packageName) == null

    private fun openSettings(packageName: String?) {
        val intent = Intent()
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        getContext(registrar).startActivity(intent)
    }

    private fun getAppInfo(packageManager: PackageManager, packageName: String) =
        try {
            convertAppToMap(packageManager, packageManager.getApplicationInfo(packageName, 0));
        } catch (e: PackageManager.NameNotFoundException) {
            null;
        }
}
