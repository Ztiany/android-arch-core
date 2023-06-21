package com.android.base.architecture.ui

import com.android.base.architecture.ui.list.ListLayoutHost
import com.android.base.foundation.data.Data
import com.android.base.foundation.data.Error
import com.android.base.foundation.data.Idle
import com.android.base.foundation.data.Loading
import com.android.base.foundation.data.NoData
import com.android.base.foundation.data.State
import com.android.base.foundation.data.Success

/*
TODO: 1 StateLayoutHost 添加一个方法，setRefreshing(boolean)。
TODO: 2 ListLayoutHost 添加一个方法，setLoadingMore(boolean)。
TODO: 3 为列表加载建模，实现 ListState<T>。
TODO: 4 重构 FullListHandler.kt，使用 ListState<T>。
 */
class ListState<T> {

}

//TODO: refactor this to DSL style.
fun <L, D, E> ListLayoutHost<D>.submitListResource(
    state: State<L, List<D>, E>,
    hasMore: Boolean,
    onEmpty: (() -> Unit)? = null,
    onError: ((Throwable, E?) -> Unit)? = null,
) {
    when (state) {
        is Idle -> {}

        is Loading -> handleListLoading()
        is Error -> {
            if (onError == null) {
                handleListError(state.error)
            } else {
                onError(state.error, state.reason)
            }
        }

        is Success<List<D>> -> {
            when (state) {
                is NoData -> submitListResult(null, hasMore, onEmpty)
                is Data<List<D>> -> submitListResult(state.value, hasMore, onEmpty)
            }
        }
    }
}

fun <D> ListLayoutHost<D>.submitListResult(list: List<D>?, hasMore: Boolean, onEmpty: (() -> Unit)? = null) {
    if (isRefreshEnable && isRefreshing()) {
        refreshCompleted()
    }

    replaceData(list ?: emptyList())

    if (isLoadMoreEnable) {
        loadMoreCompleted(hasMore)
    }

    if (isEmpty()) {
        if (onEmpty == null) {
            showEmptyLayout()
        } else {
            onEmpty()
        }
    } else {
        showContentLayout()
    }
}