package com.android.base.architecture.fragment.epoxy

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.OnModelBoundListener
import com.airbnb.epoxy.OnModelUnboundListener
import com.airbnb.epoxy.TypedEpoxyController
import com.android.base.architecture.ui.list.ListDataOperator
import com.android.base.architecture.ui.list.PagerSize
import com.ztiany.loadmore.adapter.LoadMoreController
import com.ztiany.loadmore.adapter.OnRecyclerViewScrollBottomListener

abstract class ListEpoxyController<T> : TypedEpoxyController<List<T>>(), ListDataOperator<T>, ListEpoxyControllerInterface, PagerSize {

    private var loadMoreViewState = LoadMoreViewState.LOADING

    private var resetWhenUnBind = true

    private val onLoadMoreClickListener = View.OnClickListener {
        loadMoreController?.onClickLoadMoreView()
    }

    private val onBindLoadMoreViewListener = OnModelBoundListener { _: LoadingMoreRowModel_, _: LoadingMoreRow, _: Int ->
        if (resetWhenUnBind) {
            loadMoreController?.tryCallLoadMore(0)
            resetWhenUnBind = false
        }
    }

    private val onUnboundLoadMoreListener = OnModelUnboundListener { _: LoadingMoreRowModel_, _: LoadingMoreRow ->
        resetWhenUnBind = true
    }

    final override fun buildModels(data: List<T>) {
        buildListModels(data)
        if (data.isNotEmpty()) {
            buildLoadMoreModels(data.size)
        }
    }

    private fun buildLoadMoreModels(size: Int) {
        val controller = loadMoreController ?: return
        loadingMoreRow {
            id("load-more-$size")
            state(this@ListEpoxyController.loadMoreViewState)
            autoHideWhenNoMore(controller.isAutoHideWhenNoMore)
            clickListener(this@ListEpoxyController.onLoadMoreClickListener)
            if (this@ListEpoxyController.onRecyclerViewScrollBottomListener == null) {
                onBind(this@ListEpoxyController.onBindLoadMoreViewListener)
                onUnbind(this@ListEpoxyController.onUnboundLoadMoreListener)
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

    override fun getDataSize(): Int {
        return currentData?.size ?: 0
    }

    private fun requestModelBuildInternally() {
        setData(currentData ?: emptyList())
    }

    ///////////////////////////////////////////////////////////////////////////
    // LoadMore
    ///////////////////////////////////////////////////////////////////////////

    private var loadMoreController: LoadMoreControllerImpl? = null

    private var onRecyclerViewScrollBottomListener: OnRecyclerViewScrollBottomListener? = null

    override fun setUpLoadMore(recyclerView: RecyclerView, triggerLoadMoreByScroll: Boolean): LoadMoreController {
        if (loadMoreController != null) {
            throw IllegalStateException("You can only call this method once.")
        }
        if (triggerLoadMoreByScroll) {
            onRecyclerViewScrollBottomListener = object : OnRecyclerViewScrollBottomListener() {
                override fun onBottom(direction: Int) {
                    loadMoreController?.tryCallLoadMore(direction)
                }
            }.apply {
                recyclerView.addOnScrollListener(this)
            }
        }

        return object : LoadMoreControllerImpl(triggerLoadMoreByScroll, onRecyclerViewScrollBottomListener) {
            override fun callShowClickLoad() {
                loadMoreViewState = LoadMoreViewState.CLICK_TO_LOAD
                requestModelBuildInternally()
            }

            override fun callCompleted(hasMore: Boolean) {
                loadMoreViewState = if (hasMore) {
                    LoadMoreViewState.COMPLETED_WITH_MORE
                } else {
                    LoadMoreViewState.COMPLETED_NO_MORE
                }
                requestModelBuildInternally()
            }

            override fun callFail() {
                loadMoreViewState = LoadMoreViewState.FAILED
                requestModelBuildInternally()
            }

            override fun callLoading() {
                loadMoreViewState = LoadMoreViewState.LOADING
                requestModelBuildInternally()
            }
        }.apply {
            loadMoreController = this
        }
    }

}