package com.demo_build_react_native_from_source;

import android.app.Application;
import android.os.Process;

import com.demo_build_react_native_from_source.os.AppLogger;
import com.demo_build_react_native_from_source.os.ProcessUtils;
import com.demo_build_react_native_from_source.os.RxShutdownHook;
import com.demo_build_react_native_from_source.os.RxUncaughtExceptionHandler;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static com.demo_build_react_native_from_source.MainApplication.TAG;

public class MainApplication extends Application implements ReactApplication {

    public static final String TAG = "MainApplication";

    private AppController mAppController;

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage()
            );
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAppController = new AppController(this);
    }

    /**
     * 如在退出登录时调用.
     */
    public void exitApp() {
        mAppController.destroyApp();
        System.exit(0);
    }
}





class AppController {

    public final static int APP_EXIT_STATUS_CODE_NORMAL = 0;
    public final static int APP_EXIT_STATUS_CODE_UNCAUGHTEXCEPTION = 7788;

    private int mAppExitStatusCode = APP_EXIT_STATUS_CODE_NORMAL;

    private final Application mApplication;

    private RxShutdownHook mShutdownHook;

    private RxUncaughtExceptionHandler mUncaughtExceptionHandler;

    RxShutdownHook getShutdownHook() {
        return mShutdownHook;
    }

    RxUncaughtExceptionHandler getUncaughtExceptionHandler() {
        return mUncaughtExceptionHandler;
    }

    AppController(Application application) {
        mApplication = application;

        AppLogger.open(application, BuildConfig.DEBUG);

        AppLogger.i(TAG, "--------------------------------------------------------------------------------------------");
        AppLogger.i(TAG, new StringBuilder("进程启动 : ").append(ProcessUtils.getProcessName(android.os.Process.myPid())).append('(').append(android.os.Process.myPid()).append(')').toString());
        AppLogger.i(TAG, "是否主进程 : " + ProcessUtils.isMainProcess(application));
        AppLogger.i(TAG, "--------------------------------------------------------------------------------------------");

        AppLogger.flush(true);

        mShutdownHook = new RxShutdownHook();
        mShutdownHook.onHook("AppShutdownHook").subscribe(
                new Consumer<Thread>() {
                    @Override
                    public void accept(Thread hookThread) throws Exception {
                        destroyApp();
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                    }
                });

        mUncaughtExceptionHandler = new RxUncaughtExceptionHandler();
        mUncaughtExceptionHandler.onHandle("AppUncaughtExceptionHandler").subscribe(
                new Consumer<RxUncaughtExceptionHandler.UncaughtExceptionInfo>() {
                    @Override
                    public void accept(RxUncaughtExceptionHandler.UncaughtExceptionInfo info) throws Exception {
                        AppLogger.e(RxUncaughtExceptionHandler.TAG, "--------------------------------------------------------------------------------------------");
                        AppLogger.e(RxUncaughtExceptionHandler.TAG, new StringBuilder("进程意外关闭 : ").append(ProcessUtils.getCurrentProcessName()).append('(').append(ProcessUtils.getCurrentProcessId()).append(')').toString());
                        AppLogger.e(RxUncaughtExceptionHandler.TAG, new StringBuilder("发生崩溃异常的线程 : ").append(info.t).toString());
                        AppLogger.e(RxUncaughtExceptionHandler.TAG, new StringBuilder("崩溃异常的堆栈信息 : ").toString(), info.e);
                        AppLogger.e(RxUncaughtExceptionHandler.TAG, "--------------------------------------------------------------------------------------------");

                        AppLogger.flush(true);

                        mAppExitStatusCode = APP_EXIT_STATUS_CODE_UNCAUGHTEXCEPTION;
                    }
                }
        );

        SoLoader.init(application, /* native exopackage */ false);

    }

    void destroyApp() {
        String msg0 = new StringBuilder("进程销毁 : ")
                .append(ProcessUtils.getProcessName(Process.myPid()))
                .append('(')
                .append(Process.myPid())
                .append(')')
                .toString();
        String msg1 = "是否主进程 : " + ProcessUtils.isMainProcess(mApplication);
        String msg2 = "AppExitStatusCode : " + mAppExitStatusCode;

        if (mAppExitStatusCode != 0) {
            AppLogger.e(TAG, "--------------------------------------------------------------------------------------------");
            AppLogger.e(TAG, msg0);
            AppLogger.e(TAG, msg1);
            AppLogger.e(TAG, msg2);
            AppLogger.e(TAG, "--------------------------------------------------------------------------------------------");
        } else {
            AppLogger.i(TAG, "--------------------------------------------------------------------------------------------");
            AppLogger.i(TAG, msg0);
            AppLogger.i(TAG, msg1);
            AppLogger.i(TAG, msg2);
            AppLogger.i(TAG, "--------------------------------------------------------------------------------------------");
        }

        AppLogger.close();
    }
}