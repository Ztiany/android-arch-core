package com.android.base.architecture.fragment.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.android.base.architecture.fragment.tools.ReusableView
import com.android.base.architecture.ui.viewbniding.inflateBindingWithParameterizedType

/**
 *@author Ztiany
 * @see BaseUIFragment
 */
abstract class BaseUIDialogFragment<VB : ViewBinding> : BaseDialogFragment() {

    private val reuseView by lazy { ReusableView() }

    private var _vb: VB? = null
    protected val vb: VB
        get() = checkNotNull(_vb) {
            "access this after onCreateView() is called."
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val factory = {
            _vb = provideViewBinding(inflater, container, savedInstanceState) ?: inflateBindingWithParameterizedType(layoutInflater, container, false)
            vb.root
        }
        return reuseView.createView(factory)
    }

    protected open fun provideViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): VB? = null

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (reuseView.isNotTheSameView(view)) {
            internalOnSetUpCreatedView(view, savedInstanceState)
            onSetUpCreatedView(view, savedInstanceState)
        }
        super.onViewCreated(view, savedInstanceState)
        onViewPrepared(view, savedInstanceState)
    }

    internal open fun internalOnSetUpCreatedView(view: View, savedInstanceState: Bundle?) {}

    /**
     * Called when the view is prepared. If [setReuseView] is called and passes true as the parameter, it will be called just once.
     *
     * @param view view of fragment.
     */
    protected open fun onSetUpCreatedView(view: View, savedInstanceState: Bundle?) {}

    /** Called when after [onSetUpCreatedView] is called. */
    protected open fun onViewPrepared(view: View, savedInstanceState: Bundle?) {}

    /**
     * Call it before [onCreateView] is called.
     */
    protected fun setReuseView(reuseTheView: Boolean) {
        reuseView.reuseTheView = reuseTheView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (reuseView.destroyView()) {
            _vb = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _vb = null
    }

}