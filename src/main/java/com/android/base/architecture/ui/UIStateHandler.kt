@file:JvmName("UIKit")

package com.android.base.architecture.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.android.base.AndroidSword
import com.android.base.architecture.ui.loading.LoadingViewHost
import com.android.base.foundation.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import androidx.lifecycle.observe as observeDeprecated

private fun <T> LifecycleOwner.dummyKeep(liveData: LiveData<T>) {
    liveData.observeDeprecated(this) {}
}

fun LoadingViewHost.dismissLoadingDialogDelayed(onDismiss: (() -> Unit)? = null) {
    dismissLoadingDialog(AndroidSword.minimalDialogDisplayTime, onDismiss)
}

/** Configure how to handle UI state [Resource]. */
class ResourceHandlerBuilder<L, D, E> {

    /** [onLoadingState] will be called once state is changed. */
    var onLoadingState: ((isLoading: Boolean, step: L?) -> Unit)? = null

    /** [onError] will be called once [Resource] is [Error]. */
    var onError: ((Throwable, reason: E?) -> Unit)? = null

    /** [onSuccess] will always be called once [Resource] is [Success]. */
    var onSuccess: ((D?) -> Unit)? = null

    /** [onData] will be called only when [Resource] is [Data]. */
    var onData: ((D) -> Unit)? = null

    /** [onNoData] will be called only when [Resource] is [NoData]. */
    var onNoData: (() -> Unit)? = null

    var onUninitialized: (() -> Unit)? = null

    /** when [Resource] is [Loading], what to show on the loading dialog. */
    var loadingMessage: CharSequence = ""

    /** indicate whether the loading dialog should be showing when [Resource] is [Loading]. */
    var showLoading: Boolean = true

    /** indicate whether the loading dialog is cancelable. */
    var forceLoading: Boolean = true

    /** mark the event handled so that it will not be handled again. refer to [ViewModel One-off event antipatterns](https://manuelvivo.dev/viewmodel-events-antipatterns) for more details. */
    var clearAfterHanded: Boolean = true
}

/**
 * 这是一个网络请求状态转换处理的通用逻辑封装，一般情况下，网络请求流程为：
 *
 * 1. 发起网络请求，展示 loading 对话框。
 * 2. 网络请求正常返回，则展示调用结果。
 * 3. 网络请求发送错误，则提示用户请求错误。
 *
 * [Resource] 表示请求状态，每次状态变更，[LiveData] 都应该进行通知，该方法订阅 [LiveData] 并对各种状态进行处理。
 * 展示 loading 和对错误进行提示都是自动进行的，通常情况下，只需要提供 [ResourceHandlerBuilder.onSuccess] 对正常的网络结果进行处理即可。
 * 当然如果希望自己处理错误，则可以提供 [ResourceHandlerBuilder.onError] 回调。如果希望自己处理加载中的逻辑，则可以提供 [ResourceHandlerBuilder.onLoading] 回调。
 *
 * 另外需要注意的是：[ResourceHandlerBuilder.onSuccess] =  [ResourceHandlerBuilder.onData] + [ResourceHandlerBuilder.onNoData]，请根据你的偏好进行选择。
 */
fun <H, L, D, E> H.handleLiveData(
    data: LiveData<Resource<L, D, E>>,
    handlerBuilder: ResourceHandlerBuilder<L, D, E>.() -> Unit
) where H : LoadingViewHost, H : LifecycleOwner {
    val builder = ResourceHandlerBuilder<L, D, E>().apply {
        handlerBuilder()
    }

    data.observe(this) { state ->
        handleResourceInternal(state, builder)
    }
}

