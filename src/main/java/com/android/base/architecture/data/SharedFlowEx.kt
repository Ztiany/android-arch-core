package com.android.base.architecture.data

import com.android.base.foundation.data.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal fun <T> MutableSharedFlow<T>.asImmutable(): Flow<T> = this

suspend fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<State<L, D, E>>.emitLoading(step: L? = null) {
    emit(State.loading(step))
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<State<L, D, E>>.emitError(error: Throwable) {
    emit(State.error(error))
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<State<L, D, E>>.emitData(data: D?) {
    val state: State<L, D, E> = if (data == null) {
        State.noData()
    } else {
        State.success(data)
    }
    emit(state)
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<State<L, D, E>>.emitSuccess() {
    val state: State<L, D, E> = State.noData()
    emit(state)
}

fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<State<L, D, E>>.tryEmitLoading(step: L? = null) {
    tryEmit(State.loading(step))
}

fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<State<L, D, E>>.tryEmitError(error: Throwable) {
    tryEmit(State.error(error))
}

fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<State<L, D, E>>.tryEmitData(data: D?) {
    val state: State<L, D, E> = if (data == null) {
        State.noData()
    } else {
        State.success(data)
    }
    tryEmit(state)
}

fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<State<L, D, E>>.tryEmitSuccess() {
    val state: State<L, D, E> = State.noData()
    tryEmit(state)
}
