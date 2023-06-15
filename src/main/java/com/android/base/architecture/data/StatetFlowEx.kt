package com.android.base.architecture.data

import com.android.base.foundation.data.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal fun <T> MutableStateFlow<T>.asImmutable(): Flow<T> = this

suspend fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<State<L, D, E>>.emitLoading(step: L? = null) {
    emit(State.loading(step))
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<State<L, D, E>>.emitError(error: Throwable) {
    emit(State.error(error))
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<State<L, D, E>>.emitData(data: D?) {
    val state: State<L, D, E> = if (data == null) {
        State.noData()
    } else {
        State.success(data)
    }
    emit(state)
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<State<L, D, E>>.emitSuccess() {
    val state: State<L, D, E> = State.noData()
    emit(state)
}

fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<State<L, D, E>>.setLoading(step: L? = null) {
    value = State.loading(step)
}

fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<State<L, D, E>>.setError(error: Throwable) {
    value = State.error(error)
}

fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<State<L, D, E>>.setData(data: D?) {
    val state: State<L, D, E> = if (data == null) {
        State.noData()
    } else {
        State.success(data)
    }
    value = state
}

fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<State<L, D, E>>.setSuccess() {
    val state: State<L, D, E> = State.noData()
    value = state
}