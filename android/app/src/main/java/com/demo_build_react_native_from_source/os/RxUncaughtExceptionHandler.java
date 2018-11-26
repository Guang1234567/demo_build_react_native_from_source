package com.demo_build_react_native_from_source.os;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public final class RxUncaughtExceptionHandler {

    public static final String TAG = "RxUncaughtExceptionHandler";

    private final Flowable mFlowable;

    public RxUncaughtExceptionHandler() {
        super();

        mFlowable = Flowable.create(
                new FlowableOnSubscribe<UncaughtExceptionInfo>() {
                    @Override
                    public void subscribe(final FlowableEmitter<UncaughtExceptionInfo> emitter) throws Exception {
                        final Thread.UncaughtExceptionHandler oldH = Thread.getDefaultUncaughtExceptionHandler();

                        emitter.setDisposable(new SafeDisposable() {
                            @Override
                            protected void onDispose() {
                                Thread.setDefaultUncaughtExceptionHandler(oldH);
                            }
                        });

                        try {
                            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                                @Override
                                public void uncaughtException(final Thread t, final Throwable e) {
                                    emitter.onNext(new UncaughtExceptionInfo(t, e));
                                    emitter.onComplete();
                                }
                            });
                        } catch (Throwable e) {
                            emitter.onError(e);
                        }
                    }
                },
                BackpressureStrategy.MISSING)
                .share();
    }

    public Flowable<UncaughtExceptionInfo> onHandle(final Object handleTag) {
        return mFlowable
                .doOnNext(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        AppLogger.i(TAG, "runUncaughtExceptionHandler : " + String.valueOf(handleTag));
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        AppLogger.e(TAG, "runUncaughtExceptionHandler error : " + String.valueOf(handleTag), t);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        AppLogger.i(TAG, "finishUncaughtExceptionHandler : " + String.valueOf(handleTag));
                    }
                });
    }

    public static final class UncaughtExceptionInfo {
        public final Thread t;
        public final Throwable e;

        public UncaughtExceptionInfo(Thread t, Throwable e) {
            this.t = t;
            this.e = e;
        }
    }
}
