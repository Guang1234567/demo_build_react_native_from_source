package com.demo_build_react_native_from_source.os;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.disposables.Disposable;

public abstract class SafeDisposable implements Disposable {

    private AtomicBoolean mUnsubscribed;

    public SafeDisposable() {
        mUnsubscribed = new AtomicBoolean(false);
    }

    @Override
    public final void dispose() {
        if (mUnsubscribed.compareAndSet(false, true)) {
            onDispose();
        }
    }

    @Override
    public final boolean isDisposed() {
        return mUnsubscribed.get();
    }

    protected abstract void onDispose();
}
