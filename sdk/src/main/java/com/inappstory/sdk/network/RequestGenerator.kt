package com.inappstory.sdk.network

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.webkit.WebSettings
import android.webkit.WebView
import com.inappstory.sdk.BuildConfig
import com.inappstory.sdk.api.statistic.models.ProfilingTask
import com.inappstory.sdk.api.data.models.SessionData
import com.inappstory.sdk.api.data.models.StatisticSessionObject
import com.inappstory.sdk.api.statistic.models.StatisticTaskV2
import com.inappstory.sdk.network.callbacks.*
import com.inappstory.sdk.network.utils.NetworkClient
import com.inappstory.sdk.network.utils.NetworkSettings
import com.inappstory.sdk.network.utils.Request
import com.inappstory.sdk.network.utils.RequestType
import com.inappstory.sdk.utils.json.Ignore
import com.inappstory.sdk.utils.json.SerializedName
import com.inappstory.sdk.utils.requestmodels.AppPackageSettings
import com.inappstory.sdk.utils.requestmodels.DeviceSettings
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.hasAnnotation

class RequestGenerator(var context: Context, var networkSettings: NetworkSettings, var userId: String?) {
    private val networkClient: NetworkClient = NetworkClient()

    companion object {
        const val FEATURES = "animation,data,deeplink,placeholder,webp,resetTimers,gameReader"
    }


    /**
     * UA block
     */


    internal object NewApiWrapper {
        fun getDefaultUserAgent(context: Context?): String {
            return WebSettings.getDefaultUserAgent(context)
        }
    }

    private fun getUAString(context: Context): String? {
        var userAgent = ""
        val agentString = System.getProperty("http.agent")
        if (agentString != null && agentString.isNotEmpty()) {
            var appVersion = BuildConfig.VERSION_CODE
            var appVersionName = BuildConfig.VERSION_NAME
            var appPackageName = ""
            var pInfo: PackageInfo? = null
            try {
                pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                appVersion = pInfo.versionCode
                appVersionName = pInfo.versionName
                appPackageName = pInfo.packageName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            userAgent = ("InAppStorySDK/" + BuildConfig.VERSION_CODE
                    + " " + System.getProperty("http.agent") + " " + " Application/" + appVersion + " (" + appPackageName + " " + appVersionName + ")")
        } else {
            userAgent = getDefaultUserAgentString(context)
        }
        var finalUA: String? = ""
        for (element in userAgent) {
            if (element <= '\u001f' || element >= '\u007f') {
            } else {
                finalUA += element
            }
        }
        return finalUA
    }


    private fun getDefaultUserAgentString(context: Context?): String {
        return try {
            NewApiWrapper.getDefaultUserAgent(context)
        } catch (e: java.lang.Exception) {
            getDefaultUserStringOld(context)
        }
    }

    private fun getDefaultUserStringOld(context: Context?): String {
        return try {
            val constructor = WebSettings::class.java.getDeclaredConstructor(
                Context::class.java,
                WebView::class.java
            )
            constructor.isAccessible = true
            try {
                val settings = constructor.newInstance(context, null)
                settings.userAgentString
            } finally {
                constructor.isAccessible = false
            }
        } catch (e: Exception) {
            return try {
                WebView(context!!).settings.userAgentString
            } catch (e2: Exception) {
                System.getProperty("http.agent") ?: ""
            }
        }
    }

    /**
     * headers and params
     */

    @SuppressLint("HardwareIds")
    private fun setHeaders(request: Request, hasSession: Boolean, hasUserId: Boolean) {
        request.addHeader("Accept", "application/json")
        var language: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.getDefault().toLanguageTag()
        } else {
            Locale.getDefault().language
        }
        request.addHeader("Accept-Language", language)
        request.addHeader("X-APP-PACKAGE-ID", context.packageName)
        request.addHeader("User-Agent", getUAString(context))
        request.addHeader("Authorization", "Bearer " + networkSettings.apiKey)
        request.addHeader("X-Request-ID", UUID.randomUUID().toString())
        request.addHeader(
            "X-Device-Id", Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        )
        if (hasSession)
            request.addHeader("auth-session-id", SessionData.instance?.id)
        if (hasUserId)
            request.addHeader("X-User-id", userId)
    }

