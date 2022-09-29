package com.android.base.architecture.fragment.tools

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.android.base.architecture.ui.loading.LoadingViewHost
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *@author Ztiany
 */
internal fun <T> T.dismissDialog(recentShowingDialogTime: Long, minimumMills: Long, onDismiss: (() -> Unit)?) where T : LoadingViewHost, T : LifecycleOwner {

    if (!isLoadingDialogShowing()) {
        onDismiss?.invoke()
        return
    }

    val dialogShowingTime = System.currentTimeMillis() - recentShowingDialogTime

    if (dialogShowingTime >= minimumMills) {
        dismissLoadingDialog()
        onDismiss?.invoke()

    } else {
        lifecycleScope.launch {
            try {
                delay(minimumMills - dialogShowingTime)
                dismissLoadingDialog()
                onDismiss?.invoke()
            } catch (e: CancellationException) {
                onDismiss?.invoke()
            }
        }
    }

}