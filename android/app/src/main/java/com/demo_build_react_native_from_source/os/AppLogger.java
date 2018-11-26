package com.demo_build_react_native_from_source.os;

import android.app.Application;
import android.os.Looper;
import android.os.Process;

import com.facebook.common.logging.FLog;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

public class AppLogger {

    private static boolean sIsOpened = false;

    private static boolean sIsDev = true;

    private AppLogger() {
        super();
    }

    public static void open(Application application, boolean isDev) {
        if (sIsOpened) return;

        System.loadLibrary("stlport_shared");
        System.loadLibrary("marsxlog");

        final String logPath = application.getExternalFilesDir("xlog").toString();
        // this is necessary, or may cash for SIGBUS
        final String cachePath = application.getFilesDir() + "/xlog";

        //init xlog
        int level;
        if (isDev) {
            level = Xlog.LEVEL_DEBUG;
        } else {
            level = Xlog.LEVEL_INFO;
        }

        Xlog.appenderOpen(level, Xlog.AppednerModeAsync, cachePath, logPath, application.getPackageName(), "");
        Xlog.setConsoleLogOpen(false);

        Log.setLogImp(new Xlog());

        FLog.setLoggingDelegate(AppFLogDefaultLoggingDelegate.getInstance());

        sIsDev = isDev;

        sIsOpened = true;
    }

    public static void close() {
        if (!sIsOpened) return;

        Log.appenderFlush(true);
        Log.appenderClose();
    }

    public static void flush(boolean isSync) {
        if (!sIsOpened) return;

        Log.appenderFlush(isSync);
    }


    public static int v(String tag, String msg) {
        Log.v(tag, msg);
        if (sIsDev) {
            return android.util.Log.v(tag, msg);
        } else {
            return -1;
        }
    }

    public static int v(String tag, String msg, Throwable tr) {
        Log.v(tag, msg);
        Log.printErrStackTrace(tag, tr, msg);
        if (sIsDev) {
            return android.util.Log.v(tag, msg, tr);
        } else {
            return -1;
        }
    }

    public static int d(String tag, String msg) {
        Log.d(tag, msg);
        if (sIsDev) {
            return android.util.Log.d(tag, msg);
        } else {
            return -1;
        }
    }

    public static int d(String tag, String msg, Throwable tr) {
        Log.d(tag, msg);
        Log.printErrStackTrace(tag, tr, msg);
        if (sIsDev) {
            return android.util.Log.d(tag, msg, tr);
        } else {
            return -1;
        }
    }

    public static int i(String tag, String msg) {
        Log.i(tag, msg);
        if (sIsDev) {
            return android.util.Log.i(tag, msg);
        } else {
            return -1;
        }
    }

    public static int i(String tag, String msg, Throwable tr) {
        Log.i(tag, msg);
        Log.printErrStackTrace(tag, tr, msg);
        if (sIsDev) {
            return android.util.Log.i(tag, msg, tr);
        } else {
            return -1;
        }
    }

    public static int w(String tag, String msg) {
        Log.w(tag, msg);
        if (sIsDev) {
            return android.util.Log.w(tag, msg);
        } else {
            return -1;
        }
    }

    public static int w(String tag, String msg, Throwable tr) {
        Log.w(tag, msg);
        Log.printErrStackTrace(tag, tr, msg);
        if (sIsDev) {
            return android.util.Log.w(tag, msg, tr);
        } else {
            return -1;
        }
    }

    public static int w(String tag, Throwable tr) {
        Log.printErrStackTrace(tag, tr, "");
        if (sIsDev) {
            return android.util.Log.w(tag, tr);
        } else {
            return -1;
        }
    }

    public static int e(String tag, String msg) {
        Log.e(tag, msg);
        if (sIsDev) {
            return android.util.Log.e(tag, msg);
        } else {
            return -1;
        }
    }

    public static int e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg);
        Log.printErrStackTrace(tag, tr, msg);
        if (sIsDev) {
            return android.util.Log.e(tag, msg, tr);
        } else {
            return -1;
        }
    }

    public static int println(int priority, String tag, String msg) {
        Log.LogImp logImp = Log.getImpl();
        if (android.util.Log.VERBOSE >= priority) {
            logImp.logV(tag,
                    "",
                    "",
                    0,
                    Process.myPid(),
                    Thread.currentThread().getId(),
                    Looper.getMainLooper().getThread().getId(),
                    msg);
        } else if (android.util.Log.DEBUG >= priority) {
            logImp.logD(tag,
                    "",
                    "",
                    0,
                    Process.myPid(),
                    Thread.currentThread().getId(),
                    Looper.getMainLooper().getThread().getId(),
                    msg);
        } else if (android.util.Log.INFO >= priority) {
            logImp.logI(tag,
                    "",
                    "",
                    0,
                    Process.myPid(),
                    Thread.currentThread().getId(),
                    Looper.getMainLooper().getThread().getId(),
                    msg);
        } else if (android.util.Log.WARN >= priority) {
            logImp.logW(tag,
                    "",
                    "",
                    0,
                    Process.myPid(),
                    Thread.currentThread().getId(),
                    Looper.getMainLooper().getThread().getId(),
                    msg);
        } else if (android.util.Log.ERROR >= priority) {
            logImp.logE(tag,
                    "",
                    "",
                    0,
                    Process.myPid(),
                    Thread.currentThread().getId(),
                    Looper.getMainLooper().getThread().getId(),
                    msg);
        } else {
            logImp.logE(tag,
                    "",
                    "",
                    0,
                    Process.myPid(),
                    Thread.currentThread().getId(),
                    Looper.getMainLooper().getThread().getId(),
                    msg);
        }

        if (sIsDev) {
            return android.util.Log.println(priority, tag, msg);
        } else {
            return -1;
        }
    }
}
