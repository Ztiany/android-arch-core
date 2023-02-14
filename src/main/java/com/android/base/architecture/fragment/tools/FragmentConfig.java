package com.android.base.architecture.fragment.tools;

import androidx.annotation.NonNull;

import com.android.base.architecture.fragment.animator.FragmentAnimator;
import com.android.base.architecture.fragment.animator.FragmentScaleAnimator;

/**
 * @author Ztiany
 */
public class FragmentConfig {

    private static final int INVALIDATE_ID = -1;
    private static int sDefaultContainerId = INVALIDATE_ID;
    private static FragmentAnimator sFragmentAnimator = new FragmentScaleAnimator();

    public static void setDefaultContainerId(int defaultContainerId) {
        sDefaultContainerId = defaultContainerId;
    }

    public static int defaultContainerId() {
        if (sDefaultContainerId == INVALIDATE_ID) {
            throw new IllegalStateException("sDefaultContainerId has not set");
        }
        return sDefaultContainerId;
    }

    public static void setDefaultFragmentAnimator(@NonNull FragmentAnimator animator) {
        //noinspection ConstantConditions
        if (animator == null) {
            throw new NullPointerException("animator can not be null.");
        }
        sFragmentAnimator = animator;
    }

    @NonNull
    public static FragmentAnimator defaultFragmentAnimator() {
        return sFragmentAnimator;
    }

}