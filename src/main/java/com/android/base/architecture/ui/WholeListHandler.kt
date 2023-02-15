package com.android.base.architecture.ui

import com.android.base.architecture.ui.list.ListLayoutHost
import com.android.base.foundation.data.*


//----------------------------------------------Fully Submit List And With State----------------------------------------------

fun <L, D, E> ListLayoutHost<D>.submitListResource(
    resource: Resource<L, List<D>, E>,
    hasMore: Boolean,
    onEmpty: (() -> Unit)? = null,
    onError: ((Throwable, E?) -> Unit)? = null
) {
    when (resource) {
        is Uninitialized -> {
            /*no op*/
        }
        is Loading -> handleListLoading()
        is Error -> {
            if (onError == null) {
                handleListError(resource.error)
            } else {
                onError(resource.error, resource.reason)
            }
        }
        is Success<List<D>> -> {
            when (resource) {
                is NoData -> submitListResult(null, hasMore, onEmpty)
                is Data<List<D>> -> submitListResult(resource.value, hasMore, onEmpty)
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