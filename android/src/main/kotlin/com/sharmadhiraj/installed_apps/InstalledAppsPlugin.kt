package com.sharmadhiraj.installed_apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
        map["icon"] = app.icon
        val packageInfo = packageManager.getPackageInfo(app.packageName, 0)
        map["version_name"] = packageInfo.versionName
        map["version_code"] = packageInfo.versionCode
        return map
    }
}
