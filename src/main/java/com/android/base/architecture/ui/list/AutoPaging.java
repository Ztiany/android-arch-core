package com.android.base.architecture.ui.list;

import static com.android.base.architecture.ui.list.ListLayoutHostKt.isLoadingFirstPage;

/**
 * @author Ztiany
 */
public class AutoPaging extends Paging {

    private final PagerSize mPagerSize;

    @SuppressWarnings("rawtypes")
    private final ListLayoutHost mRefreshListLayoutHost;

    @SuppressWarnings("rawtypes")
    public AutoPaging(ListLayoutHost refreshListLayoutHost, PagerSize pagerSize) {
        mRefreshListLayoutHost = refreshListLayoutHost;
        mPagerSize = pagerSize;
    }

    @Override
    public int getCurrentPage() {
        return calcPageNumber(mPagerSize.getSize());
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
        return mPagerSize.getSize();
    }

}