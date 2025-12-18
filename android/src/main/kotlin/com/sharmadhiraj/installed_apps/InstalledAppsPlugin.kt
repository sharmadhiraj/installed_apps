package com.sharmadhiraj.installed_apps

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.core.net.toUri
import com.sharmadhiraj.installed_apps.Util.Companion.convertAppToMap
import com.sharmadhiraj.installed_apps.Util.Companion.getLaunchablePackageNames
import com.sharmadhiraj.installed_apps.Util.Companion.getPackageInfo
import com.sharmadhiraj.installed_apps.Util.Companion.getPackageManager
import com.sharmadhiraj.installed_apps.Util.Companion.isSystemApp
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import java.util.Locale.ENGLISH

class InstalledAppsPlugin : MethodCallHandler, FlutterPlugin, ActivityAware {

    private lateinit var channel: MethodChannel
    private var context: Context? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = binding.applicationContext
        channel = MethodChannel(binding.binaryMessenger, "installed_apps")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
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
            result.error("ERROR", "Context is null", null)
            return
        }
        when (call.method) {
            "getInstalledApps" -> {
                val excludeSystemApps = call.argument<Boolean>("exclude_system_apps") ?: true
                val excludeNonLaunchableApps =
                    call.argument<Boolean>("exclude_non_launchable_apps") ?: true
                val withIcon = call.argument<Boolean>("with_icon") ?: false
                val packageNamePrefix = call.argument<String>("package_name_prefix") ?: ""
                val platformTypeName = call.argument<String>("platform_type") ?: ""

                Thread {
                    val apps: List<Map<String, Any?>> =
                        getInstalledApps(
                            excludeSystemApps,
                            excludeNonLaunchableApps,
                            withIcon,
                            packageNamePrefix,
                            PlatformType.fromString(platformTypeName)
                        )
                    result.success(apps)
                }.start()
            }

            "startApp" -> {
                val packageName = call.argument<String>("package_name")
                result.success(startApp(packageName))
            }

            "openSettings" -> {
                val packageName = call.argument<String>("package_name")
                openSettings(packageName)
            }

            "toast" -> {
                val message = call.argument<String>("message") ?: ""
                val short = call.argument<Boolean>("short_length") ?: true
                toast(message, short)
            }

            "getAppInfo" -> {
                val packageName = call.argument<String>("package_name") ?: ""
                result.success(getAppInfo(getPackageManager(context!!), packageName))
            }

            "isSystemApp" -> {
                val packageName = call.argument<String>("package_name") ?: ""
                result.success(isSystemApp(getPackageInfo(context!!, packageName)))
            }

            "uninstallApp" -> {
                val packageName = call.argument<String>("package_name") ?: ""
                result.success(uninstallApp(packageName))
            }

            "isAppInstalled" -> {
                val packageName = call.argument<String>("package_name") ?: ""
                result.success(isAppInstalled(packageName))
            }

            else -> result.notImplemented()
        }
    }

    private fun getInstalledApps(
        excludeSystemApps: Boolean,
        excludeNonLaunchableApps: Boolean,
        withIcon: Boolean,
        packageNamePrefix: String,
        platformType: PlatformType?
    ): List<Map<String, Any?>> {
        val packageManager = getPackageManager(context!!)
        val packageInfos = packageManager.getInstalledPackages(0)
        val packageInfoMap = packageInfos.associateBy { it.packageName }
        var installedApps = packageManager.getInstalledApplications(0)

        if (excludeSystemApps) {
            installedApps =
                installedApps.filter { app -> !isSystemApp(packageInfoMap[app.packageName]) }
        }
        val launchablePackageNames = getLaunchablePackageNames(packageManager)
        if (excludeNonLaunchableApps) {
            installedApps = installedApps.filter { app ->
                launchablePackageNames.contains(app.packageName)
            }
        }
        if (packageNamePrefix.isNotEmpty()) {
            val prefixLower = packageNamePrefix.lowercase(ENGLISH)
            installedApps = installedApps.filter { app ->
                app.packageName.lowercase(ENGLISH).startsWith(prefixLower)
            }
        }

        if (platformType != null) {
            installedApps =
                installedApps.filter { app ->
                    PlatformTypeUtil.getPlatform(
                        packageManager,
                        app
                    ) == platformType.value
                }
        }
        return installedApps.map { app ->
            convertAppToMap(
                context!!,
                packageManager,
                app,
                packageInfoMap[app.packageName],
                withIcon,
                isSystemAppOverride = if (excludeSystemApps) false else null,
                isLaunchableOverride = launchablePackageNames.contains(app.packageName),
                platformTypeOverride = platformType?.value,
            )
        }
    }


    private fun startApp(packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return false
        return try {
            val launchIntent = getPackageManager(context!!).getLaunchIntentForPackage(packageName)
            context!!.startActivity(launchIntent)
            true
        } catch (e: Exception) {
            Log.w("InstalledAppsPlugin", "startApp: ${e.message}")
            false
        }
    }

    private fun toast(text: String, short: Boolean) {
        Toast.makeText(
            context!!,
            text,
            if (short) LENGTH_SHORT else LENGTH_LONG
        ).show()
    }

    private fun openSettings(packageName: String?) {
        if (!isAppInstalled(packageName)) {
            Log.d("InstalledAppsPlugin", "App $packageName is not installed on this device.")
            return
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
        packageName: String
    ): Map<String, Any?>? {
        val app = packageManager
            .getInstalledApplications(0)
            .firstOrNull { it.packageName == packageName }

        return app?.let {
            convertAppToMap(
                context!!,
                packageManager,
                it,
                packageManager.getPackageInfo(app.packageName, 0),
                true
            )
        }
    }

    private fun uninstallApp(packageName: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = "package:$packageName".toUri()
            context!!.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.w("InstalledAppsPlugin", "uninstallApp: ${e.message}")
            false
        }
    }

    private fun isAppInstalled(packageName: String?): Boolean {
        val packageManager: PackageManager = getPackageManager(context!!)
        return try {
            packageManager.getPackageInfo(packageName ?: "", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w("InstalledAppsPlugin", "isAppInstalled: ${e.message}")
            false
        }
    }

}
