package com.android.base.architecture.data

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.android.base.foundation.data.Resource

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setLoading() {
    if (isMainThread()) {
        value = Resource.loading()
    } else {
        postValue(Resource.loading())
    }
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setError(error: Throwable, reason: E? = null) {
    if (isMainThread()) {
        value = Resource.error(error, reason)
    } else {
        postValue(Resource.error(error, reason))
    }
}

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setData(data: D?) {
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

fun <D : Any?, E : Any?> MutableLiveData<Resource<D, E>>.setSuccess() {
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