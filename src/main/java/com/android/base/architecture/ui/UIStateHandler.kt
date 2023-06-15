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

/** Configure how to handle UI state [State]. */
class ResourceHandlerBuilder<L, D, E> {

    internal var onLoading: ((step: L?) -> Unit)? = null
    internal var onLoadingEnd: (() -> Unit)? = null
    internal var onError: ((error: Throwable, reason: E?) -> Unit)? = null
    internal var onSuccess: ((D?) -> Unit)? = null
    internal var onData: ((D) -> Unit)? = null
    internal var onNoData: (() -> Unit)? = null
    internal var loadingMessage: CharSequence = ""
    internal var showLoading: Boolean = true
    internal var forceLoading: Boolean = true
    internal var clearAfterHanded: Boolean = true

    /** [onLoadingWithStep] will be called once state is [Loading]. */
    fun onLoadingWithStep(onLoading: ((step: L?) -> Unit)? = null) {
        this.onLoading = onLoading
    }

    /** [onLoading] will be called once state is [Loading]. */
    fun onLoading(onLoading: (() -> Unit)? = null) {
        onLoadingWithStep {
            onLoading?.invoke()
        }
    }

    /** [onLoadingEnd] will be called once state is not [Loading].. */
    fun onLoadingEnd(onLoadingEnd: (() -> Unit)? = null) {
        this.onLoadingEnd = onLoadingEnd
    }

    /** [onError] will be called once [State] is [Error]. */
    fun onError(onError: ((error: Throwable) -> Unit)? = null) {
        onErrorWithReason { error, _ -> onError?.invoke(error) }
    }

    /** [onErrorWithReason] will be called once [State] is [Error]. */
    fun onErrorWithReason(onError: ((error: Throwable, reason: E?) -> Unit)? = null) {
        this.onError = onError
    }

    /** [onSuccess] will always be called once [State] is [Success]. */
    fun onSuccess(onSuccess: ((D?) -> Unit)? = null) {
        this.onSuccess = onSuccess
    }

    /** [onData] will be called only when [State] is [Data]. */
    fun onData(onData: ((D) -> Unit)? = null) {
        this.onData = onData
    }

    /** [onNoData] will be called only when [State] is [NoData]. */
    fun onNoData(onNoData: (() -> Unit)? = null) {
        this.onNoData = onNoData
    }

    /** when [State] is [Loading], what to show on the loading dialog. */
    fun loadingMessage(loadingMessage: CharSequence) {
        this.loadingMessage = loadingMessage
    }

    /** indicate whether the loading dialog should be showing when [State] is [Loading]. */
    fun disableLoading() {
        showLoading = false
    }

    /** indicate whether the loading dialog is cancelable. */
    fun forceLoading() {
        this.forceLoading = true
    }

    /** mark the event handled so that it will not be handled again. refer to [ViewModel One-off event antipatterns](https://manuelvivo.dev/viewmodel-events-antipatterns) for more details. */
    fun clearAfterHanded() {
        this.clearAfterHanded = true
    }

}

/**
 * 这是一个网络请求状态转换处理的通用逻辑封装，一般情况下，网络请求流程为：
 *
 * 1. 发起网络请求，展示 loading 对话框。
 * 2. 网络请求正常返回，则展示调用结果。
 * 3. 网络请求发送错误，则提示用户请求错误。
 *
 * [State] 表示请求状态，每次状态变更，[LiveData] 都应该进行通知，该方法订阅 [LiveData] 并对各种状态进行处理。
 * 展示 loading 和对错误进行提示都是自动进行的，通常情况下，只需要提供 [ResourceHandlerBuilder.onSuccess] 对正常的网络结果进行处理即可。
 * 当然如果希望自己处理错误，则可以提供 [ResourceHandlerBuilder.onError] 回调。如果希望自己处理加载中的逻辑，则可以提供 [ResourceHandlerBuilder.onLoading] 回调。
 *
 * 另外需要注意的是：[ResourceHandlerBuilder.onSuccess] =  [ResourceHandlerBuilder.onData] + [ResourceHandlerBuilder.onNoData]，请根据你的偏好进行选择。
 */
fun <H, L, D, E> H.handleLiveData(
    data: LiveData<State<L, D, E>>,
    handlerBuilder: ResourceHandlerBuilder<L, D, E>.() -> Unit,
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
    data: Flow<State<L, D, E>>,
    handlerBuilder: ResourceHandlerBuilder<L, D, E>.() -> Unit,
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
    data: Flow<State<L, D, E>>,
    handlerBuilder: ResourceHandlerBuilder<L, D, E>.() -> Unit,
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
    state: State<L, D, E>,
    handlerBuilder: ResourceHandlerBuilder<L, D, E>.() -> Unit,
) where H : LoadingViewHost, H : LifecycleOwner {
    val builder = ResourceHandlerBuilder<L, D, E>().apply {
        handlerBuilder()
    }
    handleResourceInternal(state, builder)
}

private fun <H, L, D, E> H.handleResourceInternal(
    state: State<L, D, E>,
    handlerBuilder: ResourceHandlerBuilder<L, D, E>,
) where H : LoadingViewHost, H : LifecycleOwner {

    when (state) {
        //----------------------------------------loading start
        // The loading state should always be handled, so we ignore the clearAfterHanded config here.
        is Loading -> {
            if (handlerBuilder.showLoading) {
                if (handlerBuilder.onLoading == null) {
                    showLoadingDialog(handlerBuilder.loadingMessage, !handlerBuilder.forceLoading)
                } else {
                    handlerBuilder.onLoading?.invoke(state.step)
                }
            }
        }
        //----------------------------------------loading end

        //----------------------------------------error start
        is Error -> {
            if (state.isHandled) {
                handlerBuilder.onLoadingEnd?.invoke()
                return
            }
            if (handlerBuilder.clearAfterHanded) {
                state.markAsHandled()
            }

            dismissLoadingDialogDelayed {
                handlerBuilder.onLoadingEnd?.invoke()
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
                handlerBuilder.onLoadingEnd?.invoke()
                return
            }
            if (handlerBuilder.clearAfterHanded) {
                state.markAsHandled()
            }

            dismissLoadingDialogDelayed {
                handlerBuilder.onLoadingEnd?.invoke()
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