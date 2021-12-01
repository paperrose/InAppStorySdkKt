package com.inappstory.sdk.ui.storiesreader.views.webview

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.webkit.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.ui.storiesreader.views.pager.StoriesReaderPagerViewModel
import com.inappstory.sdk.utils.common.ThreadNavigator
import java.io.FileInputStream

class StoriesWebView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    WebView(context, attrs, defStyleAttr) {
    constructor(context: Context)
            : this(context, null, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, 0) {
    }

    private fun injectUnselectableStyle(html: String): String {
        return html.replace(
            "<head>",
            "<head><style>*{" +
                    "-webkit-touch-callout: none;" +
                    "-webkit-user-select: none;" +
                    "-khtml-user-select: none;" +
                    "-moz-user-select: none;" +
                    "-ms-user-select: none;" +
                    "user-select: none;" +
                    "} </style>"
        )
    }


    var firstLoading = true

    fun loadData(page: String?, pageData: String?) {
        if (firstLoading) {
            firstLoading = false
            page?.let { loadDataWithBaseURL("", it, "text/html; charset=utf-8", "UTF-8", null) }
        } else {
            pageData?.let { replaceHtml(it) }
        }
    }

    override fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        super.loadDataWithBaseURL(
            baseUrl,
            injectUnselectableStyle(data),
            mimeType,
            encoding,
            historyUrl
        )
    }

    private fun oldEscape(raw: String): String? {
        return raw
            .replace("\"".toRegex(), "\\\\\"")
            .replace("\n".toRegex(), " ")
            .replace("\r".toRegex(), " ")
    }

    private fun replaceHtml(page: String) {
        evaluateJavascript("(function(){show_slide(\"" + oldEscape(page) + "\");})()", null)
    }

    fun startSlide() {
        loadUrl("javascript:(function(){story_slide_start('{\"muted\": false}');})()")
    }

    fun stopSlide() {
        loadUrl("javascript:(function(){story_slide_stop();})()")
    }

    fun swipeUpSlide() {
        loadUrl("javascript:window.story_slide_swipe_up()")
    }

    fun loadJsApiResponse(result: String, cb: String) {
        evaluateJavascript("$cb('$result');", null)
    }

    fun resumeVideo() {
        loadUrl("javascript:(function(){story_slide_resume();})()")
    }

    override fun loadUrl(url: String) {
        ThreadNavigator.runInUiThread { super.loadUrl(url) }
    }

    fun gameComplete(data: String?) {
        data?.also { loadUrl("javascript:game_complete('$data')") }
            ?: loadUrl("javascript:game_complete()")

    }

    fun cancelDialog(id: String) {
        loadUrl("javascript:(function(){story_send_text_input_result(\"$id\", \"\");})()")
    }

    fun sendDialog(id: String, data: String) {
        var data = data
        data = data.replace("\n".toRegex(), "<br>")
        loadUrl("javascript:story_send_text_input_result(\"$id\", \"$data\")")
    }


    private val readerViewModel: StoriesReaderPagerViewModel? by lazy(LazyThreadSafetyMode.NONE) {
        findViewTreeViewModelStoreOwner()?.let {
            ViewModelProvider(it)[StoriesReaderPagerViewModel::class.java]
        }
    }

    val viewModel: StoriesWebViewModel? by lazy(LazyThreadSafetyMode.NONE) {
        findViewTreeViewModelStoreOwner()?.let {
            ViewModelProvider(it)[StoriesWebViewModel::class.java]
        }
    }

    private var lastTap: Long = 0

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        readerViewModel?.let {
            if (it.isBlocked()) return false
        }
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN ->
                viewModel?.clickCoordinate = motionEvent.x
            MotionEvent.ACTION_UP -> {
            }
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return super.dispatchTouchEvent(motionEvent)
    }


    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        readerViewModel?.let {
            if (it.isBlocked()) return false
        }
        val c = super.onTouchEvent(motionEvent)
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return true
            }
        }
        return c
    }

    var touchSlider = false

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        readerViewModel?.let {
            if (it.isBlocked()) return false
        }
        val c = super.onInterceptTouchEvent(motionEvent)
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - lastTap < 1500) {
                return false
            }

            //TODO pause slide

            lastTap = System.currentTimeMillis()
        } else if (motionEvent.action == MotionEvent.ACTION_UP
            || motionEvent.action == MotionEvent.ACTION_CANCEL
        ) {
            touchSlider = false
            parentForAccessibility.requestDisallowInterceptTouchEvent(false)
        }
        return c || touchSlider
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewModel?.subscribe()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewModel?.unsubscribe()
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun initSettings() {

        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.textZoom = 100
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settings.offscreenPreRaster = true
        }
    }

    init {
        setBackgroundColor(Color.BLACK)
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        setLayerType(LAYER_TYPE_HARDWARE, null)
        isClickable = true
    }

    var readerPageManager = StoriesWebViewPageManager(context)

    fun setClient(context: Context) {
        addJavascriptInterface(StoriesJSInterface(readerPageManager), "Android")
        webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                val file = readerPageManager.getCurrentFile(url)
                return if (file != null && file.exists()) {
                    try {
                        val ctType = InAppStoryManager.apiWorker?.getContentTypeByUrl(url)
                        WebResourceResponse(
                            ctType, "BINARY",
                            FileInputStream(file)
                        )
                    } catch (e: Exception) {
                        super.shouldInterceptRequest(view, url)
                    }
                } else
                    super.shouldInterceptRequest(view, url)
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                val img = request.url.toString()
                val file = readerPageManager.getCurrentFile(img)
                return if (file != null && file.exists()) {
                    try {
                        val ctType =
                            InAppStoryManager.apiWorker?.getContentTypeByUrl(request.url.toString())
                        WebResourceResponse(
                            ctType, "BINARY",
                            FileInputStream(file)
                        )
                    } catch (e: Exception) {
                        super.shouldInterceptRequest(view, request)
                    }
                } else
                    super.shouldInterceptRequest(view, request)
            }

            override fun onPageFinished(view: WebView, url: String) {}
        }
        webChromeClient = object : WebChromeClient() {
            override fun getDefaultVideoPoster(): Bitmap? {
                return if (super.getDefaultVideoPoster() == null) {
                    val bmp = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bmp)
                    canvas.drawColor(Color.BLACK)
                    bmp
                } else {
                    super.getDefaultVideoPoster()
                }
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                readerPageManager.getProgressBar()?.setProgress(newProgress)
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d(
                    "MyApplication", consoleMessage.message() + " -- From line "
                            + consoleMessage.lineNumber() + " of "
                            + consoleMessage.sourceId()
                )
                return super.onConsoleMessage(consoleMessage)
            }
        }
    }


}