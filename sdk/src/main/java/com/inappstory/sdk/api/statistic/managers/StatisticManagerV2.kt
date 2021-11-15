package com.inappstory.sdk.api.statistic.managers

import android.text.TextUtils
import com.inappstory.sdk.InAppStoryManager
import com.inappstory.sdk.api.data.models.SessionData
import com.inappstory.sdk.api.statistic.models.StatisticTaskV2
import com.inappstory.sdk.network.ApiWorker
import com.inappstory.sdk.network.callbacks.EmptyCallback
import com.inappstory.sdk.utils.json.JsonParser
import com.inappstory.sdk.utils.localpreferences.PreferencesDataStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StatisticManagerV2(private val apiWorker: ApiWorker) {
    private val tasks: ArrayList<StatisticTaskV2> = ArrayList<StatisticTaskV2>()
    private val fakeTasks: ArrayList<StatisticTaskV2> = ArrayList<StatisticTaskV2>()
    private val tasksLock = Any()
    private val csLock = Any()

    private val tasksKey = "statisticTasks"
    private val fakeTasksKey = "fakeStatisticTasks"

    private fun addTask(task: StatisticTaskV2, fake: Boolean = false, force: Boolean = false) {
        SessionData.instance?.let {
            if (it.isAllowStatV2() || force)
                synchronized(tasksLock) {
                    if (fake) {
                        fakeTasks.add(task)
                    } else {
                        tasks.add(task)
                    }
                    saveTasksInPrefs(fake)
                }
        }

    }

    @DelicateCoroutinesApi
    private fun saveTasksInPrefs(fake: Boolean = false) {
        val key: String = if (fake)
            fakeTasksKey
        else
            tasksKey
        PreferencesDataStore(apiWorker.context).saveString(key, JsonParser().getJson(tasks))
    }

    fun cleanTasks() {
        synchronized(tasksLock) {
            tasks.clear()
            fakeTasks.clear()
            PreferencesDataStore(apiWorker.context).remove(setOf(tasksKey, fakeTasksKey))
        }
    }

    fun cleanFakeEvents() {
        synchronized(tasksLock) {
            fakeTasks.clear()
            PreferencesDataStore(apiWorker.context).remove(fakeTasksKey)
        }
    }

    var viewed = ArrayList<Int>()
    var prefix = ""
    var currentTime: Long = -1
    var pauseTime: Long = 0
    var cTimes: HashMap<Int, Long> = HashMap()
    var pauseTimer: Long = -1
    var isBackgroundPause = false

    private fun generateBase(task: StatisticTaskV2) {
        task.sessionId = SessionData.instance?.id
        task.userId = InAppStoryManager.uId
        task.timestamp = System.currentTimeMillis() / 1000
    }

    fun addFakeEvents(i: Int, si: Int, st: Int) {
        val task = StatisticTaskV2(
            prefix + "slide",
            storyId = i.toString(),
            slideIndex = si
        )
        currentState?.let {
            task.durationMs = System.currentTimeMillis() - it.startTime
        }
        generateBase(task)
        addTask(task, fake = true)

        val tm = cTimes[i] ?: 0L
        val task2 = StatisticTaskV2(
            prefix + "close",
            storyId = i.toString(),
            cause = "app-close",
            slideIndex = si,
            slideTotal = st,
            durationMs = System.currentTimeMillis() - tm - pauseTime
        )
        generateBase(task2)
        addTask(task2, fake = true)
    }

    fun pauseStoryEvent(withBg: Boolean) {
        if (withBg) {
            isBackgroundPause = true
            pauseTimer = System.currentTimeMillis()
        }
    }

    fun resumeStoryEvent(withBg: Boolean) {
        if (withBg) {
            if (isBackgroundPause) {
                pauseTime += System.currentTimeMillis() - pauseTimer
            }
            isBackgroundPause = false
        }
    }

    fun sendViewStory(i: Int, w: String?) {
        if (!viewed.contains(i)) {
            val task = StatisticTaskV2(
                prefix + "view",
                storyId = i.toString(),
                whence = w
            )
            generateBase(task)
            addTask(task)
            viewed.add(i)
        }
    }

    fun sendGoodsOpen(i: Int, si: Int, wi: String?) {
        val task = StatisticTaskV2(
            prefix + "w-goods-open",
            storyId = i.toString(), slideIndex = si, widgetId = wi
        )
        generateBase(task)
        addTask(task, force = true)
    }

    fun sendGoodsClick(
        i: Int, si: Int,
        wi: String?, sku: String?
    ) {
        val task = StatisticTaskV2(
            prefix + "w-goods-click",
            storyId = i.toString(),
            slideIndex = si,
            widgetId = wi,
            widgetValue = sku
        )
        generateBase(task)
        addTask(task, force = true)
    }

    fun sendViewStory(ids: ArrayList<Int>, w: String?) {
        val localIds = ArrayList<String?>()
        ids.forEach {
            if (!viewed.contains(it)) {
                localIds.add(it.toString())
                viewed.add(it)
            }
        }
        if (localIds.isNotEmpty()) {
            val task = StatisticTaskV2(
                prefix + "view",
                storyId = TextUtils.join(",", localIds),
                whence = w
            )
            generateBase(task)
            addTask(task)
        }
    }

    fun sendReadStory(i: Int) {
        val task = StatisticTaskV2(
            prefix + "read", storyId = i.toString()
        )
        generateBase(task)
        addTask(task)
    }

    fun sendCloseStory(i: Int, c: String?, si: Int?, st: Int?) {
        sendCurrentState()
        val tm = cTimes[i] ?: 0L
        val task = StatisticTaskV2(
            prefix + "close",
            storyId = i.toString(),
            cause = c,
            slideIndex = si,
            slideTotal = st,
            durationMs = System.currentTimeMillis() - tm - pauseTime
        )
        generateBase(task)
        addTask(task)
        pauseTime = 0
    }

    fun sendDeeplinkStory(i: Int, link: String?) {
        val task = StatisticTaskV2(
            prefix + "link", storyId = i.toString(), target = link
        )
        generateBase(task)
        addTask(task)
    }

    fun sendClickLink(storyId: Int) {
        val task = StatisticTaskV2(prefix + "w-link")
        generateBase(task)
        addTask(task)
    }

    fun sendLikeStory(i: Int, si: Int) {
        val task = StatisticTaskV2(
            prefix + "like", storyId = i.toString(), slideIndex = si
        )
        generateBase(task)
        addTask(task)
    }

    fun sendDislikeStory(i: Int, si: Int) {
        val task = StatisticTaskV2(
            prefix + "dislike", storyId = i.toString(), slideIndex = si
        )
        generateBase(task)
        addTask(task)
    }

    fun sendFavoriteStory(i: Int, si: Int) {
        val task = StatisticTaskV2(
            prefix + "favorite", storyId = i.toString(), slideIndex = si
        )
        generateBase(task)
        addTask(task)
    }

    fun sendViewSlide(i: Int, si: Int, t: Long) {
        if (t <= 0) return
        val task = StatisticTaskV2(
            prefix + "slide", storyId = i.toString(), slideIndex = si, durationMs = t
        )
        generateBase(task)
        addTask(task)
    }

    fun sendShareStory(i: Int, si: Int) {
        val task = StatisticTaskV2(
            prefix + "share", storyId = i.toString(), slideIndex = si
        )
        generateBase(task)
        addTask(task)
    }

    fun sendWidgetStoryEvent(name: String?, data: String?) {
        val taskParse =
            JsonParser().jsonToPOJO(data, StatisticTaskV2::class, false)
        taskParse?.let {
            val task = taskParse as StatisticTaskV2
            task.event = name
            generateBase(task)
            addTask(task)
        }

    }

    fun sendGameEvent(name: String?, data: String?) {
        val taskParse =
            JsonParser().jsonToPOJO(data, StatisticTaskV2::class, false)
        taskParse?.let {
            val task = taskParse as StatisticTaskV2
            task.event = name
            generateBase(task)
            addTask(task)
        }
    }

    data class CurrentState(
        var storyId: Int = 0,
        var storyPause: Long = 0,
        var slideIndex: Int = 0,
        var startTime: Long = 0
    )

    private var currentState: CurrentState? = null

    fun sendCurrentState() {
        synchronized(csLock) {
            currentState?.let {
                sendViewSlide(
                    it.storyId,
                    it.slideIndex,
                    System.currentTimeMillis() - it.startTime - it.storyPause
                )
            }
            currentState = null
        }
    }

    fun createCurrentState(stId: Int, ind: Int) {
        synchronized(csLock) {
            pauseTime = 0
            currentState = CurrentState()
            currentState?.storyId = stId
            currentState?.slideIndex = ind
            currentState?.startTime = System.currentTimeMillis()
        }
    }

    private fun sendTask(task: StatisticTaskV2) {
        apiWorker.sendStatV2(
            task.event!!, object : EmptyCallback() {
                override fun onSuccess(response: Any?) {
                    super.onSuccess(response)
                    asyncSendTasks()
                }

                override fun onFailure(status: Int, error: String) {
                    super.onFailure(status, error)
                    Thread.sleep(100)
                    asyncSendTasks()
                }
            },
            task
        )
    }

    @DelicateCoroutinesApi
    private fun sendTasks() {
        var task: StatisticTaskV2? = null
        synchronized(tasksLock) {
            if (tasks.isEmpty()) {
                return@synchronized
            }
            task = tasks.removeAt(0)
            saveTasksInPrefs()
        }
        if (task != null) {
            sendTask(task!!)
        } else {
            Thread.sleep(100)
            asyncSendTasks()
        }
    }

    @DelicateCoroutinesApi
    private fun asyncSendTasks() {
        GlobalScope.launch {
            sendTasks()
        }
    }

    init {
        PreferencesDataStore(apiWorker.context).getString(tasksKey)?.let {
            JsonParser().jsonToPOJO(it, StatisticTaskV2::class, true)?.let { list ->
                tasks.addAll(list as ArrayList<StatisticTaskV2>)
            }
        }
        PreferencesDataStore(apiWorker.context).getString(fakeTasksKey)?.let {
            JsonParser().jsonToPOJO(it, StatisticTaskV2::class, true)?.let { list ->
                tasks.addAll(list as ArrayList<StatisticTaskV2>)
            }
        }
        asyncSendTasks()
    }
}