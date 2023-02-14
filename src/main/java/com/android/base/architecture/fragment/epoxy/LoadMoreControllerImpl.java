package com.android.base.architecture.fragment.epoxy;

import com.ztiany.loadmore.adapter.Direction;
import com.ztiany.loadmore.adapter.LoadMode;
import com.ztiany.loadmore.adapter.LoadMoreConfig;
import com.ztiany.loadmore.adapter.LoadMoreController;
import com.ztiany.loadmore.adapter.LoadMoreViewFactory;
import com.ztiany.loadmore.adapter.OnLoadMoreListener;
import com.ztiany.loadmore.adapter.OnRecyclerViewScrollBottomListener;

abstract class LoadMoreControllerImpl implements LoadMoreController {

    private boolean mHasMore = true;
    private boolean mStopAutoLoadWhenFailed = LoadMoreConfig.isStopAutoLoadWhenFailed();

    private OnLoadMoreListener mOnLoadMoreListener;

    private final static int STATUS_LOADING = 1;
    private final static int STATUS_FAIL = 2;
    private final static int STATUS_COMPLETE = 3;
    private final static int STATUS_PRE = 4;
    private int mCurrentStatus = STATUS_PRE;

    private long mPreviousTimeCallingLoadMore;
    private long mMixLoadMoreInterval = LoadMoreConfig.getMinLoadMoreInterval();

    private final boolean timeLimited;
    private final OnRecyclerViewScrollBottomListener mOnRecyclerViewScrollBottomListener;
    private boolean mAutoHideWhenNoMore = LoadMoreConfig.isStopAutoLoadWhenFailed();

    @LoadMode
    private int mLoadMode = LoadMoreConfig.getLoadMode();

    @Direction
    private int mDirection = Direction.UP;

    public LoadMoreControllerImpl(boolean useScrollListener, OnRecyclerViewScrollBottomListener onRecyclerViewScrollBottomListener) {
        timeLimited = useScrollListener;
        mOnRecyclerViewScrollBottomListener = onRecyclerViewScrollBottomListener;
        init();
    }

    void tryCallLoadMore(int direction) {
        if (mOnLoadMoreListener == null || !mOnLoadMoreListener.canLoadMore()) {
            return;
        }
        if (mCurrentStatus == STATUS_LOADING) {
            return;
        }
        if (isAutoLoad()) {
            if (mStopAutoLoadWhenFailed && mCurrentStatus == STATUS_FAIL) {
                return;
            }
            mCurrentStatus = STATUS_PRE;
            if (checkIfNeedCallLoadMoreWhenAutoMode(direction)) {
                callLoadMore();
            }
        } else {
            if (mCurrentStatus == STATUS_FAIL) {
                return;
            }
            if (mCurrentStatus == STATUS_COMPLETE && !mHasMore) {
                return;
            }
            mCurrentStatus = STATUS_PRE;
            callShowClickLoad();
        }
    }

    private boolean checkIfNeedCallLoadMoreWhenAutoMode(int direction) {
        if (direction != 0 && direction != mDirection) {
            return false;
        }
        if (timeLimited) {
            return System.currentTimeMillis() - mPreviousTimeCallingLoadMore >= mMixLoadMoreInterval;
        } else {
            return true;
        }
    }

    @Override
    public void setMinLoadMoreInterval(long mixLoadMoreInterval) {
        mMixLoadMoreInterval = mixLoadMoreInterval;
    }

    @Override
    public void stopAutoLoadWhenFailed(boolean stopAutoLoadWhenFailed) {
        mStopAutoLoadWhenFailed = stopAutoLoadWhenFailed;
    }

    @Override
    public void setLoadMoreDirection(@Direction int direction) {
        mDirection = direction;
    }

    @Override
    public void setLoadingTriggerThreshold(int loadingTriggerThreshold) {
        if (mOnRecyclerViewScrollBottomListener != null) {
            mOnRecyclerViewScrollBottomListener.setLoadingTriggerThreshold(loadingTriggerThreshold);
        }
    }

    @Override
    public void loadFail() {
        mCurrentStatus = STATUS_FAIL;
        callFail();
    }

    @Override
    public void loadCompleted(final boolean hasMore) {
        mHasMore = hasMore;
        mCurrentStatus = STATUS_COMPLETE;
        callCompleted(mHasMore);
    }

    @Override
    public boolean isLoadingMore() {
        return mCurrentStatus == STATUS_LOADING;
    }

    @Override
    public void setLoadMode(@LoadMode int loadMode) {
        mLoadMode = loadMode;
    }

    @Override
    public void setLoadMoreViewFactory(LoadMoreViewFactory factory) {
        throw new UnsupportedOperationException("");
    }

    private boolean isAutoLoad() {
        return mLoadMode == LoadMode.AUTO_LOAD;
    }

    @Override
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    private void callLoadMore() {
        if (mCurrentStatus != STATUS_LOADING && mOnLoadMoreListener != null && mHasMore) {
            callLoading();
            mCurrentStatus = STATUS_LOADING;
            mOnLoadMoreListener.onLoadMore();
            mPreviousTimeCallingLoadMore = System.currentTimeMillis();
        }
    }

    @Override
    public void setAutoHiddenWhenNoMore(boolean autoHiddenWhenNoMore) {
        mAutoHideWhenNoMore = autoHiddenWhenNoMore;
    }

    private void init() {
        if (mLoadMode == LoadMode.CLICK_LOAD) {
            initClickLoadMoreViewStatus();
        } else {
            initAutoLoadMoreViewStatus();
        }
    }

    private void initAutoLoadMoreViewStatus() {
        switch (mCurrentStatus) {
            case STATUS_PRE:
            case STATUS_LOADING: {
                callLoading();
                break;
            }
            case STATUS_FAIL: {
                callFail();
                break;
            }
            case STATUS_COMPLETE: {
                callCompleted(mHasMore);
                break;
            }
        }
    }

    private void initClickLoadMoreViewStatus() {
        switch (mCurrentStatus) {
            case STATUS_PRE: {
                callShowClickLoad();
                break;
            }
            case STATUS_LOADING: {
                callLoading();
                break;
            }
            case STATUS_FAIL: {
                callFail();
                break;
            }
            case STATUS_COMPLETE: {
                callCompleted(mHasMore);
                break;
            }
        }
    }

    public void onClickLoadMoreView() {
        if (mLoadMode == LoadMode.AUTO_LOAD) {
            //自动加载更多模式，只有错误才能点击
            if ((mCurrentStatus == STATUS_FAIL)) {
                callLoadMore();
            }
        }  /*点击加载更多模式，只有错误和准备状态才能点击*/ else if (mLoadMode == LoadMode.CLICK_LOAD) {
            if (mCurrentStatus == STATUS_PRE || mCurrentStatus == STATUS_FAIL) {
                callLoadMore();
            }
        }
    }

    public boolean isAutoHideWhenNoMore() {
        return mAutoHideWhenNoMore;
    }

    abstract void callShowClickLoad();

    abstract void callCompleted(boolean hasMore);

    abstract void callFail();

    abstract void callLoading();

}
