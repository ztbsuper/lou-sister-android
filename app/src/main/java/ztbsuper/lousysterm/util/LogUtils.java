package ztbsuper.lousysterm.util;

import android.util.Log;


public class LogUtils {

    public static final String TAG = "Lousysterm";

    public static void verbose(String msg) {
        log(TAG, msg, LogLevel.VERBOSE);
    }

    public static void debug(String msg) {
        log(TAG, msg, LogLevel.DEBUG);
    }

    public static void info(String msg) {
        log(TAG, msg, LogLevel.INFO);
    }

    public static void warn(String msg) {
        log(TAG, msg, LogLevel.WARN);
    }

    public static void error(String msg) {
        log(TAG, msg, LogLevel.ERROR);
    }


    private static void log(String tag, String msg, LogLevel level) {
        switch (level) {
            case VERBOSE:
                Log.v(TAG, msg);
                break;
            case DEBUG:
                Log.d(TAG, msg);
                break;
            case INFO:
                Log.i(TAG, msg);
                break;
            case WARN:
                Log.w(TAG, msg);
                break;
            case ERROR:
                Log.e(TAG, msg);
                break;
            case ASSERT:
                break;
        }
    }

    public enum LogLevel {
        VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT
    }

}
