package com.android.base.architecture.ui

import android.app.Activity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
 * @see [collectFlowRepeatedlyOnViewLifecycle]
 */
fun <T> LifecycleOwner.collectFlowRepeatedlyOnLifecycle(
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    data: Flow<T>,
    onResult: suspend (result: T) -> Unit,
) {
    runRepeatedlyOnLifecycle(activeState) {
        data.onEach {
            onResult(it)
        }.launchIn(this)
    }
}

/**
 *  A quote from [A safer way to collect flows from Android UIs](https://medium.com/androiddevelopers/a-safer-way-to-collect-flows-from-android-uis-23080b1f8bda):
 *
 *  > For avoiding wasting resources, [Fragment]s should **always** use the [Fragment.getViewLifecycleOwner] to trigger UI updates. However, that’s not the case for [DialogFragment]s which might not have a `View` sometimes. For [DialogFragment]s, you can use the [LifecycleOwner].
 *
 *  Notes: [Fragment.getViewLifecycleOwner]  is available in the calling of [Fragment.onViewCreated].
 */
fun <T> Fragment.collectFlowRepeatedlyOnViewLifecycle(
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    data: Flow<T>,
    onResult: suspend (result: T) -> Unit,
) {
    runRepeatedlyOnViewLifecycle(activeState) {
        data.onEach {
            onResult(it)
        }.launchIn(this)
    }
}

/** A extension method to call repeatOnLifecycle on lifecycleScope. */
fun LifecycleOwner.runRepeatedlyOnLifecycle(
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit,
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
fun Fragment.runRepeatedlyOnViewLifecycle(
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit,
) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(activeState) {
            block(this)
        }
    }
}