package com.android.base.architecture.ui

import com.android.base.AndroidSword
import com.android.base.architecture.ui.list.ListLayoutHost
import com.android.base.foundation.data.Data
import com.android.base.foundation.data.Error
import com.android.base.foundation.data.Idle
import com.android.base.foundation.data.Loading
import com.android.base.foundation.data.NoData
import com.android.base.foundation.data.State
import com.android.base.foundation.data.Success

//TODO: refactor this to DSL style.
fun <L, D, E> ListLayoutHost<D>.handleListResource(
    state: State<L, List<D>, E>,
    hasMore: (() -> Boolean)? = null,
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
                is NoData -> handleListResult(null, hasMore, onEmpty)
                is Data<List<D>> -> handleListResult(state.value, hasMore, onEmpty)
            }
        }
    }
}

fun <D> ListLayoutHost<D>.handleListResult(
    list: List<D>?,
    hasMore: (() -> Boolean)? = null,
    onEmpty: (() -> Unit)? = null,
) {

    if (isLoadingMore()) {
        if (!list.isNullOrEmpty()) {
            addData(list)
        }
    } else {
        replaceData(list ?: emptyList())
        if (isRefreshEnable && isRefreshing()) {
            refreshCompleted()
        }
    }

    if (isLoadMoreEnable) {
        if (hasMore == null) {
            loadMoreCompleted(list != null && getPager().hasMore(list.size))
        } else {
            loadMoreCompleted(hasMore())
        }
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

fun ListLayoutHost<*>.handleListError(throwable: Throwable) {
    if (isRefreshEnable && isRefreshing()) {
        refreshCompleted()
    }

    if (isLoadMoreEnable && isLoadingMore()) {
        loadMoreFailed()
    }

    if (isEmpty()) {
        val errorTypeClassifier = AndroidSword.errorClassifier
        if (errorTypeClassifier != null) {
            when {
                errorTypeClassifier.isNetworkError(throwable) -> showNetErrorLayout()
                errorTypeClassifier.isServerError(throwable) -> showServerErrorLayout()
                else -> showErrorLayout()
            }
        } else {
            showErrorLayout()
        }
    } else {
        showContentLayout()
    }
}

fun ListLayoutHost<*>.handleListLoading() {
    if (isEmpty()) {
        if (isRefreshing()) {
            showBlank()
        } else {
            showLoadingLayout()
        }
    }
}
