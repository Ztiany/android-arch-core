package com.android.base.core

interface RequestErrorHandler {

    fun handle(throwable: Throwable)

}