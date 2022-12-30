package com.android.base.architecture.ui.list;

import static com.android.base.architecture.ui.list.ListLayoutHostKt.isLoadingFirstPage;

import com.android.base.adapter.DataManager;

/**
 * @author Ztiany
 */
public class AutoPaging extends Paging {

    @SuppressWarnings("rawtypes")
    private final DataManager mDataManager;

    @SuppressWarnings("rawtypes")
    private final ListLayoutHost mRefreshListLayoutHost;

    @SuppressWarnings("rawtypes")
    public AutoPaging(ListLayoutHost refreshListLayoutHost, DataManager dataManager) {
        mRefreshListLayoutHost = refreshListLayoutHost;
        mDataManager = dataManager;
    }

    @Override
    public int getCurrentPage() {
        return calcPageNumber(mDataManager.getDataSize());
    }

    @Override
    public int getLoadingPage() {
        if (isLoadingFirstPage(mRefreshListLayoutHost)) {
            return getPageStart();
        } else {
            return getCurrentPage() + 1;
        }
    }

    @Override
    public int getItemCount() {
        return mDataManager.getDataSize();
    }

}
