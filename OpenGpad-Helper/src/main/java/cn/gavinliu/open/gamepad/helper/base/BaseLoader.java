package cn.gavinliu.open.gamepad.helper.base;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Gavin on 14/12/16.
 */
public abstract class BaseLoader<T> extends AsyncTaskLoader<T> {
    private T mData;

    public BaseLoader(Context context) {
        super(context);
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mData);
        }
        registerObserver();
        if (takeContentChanged() || mData == null || isConfigChanged()) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Called when there is new data to deliver to the client. The super class
     * will take care of delivering it; the implementation here just adds a
     * little more logic.
     */
    @Override
    public void deliverResult(T data) {
        if (isReset()) {
            // An async query came in while the loader is stopped. We
            // don't need the result.
            if (mData != null) {
                onReleaseResources(mData);
            }
        }
        T oldApps = mData;
        mData = data;
        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }
        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(T data) {
        super.onCanceled(data);
        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(data);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }
        unregisterObserver();
    }

    protected boolean isConfigChanged() {
        return false;
    }

    protected void registerObserver() {
    }

    protected void unregisterObserver() {
    }

    protected void onReleaseResources(T mData) {
    }
}
