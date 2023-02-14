package com.android.base

import android.content.Context
import android.content.res.Configuration
import com.android.base.architecture.app.ApplicationDelegate
import com.android.base.architecture.app.BaseAppContext
import com.android.base.architecture.fragment.animator.FragmentAnimator
import com.android.base.architecture.fragment.epoxy.DefaultEpoxyLoadMoreViewFactory
import com.android.base.architecture.fragment.epoxy.EpoxyLoadMoreViewFactory
import com.android.base.architecture.fragment.tools.FragmentConfig
import com.android.base.architecture.ui.list.Paging
import com.android.base.architecture.ui.list.RefreshLoadMoreViewFactory
import com.android.base.architecture.ui.list.RefreshLoadMoreViewFactory.Factory
import com.android.base.architecture.ui.list.RefreshViewFactory
import com.android.base.architecture.ui.loading.LoadingViewHost
import com.ztiany.loadmore.adapter.LoadMode
import com.ztiany.loadmore.adapter.LoadMoreConfig

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

    /** 列表分页起始页。 */
    var defaultPageStart: Int
        set(value) {
            Paging.setDefaultPageStart(value)
        }
        get() {
            return Paging.getDefaultPageStart()
        }

    /** 列表分页大小。 */
    var defaultPageSize: Int
        set(value) {
            Paging.setDefaultPageSize(value)
        }
        get() {
            return Paging.getDefaultPageSize()
        }

    /** 错误类型分类器。 */
    var errorClassifier: ErrorClassifier? = null

    /** dialog 最小展示时间。【单位：毫秒】  */
    var minimalDialogDisplayTime: Long = 500

    /** 用于创建 LoadingView。【必须配置】 */
    var loadingViewHostFactory: ((Context) -> LoadingViewHost)? = null

    /** 用于配置使用 epoxy 时，LoadMore Item 的视图 。 */
    var epoxyLoadMoreViewFactory: EpoxyLoadMoreViewFactory = DefaultEpoxyLoadMoreViewFactory()

    /** [Throwable] 到可读的 [CharSequence] 转换。 */
    var errorConvert: ErrorConvert = object : ErrorConvert {
        override fun convert(throwable: Throwable): CharSequence {
            return throwable.message.toString()
        }
    }

    /** 设置一个默认的布局 id，在使用 Fragments 中相关方法时，如果没有传入特定的容器 id  时，则使用设置的默认布局 id。【必须配置】  */
    var defaultFragmentContainerId: Int
        set(value) {
            FragmentConfig.setDefaultContainerId(value)
        }
        get() {
            return FragmentConfig.defaultContainerId()
        }

    /**设置默认的 Fragment 转场动画 */
    var defaultFragmentAnimator: FragmentAnimator
        set(value) {
            FragmentConfig.setDefaultFragmentAnimator(value)
        }
        get() {
            return FragmentConfig.defaultFragmentAnimator()
        }

    var refreshViewFactory: RefreshViewFactory.Factory?
        set(value) {
            RefreshViewFactory.registerFactory(value)
        }
        get() {
            return RefreshViewFactory.getFactory()
        }

    var refreshLoadViewFactory: Factory?
        set(value) {
            RefreshLoadMoreViewFactory.registerFactory(value)
        }
        get() {
            return RefreshLoadMoreViewFactory.getFactory()
        }

    /** 加载更多触发的方式，默认为 LoadMore Item 被 bind 时。*/
    var loadMoreTriggerByScroll: Boolean = false

    /** 加载更多的方式，默认为滑动到底部时自动加载更多。*/
    @LoadMode var loadMoreMode: Int
        get() = LoadMoreConfig.getLoadMode()
        set(value) {
            LoadMoreConfig.setLoadMode(value)
        }

    ///////////////////////////////////////////////////////////////////////////
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
