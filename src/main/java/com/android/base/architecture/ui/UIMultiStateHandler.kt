package com.android.base.architecture.ui

import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.util.SparseLongArray
import androidx.core.util.isEmpty
import com.android.base.AndroidSword
import com.android.base.architecture.ui.state.StateLayoutHost
import com.android.base.foundation.data.*

private fun <D> newDefaultChecker(): ((D) -> Boolean) {
    return { data ->
        (data is CharSequence && (data.isEmpty() || data.isBlank()))
                || (data is Collection<*> && data.isEmpty())
                || (data is Map<*, *> && data.isEmpty())
                || (data is SparseArray<*> && data.isEmpty())
                || (data is SparseIntArray && data.isEmpty())
                || (data is SparseLongArray && data.isEmpty())
                || (data is SparseBooleanArray && data.isEmpty())
                || (data is Array<*> && data.isEmpty())
                || (data is IntArray && data.isEmpty())
                || (data is LongArray && data.isEmpty())
                || (data is BooleanArray && data.isEmpty())
    }
}

fun <D, E> StateLayoutHost.handleSateResource(
    resource: Resource<D, E>,
    isEmpty: ((D) -> Boolean)? = newDefaultChecker(),
    onError: ((Throwable, E?) -> Unit)? = null,
    onEmpty: (() -> Unit)? = null,
    onResult: ((D) -> Unit)
) {
    when (resource) {
        is Loading -> showLoadingLayout()
        is Error -> {
            if (onError == null) {
                handleStateError(resource.error)
            } else {
                onError(resource.error, resource.reason)
            }
        }
        is Success<D> -> {
            when (resource) {
                is NoData -> handleStateResult(null, isEmpty, onEmpty, onResult)
                is Data<D> -> handleStateResult(resource.value, isEmpty, onEmpty, onResult)
            }
        }
    }
}

fun <D> StateLayoutHost.handleStateResult(
    data: D?,
    isEmpty: ((D) -> Boolean)? = newDefaultChecker(),
    onEmpty: (() -> Unit)? = null,
    onResult: ((D) -> Unit)
) {
    if (isRefreshEnable && isRefreshing()) {
        refreshCompleted()
    }

    if (data == null || isEmpty?.invoke(data) == true) {
        if (onEmpty != null) {
            onEmpty()
        } else {
            showEmptyLayout()
        }
    } else {
        showContentLayout()
        onResult.invoke(data)
    }
}

fun StateLayoutHost.handleStateError(throwable: Throwable) {
    if (isRefreshEnable && isRefreshing()) {
        refreshCompleted()
    }

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
}