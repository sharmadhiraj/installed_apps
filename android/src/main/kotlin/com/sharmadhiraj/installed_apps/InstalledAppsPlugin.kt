package com.sharmadhiraj.installed_apps

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
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
import java.util.Locale.ENGLISH
import android.app.AppOpsManager
import java.util.Calendar

class InstalledAppsPlugin() : MethodCallHandler, FlutterPlugin, ActivityAware {

    companion object {
        var context: Context? = null
        private const val CHANNEL_NAME = "installed_apps"
        // Removed legacy 'registerWith' method

        @JvmStatic
        fun register(messenger: BinaryMessenger) {
            val channel = MethodChannel(messenger, CHANNEL_NAME)
            channel.setMethodCallHandler(InstalledAppsPlugin())
        }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        register(binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {}

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
                val onlyVisibleApps = call.argument("only_visible_apps") ?: false
                Thread {
                    val apps: List<Map<String, Any?>> =
                        getInstalledApps(includeSystemApps, withIcon, packageNamePrefix, onlyVisibleApps)
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
                result.success(getAppInfo(getPackageManager(context!!), packageName))
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
            "getMostUsedApps" -> {
                // New functionality: get most used apps
                val limit = call.argument<Int>("limit") ?: 5
                Thread {
                    val apps: List<Map<String, Any?>> = getMostUsedApps(limit)
                    result.success(apps)
                }.start()
            }
            "promptUsageAccess" -> {
                promptUsageAccess()
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }

    private fun getInstalledApps(
        excludeSystemApps: Boolean,
        withIcon: Boolean,
        packageNamePrefix: String,
        onlyVisibleApps: Boolean
    ): List<Map<String, Any?>> {
        val packageManager = getPackageManager(context!!)
        var installedApps = packageManager.getInstalledApplications(0)
        if (excludeSystemApps)
            installedApps = installedApps.filter { app -> !isSystemApp(packageManager, app.packageName) }
        if (packageNamePrefix.isNotEmpty())
            installedApps = installedApps.filter { app ->
                app.packageName.startsWith(packageNamePrefix.lowercase(ENGLISH))
            }
        if (onlyVisibleApps) {
            // Get apps with a MAIN launcher intent
            val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val launchableApps = packageManager.queryIntentActivities(launcherIntent, 0)
            val launchablePackageNames = launchableApps.map { it.activityInfo.packageName }.toSet()
            installedApps = installedApps.filter { app -> launchablePackageNames.contains(app.packageName) }
        }
        
        // Define the daily usage limit in milliseconds (6 hours)
        val DAILY_USAGE_LIMIT_MS = 6 * 60 * 60 * 1000L

        // Retrieve usage stats for the last 24 hours
        val usageStatsManager = context!!.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - (1000 * 60 * 60 * 24)
        val usageStatsList = usageStatsManager?.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            beginTime,
            endTime
        )
        // Create a map from package name to total foreground time
        val usageMap = mutableMapOf<String, Long>()
        usageStatsList?.forEach { usage ->
            usageMap[usage.packageName] = usage.totalTimeInForeground
        }
        
        // Convert each app into a map and add the "daily_limit_ended" flag
        return installedApps.map { app ->
            val appMap = convertAppToMap(packageManager, app, withIcon)
            val foregroundTime = usageMap[app.packageName] ?: 0L
            appMap["daily_limit_ended"] = (foregroundTime >= DAILY_USAGE_LIMIT_MS)
            appMap
        }
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
        Toast.makeText(context!!, text, if (short) LENGTH_SHORT else LENGTH_LONG).show()
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
            return
        }
        val intent = Intent().apply {
            flags = FLAG_ACTIVITY_NEW_TASK
            action = ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }
        context!!.startActivity(intent)
    }

    private fun getAppInfo(packageManager: PackageManager, packageName: String): Map<String, Any?>? {
        var installedApps = packageManager.getInstalledApplications(0)
        installedApps = installedApps.filter { app -> app.packageName == packageName }
        return if (installedApps.isEmpty()) null
        else convertAppToMap(packageManager, installedApps[0], true)
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

    private fun getMostUsedApps(limit: Int): List<Map<String, Any?>> {
        val DAILY_USAGE_LIMIT_MS = 6 * 60 * 60 * 1000L
        val usageStatsManager = context!!.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1) // Get stats for the last day
        val usageStatsMap = usageStatsManager.queryAndAggregateUsageStats(
            calendar.timeInMillis,
            System.currentTimeMillis()
        )
        val sortedStats = usageStatsMap.values.sortedByDescending { it.totalTimeInForeground }
        val packageManager = context!!.packageManager
        return sortedStats.take(limit).mapNotNull { usageStat ->
            try {
                val appInfo = packageManager.getApplicationInfo(usageStat.packageName, 0)
                val appLabel = packageManager.getApplicationLabel(appInfo).toString()
                mutableMapOf<String, Any?>(
                    "name" to appLabel,
                    "package_name" to usageStat.packageName,
                    "total_time_in_foreground" to usageStat.totalTimeInForeground,
                    "daily_limit_ended" to (usageStat.totalTimeInForeground >= DAILY_USAGE_LIMIT_MS)
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }
    }

    private fun hasUsageAccessPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun promptUsageAccess() {
        if (!hasUsageAccessPermission(context!!)) {
            // Launch usage access settings so the user can enable usage stats permission
            val intent = Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = FLAG_ACTIVITY_NEW_TASK
            }
            context!!.startActivity(intent)
        }
    }
}
