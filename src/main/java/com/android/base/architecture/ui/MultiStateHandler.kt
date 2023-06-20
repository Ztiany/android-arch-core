package com.android.base.architecture.ui

import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.util.SparseLongArray
import androidx.core.util.isEmpty
import com.android.base.AndroidSword
import com.android.base.architecture.ui.state.StateLayoutHost
import com.android.base.foundation.data.Data
import com.android.base.foundation.data.Error
import com.android.base.foundation.data.Idle
import com.android.base.foundation.data.Loading
import com.android.base.foundation.data.NoData
import com.android.base.foundation.data.State
import com.android.base.foundation.data.Success

//TODO: refactor this to DSL style.

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

fun <L, D, E> StateLayoutHost.handleSateResource(
    state: State<L, D, E>,
    isEmpty: ((D) -> Boolean)? = newDefaultChecker(),
    onError: ((Throwable, E?) -> Unit)? = null,
    onEmpty: (() -> Unit)? = null,
    onResult: ((D) -> Unit),
) {
    when (state) {
        is Idle -> {}

        is Loading -> showLoadingLayout()
        is Error -> {
            if (onError == null) {
                handleStateError(state.error)
            } else {
                onError(state.error, state.reason)
            }
        }

        is Success<D> -> {
            when (state) {
                is NoData -> handleStateResult(null, isEmpty, onEmpty, onResult)
                is Data<D> -> handleStateResult(state.value, isEmpty, onEmpty, onResult)
            }
        }
    }
}

fun <D> StateLayoutHost.handleStateResult(
    data: D?,
    isEmpty: ((D) -> Boolean)? = newDefaultChecker(),
    onEmpty: (() -> Unit)? = null,
    onResult: ((D) -> Unit),
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