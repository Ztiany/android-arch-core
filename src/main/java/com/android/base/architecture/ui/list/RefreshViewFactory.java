package com.android.base.architecture.ui.list;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * @author Ztiany
 */
public class RefreshViewFactory {

    private static Factory sFactory;

    @NonNull
    public static RefreshView createRefreshView(View view) {
        RefreshView refreshView = null;
        if (sFactory != null) {
            refreshView = sFactory.createRefreshView(view);
        }
        if (refreshView == null && view instanceof SwipeRefreshLayout) {
            refreshView = new SwipeRefreshView((SwipeRefreshLayout) view);
        }
        if (refreshView == null) {
            throw new IllegalArgumentException("RefreshViewFactory does not support create RefreshView . the view ：" + view);
        }
        return refreshView;
    }

    public static void registerFactory(Factory factory) {
        sFactory = factory;
    }

    public interface Factory {
        RefreshView createRefreshView(View view);
    }

    public static Factory getFactory() {
        return sFactory;
    }

}