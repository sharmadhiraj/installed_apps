package com.sharmadhiraj.installed_apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import java.util.zip.ZipFile

class BuiltWithUtil {

    companion object {

        fun getPlatform(applicationInfo: ApplicationInfo): String {
            val apkPath = applicationInfo.sourceDir
            val zipFile = ZipFile(apkPath)
            val entries: List<String> = zipFile.entries().toList().map { entry -> entry.name }
            return if (isFlutterApp(entries)) {
                "flutter"
            } else if (isReactNativeApp(entries)) {
                "react_native"
            } else if (isXamarinApp(entries)) {
                "xamarin"
            } else if (isIonicApp(entries)) {
                "ionic"
            } else {
                "native_or_others"
            }
        }

        private fun isFlutterApp(entries: List<String>): Boolean {
            return contains(entries, "/flutter_assets/")
        }

        private fun isReactNativeApp(entries: List<String>): Boolean {
            return contains(entries, "react_native_routes.json")
                    || contains(entries, "libs_reactnativecore_components")
                    || contains(entries, "node_modules_reactnative")
        }

        private fun isXamarinApp(entries: List<String>): Boolean {
            return contains(entries, "libaot-Xamarin")
        }

        private fun isIonicApp(entries: List<String>): Boolean {
            return contains(entries, "node_modules_ionic")
        }

        private fun contains(entries: List<String>, value: String): Boolean {
            return entries.firstOrNull { entry -> entry.contains(value) } != null
        }

        fun getAppNameFromPackage(context: Context, packageInfo: PackageInfo): String {
            return packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
        }


    }

}