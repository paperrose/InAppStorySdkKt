package com.inappstory.sdk.utils.requestmodels

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.inappstory.sdk.utils.common.Sizes

class DeviceSettings {
    var deviceId: String? = null
    var platform: String? = null
    var model: String? = null
    var manufacturer: String? = null
    var brand: String? = null
    var screenWidth: String? = null
    var screenHeight: String? = null
    var screenDpi: String? = null
    var osVersion: String? = null
    var osSdkVersion: String? = null
    var requestFields: HashMap<String, String>? = null

    companion object {
        private var instance: DeviceSettings = DeviceSettings()

        @SuppressLint("HardwareIds")
        fun getInstance(context: Context): DeviceSettings {

            if (instance.platform == null) {
                instance.deviceId = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                instance.model = Build.MODEL
                instance.manufacturer = Build.MANUFACTURER
                instance.brand = Build.BRAND
                instance.screenWidth = Sizes.getScreenSize(context).x.toString()
                instance.screenHeight = Sizes.getScreenSize(context).y.toString()
                val metrics = context.resources.displayMetrics
                instance.screenDpi = (metrics.density * 160f).toString()
                instance.osVersion = Build.VERSION.CODENAME
                instance.osSdkVersion = Build.VERSION.SDK_INT.toString()
                instance.platform = "android"

            }
            return instance
        }

        fun getFields(context: Context) : HashMap<String, String> {
            if (getInstance(context).requestFields == null) {
                val reqFields = HashMap<String, String>()
                reqFields["platform"] = instance.platform!!
                reqFields["device_id"] = instance.deviceId!!
                reqFields["model"] = instance.model!!
                reqFields["manufacturer"] = instance.manufacturer!!
                reqFields["brand"] = instance.brand!!
                reqFields["screen_width"] = instance.screenWidth!!
                reqFields["screen_height"] = instance.screenHeight!!
                reqFields["screen_dpi"] = instance.screenDpi!!
                reqFields["os_version"] = instance.osVersion!!
                reqFields["os_sdk_version"] = instance.osSdkVersion!!
                instance.requestFields = reqFields
            }
            return instance.requestFields!!
        }
    }
}