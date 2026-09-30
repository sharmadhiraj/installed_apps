package com.sharmadhiraj.installed_apps

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.core.net.toUri
import com.sharmadhiraj.installed_apps.Util.Companion.convertAppToMap
import com.sharmadhiraj.installed_apps.Util.Companion.getLaunchablePackageNames
import com.sharmadhiraj.installed_apps.Util.Companion.getPackageInfo
import com.sharmadhiraj.installed_apps.Util.Companion.isSystemApp
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import java.util.Locale.ENGLISH
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class InstalledAppsPlugin : MethodCallHandler, FlutterPlugin, ActivityAware {

    private var channel: MethodChannel? = null
    private var applicationContext: Context? = null
    private var activity: Activity? = null
    private var executor: ExecutorService? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val context: Context?
        get() = activity ?: applicationContext

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        applicationContext = binding.applicationContext
        executor = Executors.newCachedThreadPool()
        channel = MethodChannel(binding.binaryMessenger, "installed_apps").also {
            it.setMethodCallHandler(this)
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
        channel = null
        executor?.shutdown()
        executor = null
        applicationContext = null
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        activity = activityPluginBinding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        activity = activityPluginBinding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        val context = context
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

                val executor = executor
                if (executor == null) {
                    result.error("ERROR", "Plugin is not attached to an engine", null)
                    return
                }
                executor.execute {
                    try {
                        val apps = getInstalledApps(
                            context,
                            excludeSystemApps,
                            excludeNonLaunchableApps,
                            withIcon,
                            packageNamePrefix,
                            PlatformType.fromString(platformTypeName)
                        )
                        mainHandler.post { result.success(apps) }
                    } catch (e: Exception) {
                        Log.w(TAG, "getInstalledApps: ${e.message}")
                        mainHandler.post { result.error("ERROR", e.message, null) }
                    }
                }
            }

            "startApp" -> {
                val packageName = call.argument<String>("package_name")
                result.success(startApp(context, packageName))
            }

            "openSettings" -> {
                val packageName = call.argument<String>("package_name")
                openSettings(context, packageName)
                result.success(null)
            }

            "toast" -> {
                val message = call.argument<String>("message") ?: ""
                val short = call.argument<Boolean>("short_length") ?: true
                toast(context, message, short)
                result.success(null)
            }

            "getAppInfo" -> {
                val packageName = call.argument<String>("package_name") ?: ""
                result.success(getAppInfo(context.packageManager, packageName))
            }

            "isSystemApp" -> {
                val packageName = call.argument<String>("package_name") ?: ""
                result.success(isSystemApp(getPackageInfo(context, packageName)))
            }

            "uninstallApp" -> {
                val packageName = call.argument<String>("package_name") ?: ""
                result.success(uninstallApp(context, packageName))
            }

            "isAppInstalled" -> {
                val packageName = call.argument<String>("package_name") ?: ""
                result.success(isAppInstalled(context, packageName))
            }

            else -> result.notImplemented()
        }
    }

    private fun getInstalledApps(
        context: Context,
        excludeSystemApps: Boolean,
        excludeNonLaunchableApps: Boolean,
        withIcon: Boolean,
        packageNamePrefix: String,
        platformType: PlatformType?
    ): List<Map<String, Any?>> {
        val packageManager = context.packageManager
        var packageInfos = packageManager.getInstalledPackages(0)

        if (excludeSystemApps) {
            packageInfos =
                packageInfos.filter { packageInfo -> !isSystemApp(packageInfo) }
        }
        val launchablePackageNames = getLaunchablePackageNames(packageManager)
        if (excludeNonLaunchableApps) {
            packageInfos = packageInfos.filter { packageInfo ->
                launchablePackageNames.contains(packageInfo.packageName)
            }
        }
        if (packageNamePrefix.isNotEmpty()) {
            val prefixLower = packageNamePrefix.lowercase(ENGLISH)
            packageInfos = packageInfos.filter { packageInfo ->
                packageInfo.packageName.lowercase(ENGLISH).startsWith(prefixLower)
            }
        }

        if (platformType != null) {
            packageInfos =
                packageInfos.filter { packageInfo ->
                    PlatformTypeUtil.getPlatform(
                        packageManager,
                        packageInfo.applicationInfo
                    ) == platformType.value
                }
        }
        return packageInfos
            .filter { it.applicationInfo != null }
            .map { packageInfo ->
                convertAppToMap(
                    packageManager,
                    packageInfo,
                    withIcon,
                    isSystemAppOverride = if (excludeSystemApps) false else null,
                    isLaunchableOverride = launchablePackageNames.contains(packageInfo.packageName),
                    platformTypeOverride = platformType?.value,
                )
            }
    }

    private fun startApp(context: Context, packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return false
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
                ?: return false
            startActivity(context, launchIntent)
            true
        } catch (e: Exception) {
            Log.w(TAG, "startApp: ${e.message}")
            false
        }
    }

    private fun toast(context: Context, text: String, short: Boolean) {
        Toast.makeText(
            context,
            text,
            if (short) LENGTH_SHORT else LENGTH_LONG
        ).show()
    }

    private fun openSettings(context: Context, packageName: String?) {
        if (!isAppInstalled(context, packageName)) {
            Log.d(TAG, "App $packageName is not installed on this device.")
            return
        }
        val intent = Intent().apply {
            action = ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }
        try {
            startActivity(context, intent)
        } catch (e: Exception) {
            Log.w(TAG, "openSettings: ${e.message}")
        }
    }

    private fun getAppInfo(
        packageManager: PackageManager,
        packageName: String
    ): Map<String, Any?>? {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            convertAppToMap(
                packageManager,
                packageInfo,
                true
            )
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun uninstallApp(context: Context, packageName: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = "package:$packageName".toUri()
            }
            startActivity(context, intent)
            true
        } catch (e: Exception) {
            Log.w(TAG, "uninstallApp: ${e.message}")
            false
        }
    }

    private fun isAppInstalled(context: Context, packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return false
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun startActivity(context: Context, intent: Intent) {
        if (context !is Activity) intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private companion object {
        const val TAG = "InstalledAppsPlugin"
    }
}
