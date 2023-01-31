package com.android.base.architecture.data

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.android.base.foundation.data.Resource


fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setLoading() {
    value = Resource.loading()
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setError(error: Throwable, reason: E? = null) {
    value = Resource.error(error, reason)
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setData(data: D?) {
    value = if (data == null) {
        Resource.noData()
    } else {
        Resource.success(data)
    }
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setSuccess() {
    value = Resource.noData()
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.postLoading() {
    postValue(Resource.loading())
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.postError(error: Throwable, reason: E? = null) {
    postValue(Resource.error(error, reason))
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.postData(data: D?) {
    postValue(
        if (data == null) {
            Resource.noData()
        } else {
            Resource.success(data)
        }
    )
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.postSuccess() {
    postValue(Resource.noData())
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setLoadingSafely() {
    if (isMainThread()) {
        value = Resource.loading()
    } else {
        postValue(Resource.loading())
    }
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setErrorSafely(error: Throwable, reason: E? = null) {
    if (isMainThread()) {
        value = Resource.error(error, reason)
    } else {
        postValue(Resource.error(error, reason))
    }
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setDataSafely(data: D?) {
    val resource: Resource<D, E> = if (data == null) {
        Resource.noData()
    } else {
        Resource.success(data)
    }

    if (isMainThread()) {
        value = resource
    } else {
        postValue(resource)
    }
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setSuccessSafely() {
    val resource: Resource<D, E> = Resource.noData()
    if (isMainThread()) {
        value = resource
    } else {
        postValue(resource)
    }
}

private fun isMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}