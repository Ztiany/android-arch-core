package com.android.base.architecture.ui

import android.app.Activity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Collect flow in an [Activity] or sometimes in a [DialogFragment].
 *
 * Notes: call the method in [Activity.onCreate].
 *
 * @see [collectFlowRepeatOnViewLifecycle]
 */
fun <T> LifecycleOwner.collectFlowRepeatOnLifecycle(
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    data: Flow<T>,
    onResult: suspend (result: T) -> Unit
) {
    runRepeatOnLifecycle(activeState) {
        data.onEach {
            onResult(it)
        }.launchIn(this)
    }
}

/**
 *  Fragments should **always** use the [Fragment.getViewLifecycleOwner] to trigger UI updates. However, that’s not the case for [DialogFragment]s which might not have a `View` sometimes. For [DialogFragment]s, you can use the [LifecycleOwner].
 *
 *  Notes: call the method in [Fragment.onViewCreated].
 *
 *  Refer to [A safer way to collect flows from Android UIs](https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda) for details.
 */
fun <T> Fragment.collectFlowRepeatOnViewLifecycle(
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    data: Flow<T>,
    onResult: suspend (result: T) -> Unit
) {
    runRepeatOnViewLifecycle(activeState) {
        data.onEach {
            onResult(it)
        }.launchIn(this)
    }
}

/** A extension method to call repeatOnLifecycle on lifecycleScope. */
fun LifecycleOwner.runRepeatOnLifecycle(
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(activeState) {
            block(this)
        }
    }
}

/**
 * A extension method to call repeatOnLifecycle on viewLifecycleScope.
 *
 *  Notes: call the method in [Fragment.onViewCreated].
 */
fun Fragment.runRepeatOnViewLifecycle(
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(activeState) {
            block(this)
        }
    }
}