package com.android.base.architecture.ui

import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.util.SparseLongArray
import androidx.core.util.isEmpty
import com.android.base.AndroidSword
import com.android.base.architecture.ui.state.StateLayoutHost
import com.android.base.foundation.data.*

private fun <T> newDefaultChecker(): ((T) -> Boolean) {
    return { t ->
        (t is CharSequence && (t.isEmpty() || t.isBlank()))
                || (t is Collection<*> && t.isEmpty())
                || (t is Map<*, *> && t.isEmpty())
                || (t is SparseArray<*> && t.isEmpty())
                || (t is SparseIntArray && t.isEmpty())
                || (t is SparseLongArray && t.isEmpty())
                || (t is SparseBooleanArray && t.isEmpty())
                || (t is Array<*> && t.isEmpty())
                || (t is IntArray && t.isEmpty())
                || (t is LongArray && t.isEmpty())
                || (t is BooleanArray && t.isEmpty())
    }
}

fun <T> StateLayoutHost.handleSateResource(
    resource: Resource<T>,
    ifEmpty: ((T) -> Boolean)? = newDefaultChecker(),
    onEmpty: (() -> Unit)? = null,
    onResult: ((T) -> Unit)
) {
    when (resource) {
        is Loading -> showLoadingLayout()
        is Error -> handleStateError(resource.error)
        is Success<T> -> {
            when (resource) {
                is NoData -> handleStateResult(null, ifEmpty, onEmpty, onResult)
                is Data<T> -> handleStateResult(resource.value, ifEmpty, onEmpty, onResult)
            }
        }
    }
}

fun <T> StateLayoutHost.handleStateResult(
    t: T?,
    isEmpty: ((T) -> Boolean)? = newDefaultChecker(),
    onEmpty: (() -> Unit)? = null,
    onResult: ((T) -> Unit)
) {
    if (isRefreshEnable && isRefreshing()) {
        refreshCompleted()
    }

    if (t == null || isEmpty?.invoke(t) == true) {
        if (onEmpty != null) {
            onEmpty()
        } else {
            showEmptyLayout()
        }
    } else {
        showContentLayout()
        onResult.invoke(t)
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