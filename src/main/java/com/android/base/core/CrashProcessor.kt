package com.android.base.core

interface CrashProcessor {
    fun uncaughtException(thread: Thread, ex: Throwable)
}
