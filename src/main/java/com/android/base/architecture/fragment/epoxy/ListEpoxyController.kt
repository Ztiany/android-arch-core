package com.android.base.architecture.fragment.epoxy

import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.android.base.AndroidSword
import com.android.base.architecture.ui.list.ListDataHost
import com.ztiany.loadmore.adapter.LoadMoreController

abstract class ListEpoxyController<T> : TypedEpoxyController<List<T>>(), ListDataHost<T> {

    private val loadMoreHelper by lazy {
        EpoxyControllerLoadMoreHelper {
            requestModelBuildInternally()
        }
    }

    final override fun buildModels(data: List<T>) {
        buildListModels(data)
        if (data.isNotEmpty()) {
            with(loadMoreHelper) {
                buildLoadMoreModels(data.size)
            }
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

    override fun getListSize(): Int {
        return currentData?.size ?: 0
    }

    private fun requestModelBuildInternally() {
        setData(currentData ?: emptyList())
    }

    fun setUpLoadMore(recyclerView: RecyclerView, triggerLoadMoreByScroll: Boolean = AndroidSword.loadMoreTriggerByScroll): LoadMoreController {
        return loadMoreHelper.setUpLoadMore(recyclerView, triggerLoadMoreByScroll)
    }

}