package com.android.base.app

import android.content.Context
import android.content.res.Configuration

/**
 * A set of useful tools for android development, just like a sword.
 *
 * @author Ztiany
 */
object AndroidSword {

    /** Application lifecycle delegate */
    private val coreAppDelegate = ApplicationDelegate()

    ///////////////////////////////////////////////////////////////////////////
    // configuration
    ///////////////////////////////////////////////////////////////////////////

    /** 配置异常处理器。 */
    fun crashProcessor(crashProcessor: CrashProcessor) {
        coreAppDelegate.setCrashProcessor(crashProcessor)
    }

    /** 错误类型分类器。 */
    var errorClassifier: ErrorClassifier? = null

    /** dialog 最小展示时间。【单位：毫秒】  */
    var minimalDialogDisplayTime: Long = 500

    /** [Throwable] 到可读的 [CharSequence] 转换。 */
    var errorConvert: ErrorConvert = object : ErrorConvert {
        override fun convert(throwable: Throwable): CharSequence {
            return throwable.message.toString()
        }
    }

    /** 加载更多触发的方式，默认为 LoadMore Item 被 bind 时。*/
    var loadMoreTriggerByScroll: Boolean = false

    ///////////////////////////////////////////////////////////////////
    // lifecycle of application
    ///////////////////////////////////////////////////////////////////////////
    fun attachBaseContext(base: Context) {
        coreAppDelegate.attachBaseContext(base)
    }

    fun onCreate(baseAppContext: BaseAppContext) {
        coreAppDelegate.onCreate(baseAppContext)
    }

    fun onLowMemory() {
        coreAppDelegate.onLowMemory()
    }

    fun onTrimMemory(level: Int) {
        coreAppDelegate.onTrimMemory(level)
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        coreAppDelegate.onConfigurationChanged(newConfig)
    }

    fun onTerminate() {
        coreAppDelegate.onTerminate()
    }

}

interface CrashProcessor {
    fun uncaughtException(thread: Thread, ex: Throwable)
}

interface ErrorClassifier {
    fun isNetworkError(throwable: Throwable): Boolean
    fun isServerError(throwable: Throwable): Boolean
}

interface ErrorConvert {
    fun convert(throwable: Throwable): CharSequence
}
