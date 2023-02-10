package com.android.base.architecture.fragment.epoxy

import androidx.annotation.IntDef
import com.ztiany.loadmore.adapter.LoadMoreView

interface EpoxyLoadMoreView : LoadMoreView

@IntDef(
    LoadingMoreState.LOADING,
    LoadingMoreState.NO_MORE,
    LoadingMoreState.FAILED,
    LoadingMoreState.CLICK_TO_LOAD,
)

annotation class LoadingMoreState {
    companion object {
        const val LOADING = 1
        const val NO_MORE = 2
        const val FAILED = 3
        const val CLICK_TO_LOAD = 4
    }
}