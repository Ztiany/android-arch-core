package com.android.base.architecture.fragment.epoxy

import androidx.annotation.IntDef
import com.ztiany.loadmore.adapter.LoadMoreView

interface EpoxyLoadMoreView : LoadMoreView {
    var autoHideWhenNoMore: Boolean
}

@IntDef(
    LoadMoreViewState.LOADING,
    LoadMoreViewState.COMPLETED_NO_MORE,
    LoadMoreViewState.COMPLETED_WITH_MORE,
    LoadMoreViewState.FAILED,
    LoadMoreViewState.CLICK_TO_LOAD,
)
annotation class LoadMoreViewState {
    companion object {
        const val LOADING = 1
        const val COMPLETED_NO_MORE = 2
        const val COMPLETED_WITH_MORE = 3
        const val FAILED = 4
        const val CLICK_TO_LOAD = 5
    }
}