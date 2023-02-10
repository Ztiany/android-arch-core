package com.android.base.architecture.fragment.epoxy

import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.android.base.architecture.ui.list.ListDataOperator
import com.android.base.architecture.ui.list.PagerSize
import com.ztiany.loadmore.adapter.LoadMoreController
import com.ztiany.loadmore.adapter.LoadMoreViewFactory
import com.ztiany.loadmore.adapter.OnLoadMoreListener

abstract class ListEpoxyController<T> : TypedEpoxyController<List<T>>(), ListDataOperator<T>, ListEpoxyControllerInterface, PagerSize {

    final override fun buildModels(data: List<T>) {
        buildListModels(data)
        buildLoadMoreModels()
    }

    private fun buildLoadMoreModels() {
        loadingMoreRow {

        }
    }

    abstract fun buildListModels(data: List<T>)

    override fun replaceData(data: List<T>) {
        setData(data)
    }

    override fun addData(data: List<T>) {
        val currentData: MutableList<T> = currentData?.toMutableList() ?: mutableListOf()
        currentData.removeAll(data)
        currentData.addAll(data)
        setData(currentData)
    }

    override fun isEmpty(): Boolean {
        val list = currentData
        return list == null || list.isEmpty()
    }

    override fun getDataSize(): Int {
        return currentData?.size ?: 0
    }

    ///////////////////////////////////////////////////////////////////////////
    // LoadMore
    ///////////////////////////////////////////////////////////////////////////

    override fun setUpLoadMore(recyclerView: RecyclerView, triggerLoadMoreByScroll: Boolean): LoadMoreController {
        return object : LoadMoreController {

            override fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener?) {
                TODO("Not yet implemented")
            }

            override fun loadFail() {
                TODO("Not yet implemented")
            }

            override fun loadCompleted(hasMore: Boolean) {
                TODO("Not yet implemented")
            }

            override fun isLoadingMore(): Boolean {
                TODO("Not yet implemented")
            }

            override fun setLoadMode(loadMode: Int) {
                TODO("Not yet implemented")
            }

            override fun setLoadMoreViewFactory(factory: LoadMoreViewFactory?) {
                TODO("Not yet implemented")
            }

            override fun setMinLoadMoreInterval(minLoadMoreInterval: Long) {
                TODO("Not yet implemented")
            }

            override fun stopAutoLoadWhenFailed(stopAutoLoadWhenFailed: Boolean) {
                TODO("Not yet implemented")
            }

            override fun setLoadMoreDirection(direction: Int) {
                TODO("Not yet implemented")
            }

            override fun setLoadingTriggerThreshold(threshold: Int) {
                TODO("Not yet implemented")
            }

            override fun setAutoHiddenWhenNoMore(autoHiddenWhenNoMore: Boolean) {
                TODO("Not yet implemented")
            }

            override fun setVisibilityWhenNoMore(visibility: Int) {
                TODO("Not yet implemented")
            }

        }
    }

}