package com.android.base.architecture.data

import com.android.base.foundation.data.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

suspend fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<Resource<L, D, E>>.emitLoading(step: L? = null) {
    emit(Resource.loading(step))
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<Resource<L, D, E>>.emitError(error: Throwable) {
    emit(Resource.error(error))
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableSharedFlow<Resource<L, D, E>>.emitData(data: D?) {
    val resource: Resource<L, D, E> = if (data == null) {
        Resource.noData()
    } else {
        Resource.success(data)
    }
    emit(resource)
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<Resource<L, D, E>>.emitLoading(step: L? = null) {
    emit(Resource.loading(step))
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<Resource<L, D, E>>.emitError(error: Throwable) {
    emit(Resource.error(error))
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<Resource<L, D, E>>.emitData(data: D?) {
    val resource: Resource<L, D, E> = if (data == null) {
        Resource.noData()
    } else {
        Resource.success(data)
    }
    emit(resource)
}

suspend fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<Resource<L, D, E>>.emitSuccess() {
    val resource: Resource<L, D, E> = Resource.noData()
    emit(resource)
}

fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<Resource<L, D, E>>.setLoading(step: L? = null) {
    value = Resource.loading(step)
}

fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<Resource<L, D, E>>.setError(error: Throwable) {
    value = Resource.error(error)
}

fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<Resource<L, D, E>>.setData(data: D?) {
    val resource: Resource<L, D, E> = if (data == null) {
        Resource.noData()
    } else {
        Resource.success(data)
    }
    value = resource
}

fun <L : Any?, D : Any?, E : Any?> MutableStateFlow<Resource<L, D, E>>.setSuccess() {
    val resource: Resource<L, D, E> = Resource.noData()
    value = resource
}