package com.android.base.architecture.data

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.android.base.foundation.data.Resource

fun <T : Any?> MutableLiveData<Resource<T>>.setLoading() {
    if (isMainThread()) {
        value = Resource.loading()
    } else {
        postValue(Resource.loading())
    }
}

fun <T : Any?> MutableLiveData<Resource<T>>.setError(error: Throwable) {
    if (isMainThread()) {
        value = Resource.error(error)
    } else {
        postValue(Resource.error(error))
    }
}

fun <T : Any?> MutableLiveData<Resource<T>>.setData(data: T?) {
    val resource = if (data == null) {
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

fun <T : Any?> MutableLiveData<Resource<T>>.setSuccess() {
    val resource: Resource<T> = Resource.noData()
    if (isMainThread()) {
        value = resource
    } else {
        postValue(resource)
    }
}

private fun isMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}