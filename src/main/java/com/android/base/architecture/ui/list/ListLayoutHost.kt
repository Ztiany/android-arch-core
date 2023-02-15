package com.android.base.architecture.ui.list

import com.android.base.adapter.DataManager
import com.android.base.architecture.ui.state.StateLayoutHost

interface ListDataHost<T> {

    fun replaceData(data: List<T>)

    fun addData(data: List<T>)

    fun isEmpty(): Boolean

    fun getListSize(): Int

}

/**
 * 列表视图行为。
 */
interface ListLayoutHost<T> : StateLayoutHost, ListDataHost<T> {

    fun loadMoreCompleted(hasMore: Boolean)

    fun loadMoreFailed()

    fun getPager(): Paging

    fun isLoadingMore(): Boolean

    var isLoadMoreEnable: Boolean

}

fun ListLayoutHost<*>.isLoadingFirstPage(): Boolean {
    return isRefreshing() || !isLoadingMore()
}

fun <T> DataManager<T>.toListDataHost(): ListDataHost<T> {
    return object : ListDataHost<T> {
        override fun replaceData(data: List<T>) {
            replaceAll(data)
        }

        override fun addData(data: List<T>) {
            addItems(data)
        }

        override fun isEmpty(): Boolean {
            return this@toListDataHost.isEmpty()
        }

        override fun getListSize(): Int {
            return getDataSize()
        }
    }
}