package com.android.base.architecture.fragment.list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewbinding.ViewBinding
import com.android.base.AndroidSword
import com.android.base.adapter.DataManager
import com.android.base.architecture.fragment.base.BaseUIFragment
import com.android.base.architecture.ui.CommonId
import com.android.base.architecture.ui.list.ListDataHost
import com.android.base.architecture.ui.list.ListLayoutHost
import com.android.base.architecture.ui.list.Paging
import com.android.base.architecture.ui.state.StateLayoutConfig
import com.ztiany.loadmore.adapter.LoadMoreAdapter
import com.ztiany.loadmore.adapter.LoadMoreController
import kotlin.properties.Delegates

/**
 * 通用的基于 RecyclerView 的列表界面，支持下拉刷新和加载更多。其一般的使用方式如下：
 *
 * 1. 列表数据由 [DataManager] 来管理；
 * 2. 在 [BaseListFragment] 中通过 [lifecycleScope] 来分段加载数据，然后累加到 [DataManager]  中；
 * 3. 在 [BaseListFragment] 被销毁重建（因配置发送改变）后，所有的 UI 状态和已经加载的数据都会被重置，未完成的加载也会被取消。
 * 4. 所有对列表的 Item 的操作，都在 [DataManager] 中进行。
 *
 * 以上是典型的命令式编程。
 *
 * @param <T> 当前列表使用的数据类型。
 * @author Ztiany
 */
abstract class BaseListFragment<T, VB : ViewBinding> : BaseUIFragment<VB>(), ListLayoutHost<T> {

    private var loadMoreImpl: LoadMoreController? = null

    private var listLayoutHostImpl: ListLayoutHost<T> by Delegates.notNull()

    override fun internalOnSetUpCreatedView(view: View, savedInstanceState: Bundle?) {
        super.internalOnSetUpCreatedView(view, savedInstanceState)
        listLayoutHostImpl = provideListImplementation(view, savedInstanceState)
    }

    /**
     *  1. This method will be called before [onViewCreated] and [onSetUpCreatedView].
     *  2. You should invoke [setUpList] to return a real [ListLayoutHost].
     */
    abstract fun provideListImplementation(view: View, savedInstanceState: Bundle?): ListLayoutHost<T>

    /**
     * Call this method before calling to [setUpList]. And assign [RecyclerView]'s [Adapter] with the return value.
     */
    protected fun enableLoadMore(
        adapter: Adapter<*>,
        triggerLoadMoreByScroll: Boolean = AndroidSword.loadMoreTriggerByScroll,
    ): Adapter<*> {
        return LoadMoreAdapter.wrap(adapter, triggerLoadMoreByScroll).apply {
            loadMoreImpl = this
        }
    }

    protected fun setUpList(listDataHost: ListDataHost<T>): ListLayoutHost<T> {
        return buildListLayoutHost(
            listDataHost,
            loadMoreImpl,
            vb.root.findViewById(CommonId.STATE_ID),
            vb.root.findViewById(CommonId.REFRESH_ID)
        ) {
            onRetry = {
                this@BaseListFragment.onRetry(it)
            }
            onRefresh = {
                this@BaseListFragment.onRefresh()
            }
            onLoadMore = {
                this@BaseListFragment.onLoadMore()
            }
        }
    }

    protected open fun onRetry(@StateLayoutConfig.RetryableState state: Int) {
        if (listLayoutHostImpl.isRefreshEnable) {
            if (!isRefreshing()) {
                listLayoutHostImpl.autoRefresh()
            }
        } else {
            onRefresh()
        }
    }

    protected open fun onRefresh() = onStartLoad()

    protected open fun onLoadMore() = onStartLoad()

    /**called by [onRefresh] or [onLoadMore], you can get current loading type from [isRefreshing] or [isLoadingMore].*/
    protected open fun onStartLoad() {}

    override fun replaceData(data: List<T>) = listLayoutHostImpl.replaceData(data)

    override fun addData(data: List<T>) = listLayoutHostImpl.addData(data)

    override fun isEmpty(): Boolean {
        return listLayoutHostImpl.isEmpty()
    }

    override fun getListSize(): Int {
        return listLayoutHostImpl.getListSize()
    }

    override fun isLoadingMore(): Boolean {
        return listLayoutHostImpl.isLoadingMore()
    }

    override fun getPager(): Paging {
        return listLayoutHostImpl.getPager()
    }

    val loadMoreController: LoadMoreController
        get() = loadMoreImpl ?: throw NullPointerException("You didn't enable load-more.")

    override fun loadMoreCompleted(hasMore: Boolean) {
        loadMoreImpl?.loadCompleted(hasMore)
    }

    override fun loadMoreFailed() {
        loadMoreImpl?.loadFail()
    }

    override var isLoadMoreEnable: Boolean
        get() = listLayoutHostImpl.isLoadMoreEnable
        set(value) {
            listLayoutHostImpl.isLoadMoreEnable = value
        }

    override fun autoRefresh() {
        listLayoutHostImpl.autoRefresh()
    }

    override fun refreshCompleted() {
        listLayoutHostImpl.refreshCompleted()
    }

    override fun isRefreshing(): Boolean {
        return listLayoutHostImpl.isRefreshing()
    }

    override fun showContentLayout() {
        listLayoutHostImpl.showContentLayout()
    }

    override fun showLoadingLayout() {
        listLayoutHostImpl.showLoadingLayout()
    }

    override fun showEmptyLayout() {
        listLayoutHostImpl.showEmptyLayout()
    }

    override fun showErrorLayout() {
        listLayoutHostImpl.showErrorLayout()
    }

    override fun showRequesting() {
        listLayoutHostImpl.showRequesting()
    }

    override fun showBlank() {
        listLayoutHostImpl.showBlank()
    }

    override fun showNetErrorLayout() {
        listLayoutHostImpl.showNetErrorLayout()
    }

    override fun showServerErrorLayout() {
        listLayoutHostImpl.showServerErrorLayout()
    }

    override fun getStateLayoutConfig(): StateLayoutConfig {
        return listLayoutHostImpl.stateLayoutConfig
    }

    @StateLayoutConfig.ViewState
    override fun currentStatus(): Int {
        return listLayoutHostImpl.currentStatus()
    }

    override var isRefreshEnable: Boolean
        get() = listLayoutHostImpl.isRefreshEnable
        set(value) {
            listLayoutHostImpl.isRefreshEnable = value
        }

    @Suppress("UNUSED")
    companion object {
        const val CONTENT = StateLayoutConfig.CONTENT
        const val LOADING = StateLayoutConfig.LOADING
        const val ERROR = StateLayoutConfig.ERROR
        const val EMPTY = StateLayoutConfig.EMPTY
        const val NET_ERROR = StateLayoutConfig.NET_ERROR
        const val SERVER_ERROR = StateLayoutConfig.SERVER_ERROR
    }

}