package com.android.base.architecture.fragment.epoxy

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.OnModelBoundListener
import com.airbnb.epoxy.OnModelUnboundListener
import com.ztiany.loadmore.adapter.LoadMoreController
import com.ztiany.loadmore.adapter.OnRecyclerViewScrollBottomListener

class EpoxyControllerLoadMoreHelper(
    val onStateChanged: () -> Unit
) {

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

    fun EpoxyController.buildLoadMoreModels(size: Int) {
        val controller = loadMoreController ?: return
        loadingMoreRow {
            id("load-more-$size")
            state(this@EpoxyControllerLoadMoreHelper.loadMoreViewState)
            autoHideWhenNoMore(controller.isAutoHideWhenNoMore)
            clickListener(this@EpoxyControllerLoadMoreHelper.onLoadMoreClickListener)
            if (this@EpoxyControllerLoadMoreHelper.onRecyclerViewScrollBottomListener == null) {
                onBind(this@EpoxyControllerLoadMoreHelper.onBindLoadMoreViewListener)
                onUnbind(this@EpoxyControllerLoadMoreHelper.onUnboundLoadMoreListener)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // LoadMore
    ///////////////////////////////////////////////////////////////////////////

    private var loadMoreController: LoadMoreControllerImpl? = null

    private var onRecyclerViewScrollBottomListener: OnRecyclerViewScrollBottomListener? = null

    fun setUpLoadMore(recyclerView: RecyclerView, triggerLoadMoreByScroll: Boolean): LoadMoreController {
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
                onStateChanged()
            }

            override fun callCompleted(hasMore: Boolean) {
                loadMoreViewState = if (hasMore) {
                    LoadMoreViewState.COMPLETED_WITH_MORE
                } else {
                    LoadMoreViewState.COMPLETED_NO_MORE
                }
                onStateChanged()
            }

            override fun callFail() {
                loadMoreViewState = LoadMoreViewState.FAILED
                onStateChanged()
            }

            override fun callLoading() {
                loadMoreViewState = LoadMoreViewState.LOADING
                onStateChanged()
            }
        }.apply {
            loadMoreController = this
        }
    }

}