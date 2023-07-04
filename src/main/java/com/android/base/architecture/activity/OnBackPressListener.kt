package com.android.base.architecture.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * 需要处理返回事件的 Fragment 需要实现该接口。流程如下：
 *
 *  1. Fragment 需要自己处理 BackPress 事件，如果不处理，就交给子 Fragment 处理。都不处理则由 Activity 处理。
 *  2. BackPress 的传递由低层往深层传递，同一层级的中外层中的 Fragment 优先处理。
 *  3. 在 Fragment 中嵌套使用 Fragment 时，请使用 getSupportChildFragmentManager。
 */
interface OnBackPressListener {

    /**
     * @return true 表示 Fragment 处理 back press，false 表示由 Activity 处理。
     */
    fun onBackPressed(): Boolean

}

/**
 * 将 back 事件分发给 Activity 中的子 Fragment.
 *
 * @return 如果处理了 back 键则返回 true。
 */
fun activityHandleBackPress(fragmentActivity: FragmentActivity): Boolean {
    return handleBackPress(fragmentActivity.supportFragmentManager)
}

/**
 * 将 back 事件分发给 FragmentManager 中管理的子 Fragment，如果该 FragmentManager 中的所有 Fragment 都
 * 没有处理 back 事件，则尝试 FragmentManager.popBackStack()。
 */
private fun handleBackPress(fragmentManager: FragmentManager): Boolean {
    val fragments = fragmentManager.fragments
    for (i in fragments.indices.reversed()) {
        val child = fragments[i]
        if (isFragmentBackHandled(child)) {
            return true
        }
    }
    return false
}

/**
 * 将back事件分发给Fragment中的子Fragment,
 * 该方法调用了 [.handleBackPress]
 *
 * @return 如果处理了back键则返回 **true**
 */
fun fragmentHandleBackPress(fragment: Fragment): Boolean {
    return handleBackPress(fragment.childFragmentManager)
}

/**
 * 判断 Fragment 是否处理了 Back 键。
 */
private fun isFragmentBackHandled(fragment: Fragment?): Boolean {
    return (fragment != null && fragment.isVisible
            && fragment.userVisibleHint //getUserVisibleHint 默认情况下为 true，在 ViewPager 中会被使用到。
            && fragment is OnBackPressListener
            && (fragment as OnBackPressListener).onBackPressed())
}