package com.studyhelper.utillib;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2018/5/4.
 *
 * @author jogern
 */
public class Logcat {

    /**
     * TAG的占位格式：[T:线程名] [J:类] [M:方法名] [L:行号]
     */
    private static final String FORMAT = "[T:%s J:%s M:%s L:%s]->";
    /**
     * stackTrace 的固定下标
     */
    private static final int FIXED_INDEX = 4;
    /**
     * 每行字数
     */
    private static final int LINE_LEN = 4 * 850;
    /**
     * TAG前缀 以便过滤log
     */
    private static String TAG_PREFIX = "log";

    private Logcat() {
    }

    private static int allow_level = Log.DEBUG;

    public static void initForApp(String tagPrefix, int level) {
        allow_level = level;
        if (!TextUtils.isEmpty(tagPrefix)) {
            TAG_PREFIX = tagPrefix;
        }
    }

    public static boolean allowDebug(){
        return allow_level <= Log.DEBUG;
    }

    public static boolean allowInfo(){
        return allow_level <= Log.INFO;
    }

    public static void v(String msg) {
        if (allow_level <= Log.VERBOSE) {
            String tag = getThrowableTag();
            realPrintf(Log.VERBOSE, tag, splitMsg(msg, tag));
        }
    }

    public static void d(String msg) {
        if (allow_level <= Log.DEBUG) {
            String tag = getThrowableTag();
            realPrintf(Log.DEBUG, tag, splitMsg(msg, tag));
        }
    }

    public static void i(String msg) {
        if (allow_level <= Log.INFO) {
            String tag = getThrowableTag();
            realPrintf(Log.INFO, tag, splitMsg(msg, tag));
        }
    }

    public static void w(String msg) {
        if (allow_level <= Log.WARN) {
            String tag = getThrowableTag();
            realPrintf(Log.WARN, tag, splitMsg(msg, tag));
        }
    }

    public static void e(String msg) {
        if (allow_level <= Log.ERROR) {
            String tag = getThrowableTag();
            realPrintf(Log.ERROR, tag, splitMsg(msg, tag));
        }
    }

    public static void e(Throwable e) {
        List<String> stringList = new ArrayList<>();
        if (e != null) {
            String msg = e.toString();
            stringList.add(msg);

            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                msg = element.toString();
                stringList.add(msg);
            }
            Throwable cause = e.getCause();
            while (cause != null) {
                stringList.add("\t\t");
                stringList.add(cause.toString());
                stackTrace = cause.getStackTrace();
                for (StackTraceElement element : stackTrace) {
                    msg = element.toString();
                    stringList.add(msg);
                }
                cause = cause.getCause();
            }
        }
        realPrintf(Log.ERROR, getThrowableTag(), stringList.toArray(new String[0]));
    }

    private static String getThrowableTag() {
        StackTraceElement caller = null;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > FIXED_INDEX) {
            caller = stackTrace[FIXED_INDEX];
        }
        String clsName = caller == null ? "" : caller.getClassName();
        if (TextUtils.isEmpty(clsName)) {
            clsName = "UNKNOWN";
        }
        String methodName = caller == null ? "" : caller.getMethodName();
        int line = caller == null ? -1 : caller.getLineNumber();
        String tag = Thread.currentThread().getName();
        // String cName = clsName.substring(clsName.lastIndexOf(".") + 1);
        return String.format(FORMAT, tag, clsName, methodName, String.valueOf(line));
    }

    private static String getLineSp(int strlen) {
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < strlen; i++) {
            msg.append("-");
        }
        return msg.toString();
    }


    private static String[] splitMsg(String msg, String tag) {
        if (msg == null || msg.length() <= 0) {
            return new String[]{""};
        }
        int lineLen = LINE_LEN - (tag == null ? 0 : tag.length());
        int length = msg.length();
        if (length <= lineLen) {
            return new String[]{msg};
        }

        List<String> splitMsg = new ArrayList<>();

        int size = length / lineLen;
        for (int i = 0; i < size; i++) {
            splitMsg.add(msg.substring(lineLen * i, lineLen * (i + 1)));
        }
        splitMsg.add(msg.substring(lineLen * size));

//        int max = 0;
//        for (String msgText : splitMsg) {
//            if (msgText.length() > max) {
//                max = msgText.length();
//            }
//        }
//
//        String lineSp = getLineSp(max);
//       // splitMsg.add(0, lineSp);
//        splitMsg.add(lineSp);

        return splitMsg.toArray(new String[0]);
    }

    private static void realPrintf(int logLevel, String tag, String[] msgArray) {
        int len = msgArray == null ? 0 : msgArray.length;
        if (len < 1) {
            realPrintf(logLevel, tag, "");
            return;
        }
        for (String msg : msgArray) {
            realPrintf(logLevel, tag, msg);
        }
    }

    private static void realPrintf(int logLevel, String tag, String msg) {
        switch (logLevel) {
            case Log.VERBOSE:
                Log.v(TAG_PREFIX, String.format(tag + "%s", msg));
                break;
            case Log.INFO:
                Log.i(TAG_PREFIX, String.format(tag + "%s", msg));
                break;
            case Log.WARN:
                Log.w(TAG_PREFIX, String.format(tag + "%s", msg));
                break;
            case Log.ERROR:
                Log.e(TAG_PREFIX, String.format(tag + "%s", msg));
                break;
            default:
                Log.d(TAG_PREFIX, String.format(tag + "%s", msg));
        }
    }


}