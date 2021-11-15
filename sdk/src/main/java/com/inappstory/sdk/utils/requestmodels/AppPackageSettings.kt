package com.inappstory.sdk.utils.requestmodels

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

class AppPackageSettings {
    var appPackageId:String? = null
    var appVersion:String? = null
    var appBuild:String? = null
    var requestFields: HashMap<String, String>? = null

    companion object {
        var instance: AppPackageSettings = AppPackageSettings()
        fun getInstance(context: Context): AppPackageSettings {
            if (instance.appPackageId == null) {
                instance.appPackageId = context.packageName
                var pInfo: PackageInfo? = null
                try {
                    pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
                instance.appVersion = if (pInfo != null) pInfo.versionName else ""
                instance.appBuild = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    pInfo?.longVersionCode?.toString() ?: ""
                } else {
                    pInfo?.versionCode?.toString() ?: ""
                }
            }
            return instance
        }

        fun getFields(context: Context) : HashMap<String, String> {
            if (getInstance(context).requestFields == null) {
                val reqFields = HashMap<String, String>()
                reqFields["app_package_id"] = instance.appPackageId!!
                reqFields["app_version"] = instance.appVersion!!
                reqFields["app_build"] = instance.appBuild!!
                instance.requestFields = reqFields
            }
            return instance.requestFields!!
        }
    }
}