    private fun getCC(): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var countryCodeValue = tm.networkCountryIso
        if (countryCodeValue == null || countryCodeValue.isEmpty()) {
            countryCodeValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0].country
            } else {
                context.resources.configuration.locale.country
            }
        }
        return countryCodeValue!!.uppercase()
    }

    private fun getUrl(path: String): String {
        return networkSettings.cmsUrl + path
    }


    private fun getStoriesListQueryParams(tags: String?, fields: String?, isFavorite: Boolean):
            HashMap<String, String> {
        val qParams: HashMap<String, String> = HashMap()
        if (tags != null)
            qParams["tags"] = tags
        if (fields != null)
            qParams["fields"] = fields
        if (isFavorite) {
            qParams["favorite"] = "1"
        } else {
            qParams["favorite"] = "0"
        }
        return qParams
    }

    private fun getRequestFields(value: Any): HashMap<String, String> {
        val res = HashMap<String, String>()
        for (field in value::class.declaredMembers)
            if (field is KMutableProperty<*>) {
                if (field.hasAnnotation<Ignore>()) continue
                var c = field.getter.call(value)
                if (c != null) {
                    var fieldName: String = field.name
                    if (field.hasAnnotation<SerializedName>()) {
                        for (annotate in field.annotations) {
                            if (annotate is SerializedName)
                                fieldName = annotate.value
                        }
                    }
                    res[fieldName] = c.toString()
                }
            }
        return res
    }


    /**
     * requests
     */

    fun getStoryById(id: String, getStoryCallback: GetStoryCallback) {
        val request = Request(
            RequestType.GET,
            getUrl("v2/story/$id"),
            false
        )
        val qParams: HashMap<String, String> = HashMap()
        qParams["src_list"] = "1"
        qParams["expand"] = "slides_html,slides_structure," +
                "layout,slides_duration,src_list"
        request.queryFields = qParams
        setHeaders(request, hasSession = true, hasUserId = true)
        networkClient.enqueue(request, getStoryCallback);
    }

    fun getStoriesList(
        tags: String?,
        getStoriesListCallback: GetStoriesListCallback
    ) {
        val request = Request(
            RequestType.GET,
            getUrl("v2/story"),
            false
        )
        request.queryFields = getStoriesListQueryParams(tags, null, false)
        setHeaders(request, hasSession = true, hasUserId = true)
        networkClient.enqueue(request, getStoriesListCallback);
    }

    fun getStoriesFavList(
        getStoriesListCallback: GetStoriesListCallback
    ) {
        val request = Request(
            RequestType.GET,
            getUrl("v2/story"),
            false
        )
        request.queryFields = getStoriesListQueryParams(null, null, true)
        setHeaders(request, hasSession = true, hasUserId = true)
        networkClient.enqueue(request, getStoriesListCallback);
    }

    fun getStoriesFavImages(
        getStoriesListCallback: GetStoriesListCallback
    ) {
        val request = Request(
            RequestType.GET,
            getUrl("v2/story"),
            false
        )
        request.queryFields = getStoriesListQueryParams(
            null,
            "id,background_color,image", true
        )
        setHeaders(request, hasSession = true, hasUserId = true)
        networkClient.enqueue(request, getStoriesListCallback);
    }

    fun getOnboardingStories(
        tags: String?,
        getStoriesListCallback: GetStoriesListCallback
    ) {
        val request = Request(
            RequestType.GET,
            getUrl("v2/story-onboarding"),
            false
        )

        val qParams: HashMap<String, String> = HashMap()
        if (tags != null)
            qParams["tags"] = tags
        request.queryFields = qParams
        setHeaders(request, hasSession = true, hasUserId = true)
        networkClient.enqueue(request, getStoriesListCallback);

    }


    fun sendProfilingTiming(task: ProfilingTask, callback: EmptyCallback?) {
        val request = Request(
            RequestType.POST,
            getUrl("v2/profiling/timing"),
            false
        )

        val qParams: HashMap<String, String> = HashMap()
        if (task.sessionId.isNullOrEmpty()) {
            qParams["s"] = SessionData.instance!!.id
        } else {
            qParams["s"] = task.sessionId!!
        }
        val cc = getCC()
        qParams["ts"] = "" + System.currentTimeMillis() / 1000
        if (cc != null) qParams["c"] = cc
        qParams["n"] = task.name!!
        qParams["v"] = "" + (task.endTime - task.startTime)
        request.queryFields = qParams
        setHeaders(request, hasSession = true, hasUserId = true)
        networkClient.enqueue(request, callback);
    }


    fun sendStatV2(eventName: String, callback: EmptyCallback?, task: StatisticTaskV2) {
        val request = Request(
            RequestType.GET,
            getUrl("stat/$eventName"),
            true
        )
        val qParams: HashMap<String, String> = HashMap()
        qParams.putAll(getRequestFields(task))
        request.queryFields = qParams
        networkClient.enqueue(request, callback);
    }

    fun sendStoryData(id: String, data: String) {
        val request = Request(
            RequestType.PUT,
            getUrl("v2/story-data/$id"),
            true
        )
        request.bodyFields = HashMap()
        request.bodyFields["data"] = data
        setHeaders(request, hasSession = true, hasUserId = true)
        networkClient.enqueue(request, null);
    }

    fun storyLikeDislike(id: String, value: String, callback: EmptyCallback?) {
        val request = Request(
            RequestType.POST,
            getUrl("v2/story-like/$id"),
            false
        )

        val qParams: HashMap<String, String> = HashMap()
        qParams["value"] = value
        request.queryFields = qParams
        setHeaders(request, hasSession = true, hasUserId = true)
        networkClient.enqueue(request, callback);
    }

    fun storyFavorite(id: String, value: String, callback: EmptyCallback?) {
        val request = Request(
            RequestType.POST,
            getUrl("v2/story-favorite/$id"),
            false
        )

        val qParams: HashMap<String, String> = HashMap()
        qParams["value"] = value
        request.queryFields = qParams
        setHeaders(request, hasSession = true, hasUserId = true)
        networkClient.enqueue(request, callback);
    }

    fun share(id: String, shareCallback: StoryShareCallback) {
        val request = Request(
            RequestType.GET,
            getUrl("v2/story-share/$id"),
            false
        )
        setHeaders(request, hasSession = true, hasUserId = true)
        networkClient.enqueue(request, shareCallback);
    }

    fun openSession(openCallback: SessionOpenCallback) {
        val request = Request(
            RequestType.POST,
            getUrl("v2/session/open"),
            true
        )
        val qParams: HashMap<String, String> = HashMap()
        qParams["expand"] = "cache"
        val bodyParams: HashMap<String, String> = HashMap()
        bodyParams.putAll(AppPackageSettings.getFields(context))
        bodyParams.putAll(DeviceSettings.getFields(context))
        if (userId != null)
            bodyParams["user_id"] = userId!!
        else
            bodyParams["user_id"] = ""
        bodyParams["features"] = FEATURES
        setHeaders(request, hasSession = false, hasUserId = true)
        request.queryFields = qParams
        request.bodyFields = bodyParams
        networkClient.enqueue(request, openCallback);
    }

    fun sendSessionStatistic(data: StatisticSessionObject, callback: EmptyCallback?) {
        val request = Request(
            RequestType.POST,
            getUrl("v2/session/update"),
            false
        )
        val body = networkClient.jsonParser.getJson(data)
        if (body != null)
            request.body = body
        networkClient.enqueue(request, callback);
    }

    fun closeSession(data: StatisticSessionObject?, callback: EmptyCallback?) {
        val request = Request(
            RequestType.POST,
            getUrl("v2/session/close"),
            false
        )
        val body = networkClient.jsonParser.getJson(data)
        if (body != null)
            request.body = body
        networkClient.enqueue(request, callback);
    }

}