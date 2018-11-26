package com.demo_build_react_native_from_source.os;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * https://blog.csdn.net/pengjunlee/article/details/72845449
 */
public final class RxShutdownHook {
    public static final String TAG = "RxShutdownHook";

    private final Flowable mFlowable;

    public RxShutdownHook() {
        super();

        mFlowable = Flowable.create(
                new FlowableOnSubscribe<Thread>() {
                    @Override
                    public void subscribe(final FlowableEmitter<Thread> emitter) throws Exception {
                        final Runtime runtime = Runtime.getRuntime();
                        final Thread hook = new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        emitter.onNext(Thread.currentThread());
                                        emitter.onComplete();
                                    }
                                });
                        hook.setName(TAG + " # " + String.valueOf(hook));

                        emitter.setDisposable(new SafeDisposable() {
                            @Override
                            protected void onDispose() {
                                //runtime.removeShutdownHook(hook); // call this func will be cause crash after System.exit(...);
                            }
                        });

                        try {
                            runtime.addShutdownHook(hook);
                        } catch (Throwable e) {
                            emitter.onError(e);
                        }
                    }
                },
                BackpressureStrategy.MISSING)
                .share();
    }

    public Flowable<Thread> onHook(final Object hookTag) {
        return mFlowable
                .doOnNext(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        AppLogger.i(TAG, "runShutdownHook : " + String.valueOf(hookTag));
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        AppLogger.e(TAG, "runShutdownHook error : " + String.valueOf(hookTag), t);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        AppLogger.i(TAG, "finishShutdownHook : " + String.valueOf(hookTag));
                    }
                });
    }
}
