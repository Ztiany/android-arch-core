package com.android.base.architecture.fragment.epoxy

import androidx.recyclerview.widget.RecyclerView
import com.ztiany.loadmore.adapter.LoadMoreController

interface ListEpoxyControllerInterface {

    fun setUpLoadMore(
        recyclerView: RecyclerView,
        triggerLoadMoreByScroll: Boolean
    ): LoadMoreController

}