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

fun <T : Any?> MutableLiveData<Resource<T>>.setData(t: T? = null) {
    val resource = if (t == null) {
        Resource.noData()
    } else {
        Resource.success(t)
    }

    if (isMainThread()) {
        value = resource
    } else {
        postValue(resource)
    }
}

private fun isMainThread(): Boolean {
    return Looper.myLooper() == Looper.getMainLooper()
}