package com.sharmadhiraj.installed_apps

import android.app.ActivityManager
import android.view.accessibility.AccessibilityManager
import android.app.AppOpsManager
import android.os.Build
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.app.usage.UsageStatsManager
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import com.sharmadhiraj.installed_apps.Util.Companion.convertAppToMap
import com.sharmadhiraj.installed_apps.Util.Companion.getPackageManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.util.Locale.ENGLISH


class InstalledAppsPlugin() : MethodCallHandler, FlutterPlugin, ActivityAware {

    companion object {

        var context: Context? = null

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            context = registrar.context()
            register(registrar.messenger())
        }

        @JvmStatic
        fun register(messenger: BinaryMessenger) {
            val channel = MethodChannel(messenger, "installed_apps")
            channel.setMethodCallHandler(InstalledAppsPlugin())
        }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        register(binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        context = activityPluginBinding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {}

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        context = activityPluginBinding.activity
    }

    override fun onDetachedFromActivity() {}

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        if (context == null) {
            result.error("", "Something went wrong!", null)
            return
        }
        when (call.method) {
            "getInstalledApps" -> {
                val includeSystemApps = call.argument("exclude_system_apps") ?: true
                val withIcon = call.argument("with_icon") ?: false
                val packageNamePrefix: String = call.argument("package_name_prefix") ?: ""
                val platformTypeName: String = call.argument("platform_type") ?: ""
                Thread {
                   val apps: List<Map<String, Any?>> =
                        getInstalledApps(includeSystemApps, withIcon, packageNamePrefix, PlatformType.fromString(platformTypeName))
                    result.success(apps)
                }.start()
            }

            "getRunningApps" -> {
                val excludeSystemApps = call.argument("exclude_system_apps") ?: true
                val withIcon = call.argument("with_icon") ?: false
                val platformTypeName: String = call.argument("platform_type") ?: ""
                Thread {
                    val apps = getRunningApps(excludeSystemApps, withIcon, PlatformType.fromString(platformTypeName))
                    result.success(apps)
                }.start()
            }
            
            "startApp" -> {
                val packageName: String? = call.argument("package_name")
                result.success(startApp(packageName))
            }

            "openSettings" -> {
                val packageName: String? = call.argument("package_name")
                openSettings(packageName)
            }

            "toast" -> {
                val message = call.argument("message") ?: ""
                val short = call.argument("short_length") ?: true
                toast(message, short)
            }

            "getAppInfo" -> {
                val packageName: String = call.argument("package_name") ?: ""
                val platformTypeName: String = call.argument("platform_type") ?: ""
                val platformType: PlatformType? = PlatformType.fromString(platformTypeName)
                result.success(getAppInfo(getPackageManager(context!!), packageName, platformType))
            }

            "isSystemApp" -> {
                val packageName: String = call.argument("package_name") ?: ""
                result.success(isSystemApp(getPackageManager(context!!), packageName))
            }

            "uninstallApp" -> {
                val packageName: String = call.argument("package_name") ?: ""
                result.success(uninstallApp(packageName))
            }

            "isAppInstalled" -> {
                val packageName: String = call.argument("package_name") ?: ""
                result.success(isAppInstalled(packageName))
            }

            "checkUsageAccessPermission" -> {
                val isGranted = isUsageAccessGranted()
                result.success(isGranted)
            }

            "checkAccessibilityPermission" -> {
                result.success(isAccessibilityPermissionGranted())
            }
            
            "requestAccessibilityPermission" -> {
                checkAndRequestAccessibilityPermission()
                result.success(null) // İşlem başlatıldığı için geri dönecek bir sonuç yok
            }

            "closeBackgroundApps" -> {
                val success = closeBackgroundApps()
                result.success(success)
            }
            
             "openUsageAccessSettings" -> {
                openUsageAccessSettings()
                result.success(null)
            }
             
            else -> result.notImplemented()
        }
    }
    
    private fun openUsageAccessSettings() {
        val intent = Intent().apply {
            flags = FLAG_ACTIVITY_NEW_TASK
            action = ACTION_USAGE_ACCESS_SETTINGS
        }
        
        context!!.startActivity(intent)
    }

    private fun checkAndRequestAccessibilityPermission() {
         val intent = Intent().apply {
            flags = FLAG_ACTIVITY_NEW_TASK
            action = ACTION_ACCESSIBILITY_SETTINGS
        }

        context!!.startActivity(intent)
    }
    
    private fun isAccessibilityPermissionGranted(): Boolean {
        val accessibilityManager =
            context!!.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    
        val enabledServices = Settings.Secure.getString(
            context!!.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        val myServiceName = "${context!!.packageName}/net.permission.man.MyAccessibilityService"
    
        // check: MyAccessibilityService is enabled?
        return enabledServices?.contains(myServiceName) == true &&
                accessibilityManager.isEnabled
    }
    
    private fun closeBackgroundApps(): Boolean {
        if (!isAccessibilityPermissionGranted()) {
            return false
        }
    
        // Kullanıcı izin verdikten sonra çalışan uygulamaları kapatır
        val activityManager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = activityManager.runningAppProcesses ?: return false
    
        runningApps.forEach { processInfo ->
            processInfo.pkgList.forEach { packageName ->
                if (packageName != context!!.packageName) {
                    try {
                        activityManager.killBackgroundProcesses(packageName)
                    } catch (e: Exception) {
                        // Uygulama kapatılamadı
                    }
                }
            }
        }
        return true
    }


    private fun isUsageAccessGranted(): Boolean {
        val appOpsManager = context!!.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context!!.packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context!!.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
 
    private fun getInstalledApps(
        excludeSystemApps: Boolean,
        withIcon: Boolean,
        packageNamePrefix: String,
        platformType: PlatformType?,
    ): List<Map<String, Any?>> {
        val packageManager = getPackageManager(context!!)
        var installedApps = packageManager.getInstalledApplications(PackageManager.GET_PERMISSIONS)
        if (excludeSystemApps)
            installedApps = installedApps.filter { app -> !isSystemApp(packageManager, app.packageName) }
        if (packageNamePrefix.isNotEmpty())
            installedApps = installedApps.filter { app ->
                app.packageName.startsWith(
                    packageNamePrefix.lowercase(ENGLISH)
                )
            }
        return installedApps.map { app -> convertAppToMap(packageManager, app, withIcon, platformType) }
    }

    private fun getRunningApps(
        excludeSystemApps: Boolean,
        withIcon: Boolean,
        platformType: PlatformType?
    ): List<Map<String, Any?>> {
        val packageManager = getPackageManager(context!!)
        val runningApps = mutableListOf<Map<String, Any?>>()
    
        // 1. Kullanıcı İstatistiklerini Kullanma
        val usageStatsManager = context!!.getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val endTime = currentTime
        val startTime = currentTime - 1000 * 60 * 60 * 1 // Last 1 hour
    
        val usageStats = usageStatsManager.queryUsageStats(
            android.app.usage.UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
    
        if (usageStats != null) {
            val uniquePackages = mutableSetOf<String>()
    
            usageStats.forEach { stat ->
                val packageName = stat.packageName
                if (packageName != null && uniquePackages.add(packageName)) {
                    try {
                        val appInfo = packageManager.getApplicationInfo(packageName, 0)
                        val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        if (!excludeSystemApps || !isSystemApp) {
                            val appMap = convertAppToMap(packageManager, appInfo, withIcon, platformType)
                            runningApps.add(appMap)
                        }
                    } catch (e: PackageManager.NameNotFoundException) {
                        // Paket bulunamadı, devam et
                    }
                }
            }
        }
    
        // 2. Çalışan İşlemleri Kullanma (Eski Yöntem)
        val activityManager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val runningProcesses = activityManager.runningAppProcesses
        runningProcesses?.forEach { processInfo ->
            processInfo.pkgList?.forEach { packageName ->
                try {
                    if (!runningApps.any { it["packageName"] == packageName }) { // Eğer daha önce eklenmediyse
                        val appInfo = packageManager.getApplicationInfo(packageName, 0)
                        val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        if (!excludeSystemApps || !isSystemApp) {
                            val appMap = convertAppToMap(packageManager, appInfo, withIcon, platformType)
                            runningApps.add(appMap)
                        }
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    // Uygulama bilgisi bulunamadı, devam et
                }
            }
        }
    
        return runningApps
    }


    private fun startApp(packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return false
        return try {
            val launchIntent = getPackageManager(context!!).getLaunchIntentForPackage(packageName)
            context!!.startActivity(launchIntent)
            true
        } catch (e: Exception) {
            print(e)
            false
        }
    }

    private fun toast(text: String, short: Boolean) {
        Toast.makeText(context!!, text, if (short) LENGTH_SHORT else LENGTH_LONG)
            .show()
    }

    private fun isSystemApp(packageManager: PackageManager, packageName: String): Boolean {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openSettings(packageName: String?) {
        if (!isAppInstalled(packageName)) {
            print("App $packageName is not installed on this device.")
            return;
        }
        val intent = Intent().apply {
            flags = FLAG_ACTIVITY_NEW_TASK
            action = ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }
        context!!.startActivity(intent)
    }

    private fun getAppInfo(
        packageManager: PackageManager,
        packageName: String,
        platformType: PlatformType?
    ): Map<String, Any?>? {
        var installedApps = packageManager.getInstalledApplications(0)
        installedApps = installedApps.filter { app -> app.packageName == packageName }
        return if (installedApps.isEmpty()) null
        else convertAppToMap(packageManager, installedApps[0], true, platformType)
    }

    private fun uninstallApp(packageName: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            context!!.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }


    private fun isAppInstalled(packageName: String?): Boolean {
        val packageManager: PackageManager = context!!.packageManager
        return try {
            packageManager.getPackageInfo(packageName ?: "", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

}
