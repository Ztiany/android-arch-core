package com.android.base.architecture.data

import com.android.base.foundation.data.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

suspend fun <D : Any?, E : Any?> MutableSharedFlow<Resource<D, E>>.emitLoading() {
    emit(Resource.loading())
}

suspend fun <D : Any?, E : Any?> MutableSharedFlow<Resource<D, E>>.emitError(error: Throwable) {
    emit(Resource.error(error))
}

suspend fun <D : Any?, E : Any?> MutableSharedFlow<Resource<D, E>>.emitData(data: D?) {
    val resource: Resource<D, E> = if (data == null) {
        Resource.noData()
    } else {
        Resource.success(data)
    }
    emit(resource)
}

suspend fun <D : Any?, E : Any?> MutableStateFlow<Resource<D, E>>.emitLoading() {
    emit(Resource.loading())
}

suspend fun <D : Any?, E : Any?> MutableStateFlow<Resource<D, E>>.emitError(error: Throwable) {
    emit(Resource.error(error))
}

suspend fun <D : Any?, E : Any?> MutableStateFlow<Resource<D, E>>.emitData(data: D?) {
    val resource: Resource<D, E> = if (data == null) {
        Resource.noData()
    } else {
        Resource.success(data)
    }
    emit(resource)
}

fun <D : Any?, E : Any?> MutableStateFlow<Resource<D, E>>.setLoading() {
    value = Resource.loading()
}

fun <D : Any?, E : Any?> MutableStateFlow<Resource<D, E>>.setError(error: Throwable) {
    value = Resource.error(error)
}

fun <D : Any?, E : Any?> MutableStateFlow<Resource<D, E>>.setData(data: D? = null) {
    val resource: Resource<D, E> = if (data == null) {
        Resource.noData()
    } else {
        Resource.success(data)
    }
    value = resource
}