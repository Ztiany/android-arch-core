package com.android.base.architecture.data

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.android.base.foundation.data.Resource


fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.setLoading(step: L? = null) {
    value = Resource.loading(step)
}

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.setError(error: Throwable, reason: E? = null) {
    value = Resource.error(error, reason)
}

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.setData(data: D?) {
    value = if (data == null) {
        Resource.noData()
    } else {
        Resource.success(data)
    }
}

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.setSuccess() {
    value = Resource.noData()
}

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.postLoading(step: L? = null) {
    postValue(Resource.loading(step))
}

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.postError(error: Throwable, reason: E? = null) {
    postValue(Resource.error(error, reason))
}

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.postData(data: D?) {
    postValue(
        if (data == null) {
            Resource.noData()
        } else {
            Resource.success(data)
        }
    )
}

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.postSuccess() {
    postValue(Resource.noData())
}

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.setLoadingSafely(step: L? = null) {
    if (isMainThread()) {
        value = Resource.loading(step)
    } else {
        postValue(Resource.loading(step))
    }
}

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.setErrorSafely(error: Throwable, reason: E? = null) {
    if (isMainThread()) {
        value = Resource.error(error, reason)
    } else {
        postValue(Resource.error(error, reason))
    }
}

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.setDataSafely(data: D?) {
    val resource: Resource<L, D, E> = if (data == null) {
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

fun <L : Any?, D : Any?, E : Any?> MutableLiveData<Resource<L, D, E>>.setSuccessSafely() {
    val resource: Resource<L, D, E> = Resource.noData()
    if (isMainThread()) {
        value = resource
    } else {
        postValue(resource)
    }
}

private fun isMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}