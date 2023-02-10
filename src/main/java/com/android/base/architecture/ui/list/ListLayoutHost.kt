package com.android.base.architecture.ui.list

import com.android.base.architecture.ui.state.StateLayoutHost

interface ListDataOperator<T> {

    fun replaceData(data: List<T>)

    fun addData(data: List<T>)

    fun isEmpty(): Boolean

}

/**
 * 列表视图行为。
 */
interface ListLayoutHost<T> : StateLayoutHost, ListDataOperator<T> {

    fun loadMoreCompleted(hasMore: Boolean)

    fun loadMoreFailed()

    fun getPager(): Paging

    fun isLoadingMore(): Boolean

    var isLoadMoreEnable: Boolean

}


fun ListLayoutHost<*>.isLoadingFirstPage(): Boolean {
    return isRefreshing() || !isLoadingMore()
}