/** refers to [handleLiveData] for details. If you are using a [Fragment] with Ui, you probably need to use [handleFlowWithViewLifecycle] instead. */
fun <H, L, D, E> H.handleFlowWithLifecycle(
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    data: Flow<Resource<L, D, E>>,
    handlerBuilder: ResourceHandlerBuilder<L, D, E>.() -> Unit
) where H : LoadingViewHost, H : LifecycleOwner {
    val builder = ResourceHandlerBuilder<L, D, E>().apply {
        handlerBuilder()
    }

    lifecycleScope.launch {
        repeatOnLifecycle(activeState) {
            data.onEach {
                handleResourceInternal(it, builder)
            }.launchIn(this)
        }
    }
}

/** refers to [handleLiveData] for details. Notes：You should call this method in [Fragment.onViewCreated]. */
fun <H, L, D, E> H.handleFlowWithViewLifecycle(
    activeState: Lifecycle.State = Lifecycle.State.STARTED,
    data: Flow<Resource<L, D, E>>,
    handlerBuilder: ResourceHandlerBuilder<L, D, E>.() -> Unit
) where H : LoadingViewHost, H : Fragment {
    val builder = ResourceHandlerBuilder<L, D, E>().apply {
        handlerBuilder()
    }

    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(activeState) {
            data.onEach {
                handleResourceInternal(it, builder)
            }.launchIn(this)
        }
    }
}

/** refers to [handleLiveData] for details. */
fun <H, L, D, E> H.handleResource(
    state: Resource<L, D, E>,
    handlerBuilder: ResourceHandlerBuilder<L, D, E>.() -> Unit
) where H : LoadingViewHost, H : LifecycleOwner {
    val builder = ResourceHandlerBuilder<L, D, E>().apply {
        handlerBuilder()
    }
    handleResourceInternal(state, builder)
}

private fun <H, L, D, E> H.handleResourceInternal(
    state: Resource<L, D, E>,
    handlerBuilder: ResourceHandlerBuilder<L, D, E>
) where H : LoadingViewHost, H : LifecycleOwner {

    when (state) {
        is Uninitialized -> {
            dismissLoadingDialogDelayed {
                handlerBuilder.onUninitialized?.invoke()
                handlerBuilder.onLoadingState?.invoke(false, null)
            }
        }

        //----------------------------------------loading start
        // The loading state should always be handled, so we ignore the clearAfterHanded config here.
        is Loading -> {
            if (handlerBuilder.showLoading) {
                if (handlerBuilder.onLoadingState == null) {
                    showLoadingDialog(handlerBuilder.loadingMessage, !handlerBuilder.forceLoading)
                } else {
                    handlerBuilder.onLoadingState?.invoke(true, state.step)
                }
            }
        }
        //----------------------------------------loading end

        //----------------------------------------error start
        is Error -> {
            if (state.isHandled) {
                handlerBuilder.onLoadingState?.invoke(false, null)
                return
            }
            if (handlerBuilder.clearAfterHanded) {
                state.markAsHandled()
            }

            dismissLoadingDialogDelayed {
                handlerBuilder.onLoadingState?.invoke(false, null)
                val onError = handlerBuilder.onError
                if (onError != null) {
                    onError(state.error, state.reason)
                } else {
                    showMessage(AndroidSword.errorConvert.convert(state.error))
                }
            }
        }
        //----------------------------------------error end

        //----------------------------------------success start
        is Success<D> -> {
            if (state.isHandled) {
                handlerBuilder.onLoadingState?.invoke(false, null)
                return
            }
            if (handlerBuilder.clearAfterHanded) {
                state.markAsHandled()
            }

            dismissLoadingDialogDelayed {
                handlerBuilder.onLoadingState?.invoke(false, null)
                when (state) {
                    is NoData -> {
                        handlerBuilder.onSuccess?.invoke(null)
                        handlerBuilder.onNoData?.invoke()
                    }

                    is Data<D> -> {
                        handlerBuilder.onSuccess?.invoke(state.value)
                        handlerBuilder.onData?.invoke(state.value)
                    }
                }
            }
        }
        //----------------------------------------success end

    }
}