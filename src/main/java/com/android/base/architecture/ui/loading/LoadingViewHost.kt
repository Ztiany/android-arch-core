package com.android.base.architecture.ui.loading

import android.app.Dialog
import androidx.annotation.StringRes

/**
 * 显示通用的 LoadingDialog 和 Message 展示。
 *
 * @author Ztiany
 */
interface LoadingViewHost {

    fun showLoadingDialog(): Dialog

    /** Display a loading dialog. If the dialog is already showing, then the message will be reset. */
    fun showLoadingDialog(cancelable: Boolean): Dialog

    /** Display a loading dialog. If the dialog is already showing, then the message will be updated. */
    fun showLoadingDialog(message: CharSequence, cancelable: Boolean): Dialog

    /** Display a loading dialog. If the dialog is already showing, then the message will be updated. */
    fun showLoadingDialog(@StringRes messageId: Int, cancelable: Boolean): Dialog

    fun dismissLoadingDialog()

    fun dismissLoadingDialog(minimumMills: Long, onDismiss: (() -> Unit)? = null)

    fun isLoadingDialogShowing(): Boolean

    fun showMessage(message: CharSequence)

    fun showMessage(@StringRes messageId: Int)

}