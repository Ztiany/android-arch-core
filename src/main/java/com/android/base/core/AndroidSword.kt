package com.android.base.core

import android.content.Context
import android.content.res.Configuration
import timber.log.Timber

/**
 * A set of useful tools for android development, just like a sword.
 *
 * @author Ztiany
 */
object AndroidSword {

    fun touchMe() {
        // nothing to do.
    }

    /** Application lifecycle delegate */
    private val coreAppDelegate = ApplicationDelegate()

    ///////////////////////////////////////////////////////////////////////////
    // configuration
    ///////////////////////////////////////////////////////////////////////////

    /** 配置异常处理器。 */
    fun crashProcessor(crashProcessor: CrashProcessor) {
        coreAppDelegate.setCrashProcessor(crashProcessor)
    }

    /**
     * The time to display the dialog at least, even if the network request is completed.
     * It is used to prevent the dialog from flashing.
     */
    var minimalDialogDisplayTime: Long = 500

    /**
     * It is used to classify the error type of the network request.
     */
    var requestErrorClassifier: RequestErrorClassifier? = null

    /**
     * The handler for the error of the network request.
     */
    var requestErrorHandler: RequestErrorHandler = object : RequestErrorHandler {
        override fun handle(throwable: Throwable) {
            return Timber.e("Error: ${throwable.message}")
        }
    }

    /**
     * Load more trigger by scroll or by bind.
     */
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