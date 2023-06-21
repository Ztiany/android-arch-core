package com.android.base.architecture.fragment.list

import android.view.View
import com.android.base.AndroidSword
import com.android.base.architecture.ui.list.AutoPaging
import com.android.base.architecture.ui.list.ListDataHost
import com.android.base.architecture.ui.list.ListLayoutHost
import com.android.base.architecture.ui.list.PagerSize
import com.android.base.architecture.ui.list.Paging
import com.android.base.architecture.ui.list.RefreshView
import com.android.base.architecture.ui.list.RefreshViewFactory
import com.android.base.architecture.ui.state.OnRetryActionListener
import com.android.base.architecture.ui.state.StateLayout
import com.android.base.architecture.ui.state.StateLayoutConfig
import com.ztiany.loadmore.adapter.LoadMoreController
import com.ztiany.loadmore.adapter.OnLoadMoreListener

class ListLayoutHostConfig {
    var onRetry: ((state: Int) -> Unit)? = null
    var onRefresh: (() -> Unit)? = null
    var onLoadMore: (() -> Unit)? = null
}

/** It is useful when there is more than one list layout in a fragment. */
fun <T> buildListLayoutHost(
    listDataHost: ListDataHost<T>,
    loadMoreController: LoadMoreController?,
    stateLayout: View,
    refreshLayout: View? = null,
    config: ListLayoutHostConfig.() -> Unit,
): ListLayoutHost<T> {

    val stateLayoutImpl = (stateLayout as? StateLayout) ?: throw IllegalStateException("Make sure that stateLayout implements StateLayout.")

    val refreshLayoutImpl = if (refreshLayout != null) {
        RefreshViewFactory.createRefreshView(refreshLayout)
    } else {
        null
    }

    val hostConfig = ListLayoutHostConfig().apply(config)

    refreshLayoutImpl?.setRefreshHandler(object : RefreshView.RefreshHandler() {
        override fun onRefresh() {
            hostConfig.onRefresh?.invoke()
        }

        override fun canRefresh(): Boolean {
            return if (loadMoreController != null && AndroidSword.avoidingRefreshLoadMoreConflict) {
                !loadMoreController.isLoadingMore
            } else true
        }
    })

    stateLayoutImpl.stateLayoutConfig.setStateRetryListener(object : OnRetryActionListener {
        override fun onRetry(state: Int) {
            hostConfig.onRetry?.invoke(state)
        }
    })

    var enableLoadMore = loadMoreController != null

    loadMoreController?.setOnLoadMoreListener(object : OnLoadMoreListener {
        override fun onLoadMore() {
            hostConfig.onLoadMore?.invoke()
        }

        override fun canLoadMore() = if (refreshLayoutImpl != null && AndroidSword.avoidingRefreshLoadMoreConflict) {
            enableLoadMore && !refreshLayoutImpl.isRefreshing
        } else {
            enableLoadMore
        }
    })

    return object : ListLayoutHost<T> {

        val pager = AutoPaging(this, object : PagerSize {
            override fun getSize(): Int {
                return listDataHost.getListSize()
            }
        })

        override fun replaceData(data: List<T>) {
            listDataHost.replaceData(data)
        }

        override fun addData(data: List<T>) {
            listDataHost.addData(data)
        }

        override fun loadMoreCompleted(hasMore: Boolean) {
            loadMoreController?.loadCompleted(hasMore)
        }

        override fun loadMoreFailed() {
            loadMoreController?.loadFail()
        }

        override fun getPager(): Paging {
            return pager
        }

        override fun isEmpty(): Boolean {
            return listDataHost.isEmpty()
        }

        override fun getListSize(): Int {
            return listDataHost.getListSize()
        }

        override fun isLoadingMore(): Boolean {
            return loadMoreController?.isLoadingMore ?: false
        }

        override var isLoadMoreEnable: Boolean
            get() = loadMoreController != null && enableLoadMore
            set(value) {
                enableLoadMore = value
            }

        override fun autoRefresh() {
            refreshLayoutImpl?.autoRefresh()
        }

        override fun refreshCompleted() {
            refreshLayoutImpl?.refreshCompleted()
        }

        override fun isRefreshing(): Boolean {
            return refreshLayoutImpl?.isRefreshing ?: false
        }

        override var isRefreshEnable: Boolean
            get() = refreshLayoutImpl?.isRefreshEnable ?: false
            set(value) {
                refreshLayoutImpl?.isRefreshEnable = value
            }

        override fun showContentLayout() {
            stateLayoutImpl.showContentLayout()
        }

        override fun showLoadingLayout() {
            stateLayoutImpl.showLoadingLayout()
        }

        override fun showEmptyLayout() {
            stateLayoutImpl.showEmptyLayout()
        }

        override fun showErrorLayout() {
            stateLayoutImpl.showErrorLayout()
        }

        override fun showRequesting() {
            stateLayoutImpl.showRequesting()
        }

        override fun showBlank() {
            stateLayoutImpl.showBlank()
        }

        override fun showNetErrorLayout() {
            stateLayoutImpl.showNetErrorLayout()
        }

        override fun showServerErrorLayout() {
            stateLayoutImpl.showServerErrorLayout()
        }

        override fun getStateLayoutConfig(): StateLayoutConfig {
            return stateLayoutImpl.stateLayoutConfig
        }

        override fun currentStatus(): Int {
            return stateLayoutImpl.currentStatus()
        }

    }//object end.

}