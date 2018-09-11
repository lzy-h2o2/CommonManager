package com.zndroid.common.log;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.zndroid.common.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * @author lazy
 * @create 2018/8/1 23:02
 * @desc
 * @since
 *
 * <p>和assets文件夹中的log.properties文件配合控制log的打印输出，不配置则按照默认显示
 * 在文件中配置类的全名和log打印模式(true表为debug模式,打印log;false表示非debug模式,即不打印log)
 * 在文件中配置类的log打印模式是永久生效的,也可直接调用debugAll()或者debug()方法来动态改变文件的配置
 * 不过调用这两个方法改变的打印模式只在内存中生效,不会写入配置文件中{@link checkpermission}(记得要检查文件的读写权限 API>=23)</br>
 * addDebug()是动态添加新的类的打印模式,只在内存中生效,不写入配置文件</p>
 *
 * <p>log.properties文件的内容可以仿照如下配置
 * #######################################</p>
 * <code>
 *     <li>saveFile=true</li>
 *     <li>i=true</li>
 *     <li>d=true</li>
 *     <li>w=true</li>
 *     <li>v=true</li>
 *     <li>e=true</li>
 * </code>
 * <p>#######################################</p>
 */
public class ZLogger {

    private static String pckName;

    private static String className;

    private static String simpleClassName;

    private static String methodName;

    private static int lineNumber;

    private static boolean DEBUG = BuildConfig.DEBUG;

    /**
     * 日志保存模式,false不保存文件,true则自动保存文件
     */
    private static boolean saveFile = false;

    private static boolean i = true;

    private static boolean d = true;

    private static boolean w = true;

    private static boolean v = true;

    private static boolean e = true;

    private static String saveUrl = Environment.getExternalStorageDirectory() + "/zlogger/logs/sync/";

    private static Properties properties = new Properties();

    private static String propertiesName = "log.properties";

    public static void init(Context context) {
        initProperties(context);
        initVar();
        pckName = context.getPackageName();
    }

    private static void initProperties(Context context) {
        try {
            InputStream is = context.getAssets().open(propertiesName);
            properties.load(is);
            is.close();
        } catch (IOException e) {
            print(LEVEL.error, "'log.properties' not found and use default");
        }
    }

    private static void initVar() {
        if (properties != null) {
            String saveMode = properties.getProperty("saveFile");
            if (saveMode != null) {
                saveFile = Boolean.parseBoolean(saveMode);
            }
            String iMode = properties.getProperty("i");
            if (iMode != null) {
                i = Boolean.parseBoolean(iMode);
            }
            String dMode = properties.getProperty("d");
            if (dMode != null) {
                d = Boolean.parseBoolean(dMode);
            }
            String wMode = properties.getProperty("w");
            if (wMode != null) {
                w = Boolean.parseBoolean(wMode);
            }
            String vMode = properties.getProperty("v");
            if (vMode != null) {
                v = Boolean.parseBoolean(vMode);
            }
            String eMode = properties.getProperty("e");
            if (eMode != null) {
                e = Boolean.parseBoolean(eMode);
            }
        }
    }

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    /**
     * 日志保存模式,false不保存文件,true则自动保存文件
     */
    public static void setSaveMode(boolean saveFile) {
        ZLogger.saveFile = saveFile;
    }

    public static void debugAll(boolean debug) {
        if (properties == null) {
            return;
        }
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            entry.setValue(String.valueOf(debug));
        }
    }

    public static void debug(String name, boolean debug) {
        if (properties == null) {
            return;
        }
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getKey().toString().equals(name)) {
                entry.setValue(String.valueOf(debug));
                break;
            }
        }
    }

    private enum LEVEL {
        verbose, debug, info, warn, error
    }

    private static void initLogMember(StackTraceElement[] sElements) {
        className = sElements[1].getClassName();
        int i = className.lastIndexOf(".");
        if (i + 1 < className.length() - 1)
            simpleClassName = className.substring(i + 1, className.length());
        else
            simpleClassName = className;
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    private synchronized static void log(LEVEL level, StackTraceElement[] sElements, String... msg) {
        if (!DEBUG) {
            Log.i("ZLogger", "DEBUG = false");
            return;
        }
        initLogMember(sElements);
        if (check()) {
            print(level, msg);
        } else {
            if (properties == null) {//不配置"log.properties"文件，则按照默认方式显示日志
                print(level, msg);
            }
        }
    }

    private static void print(LEVEL level, String... msg) {
        String tag = "";
        String text = "";
        if (msg != null && msg.length == 1) {
            tag = formatTag();
            text = msg[0];
        } else if (msg != null && msg.length == 2) {
            tag = msg[0];
            text = msg[1];
        } else {
            Log.w("ZLogger", "String... msg is illegal(null or length>2)");
            return;
        }

        tag = "ZLogger: " + tag;
        switch (level) {
            case verbose:
                Log.v(tag, text);
                break;
            case debug:
                Log.d(tag, text);
                break;
            case info:
                Log.i(tag, text);
                break;
            case warn:
                Log.w(tag, text);
                break;
            case error:
                Log.e(tag, text);
                break;
            default:
                break;
        }
    }

    private static final class LogStruct {
        String level;
        String time;
        int pid;
        int tid;
        String pckName;
        String tag;
        String text;

        LogStruct(String level, String tag, String text) {
            this.time = DateFormatUtil.format(DateFormatUtil.FORMAT_1);
            this.pid = android.os.Process.myPid();
            this.tid = android.os.Process.myTid();
            this.pckName = ZLogger.pckName;
            this.level = level;
            this.tag = tag;
            this.text = text;
        }

        @Override
        public String toString() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("level", level);
                jsonObject.put("time", time);
                jsonObject.put("pid", pid);
                jsonObject.put("tid", tid);
                jsonObject.put("pckName", pckName);
                jsonObject.put("tag", tag);
                jsonObject.put("text", text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject.toString();
        }
    }

    public static String getSaveUrl() {
        return saveUrl;
    }

    /**
     * 把日志保存到sdcard文件夹中
     *
     * @param level
     * @param msg
     */
    private static void saveLogToFile(LEVEL level, String... msg) {
        String tag = "";
        String text = "";
        if (msg != null && msg.length == 1) {
            tag = formatTag();
            text = msg[0];
        } else if (msg != null && msg.length == 2) {
            tag = msg[0];
            text = msg[1];
        } else {
            log(LEVEL.warn, new Throwable().getStackTrace(), "String... msg is illegal(null or length>2)");
            return;
        }
        File file = new File(saveUrl);
        if (!file.exists()) {
            file.mkdirs();
        }
        File logFile = new File(saveUrl + DateFormatUtil.format(DateFormatUtil.FORMAT_2) + ".log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                log(LEVEL.error, new Throwable().getStackTrace(), "create log file failed:" + e.toString());
            }
        }
        LogStruct logStruct = new LogStruct(level.name(), tag, text);
        writeToFile(logFile, logStruct);
    }

    private static void writeToFile(File logFile, LogStruct logStruct) {
        String line = logStruct.toString() + "\n";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(logFile, true); // 内容追加方式append
            fos.write(line.getBytes());
        } catch (IOException e) {
            log(LEVEL.error, new Throwable().getStackTrace(), "save log failed:" + e.toString());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log(LEVEL.error, new Throwable().getStackTrace(), "close FileOutputStream failed after save log:" + e.toString());
                }
            }
        }
    }

    private static boolean check() {
        if (properties == null) {
            Log.e("ZLogger", "properties is null");
            return false;
        }

        return true;
    }

    private static String formatTag() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(simpleClassName);
        strBuf.append(".");
        strBuf.append(methodName);
        strBuf.append(":#");
        strBuf.append(lineNumber);
        return strBuf.toString();
    }

    public static void v(String... msg) {
        log(LEVEL.verbose, new Throwable().getStackTrace(), msg);
        if (saveFile && v) {
            saveLogToFile(LEVEL.verbose, msg);
        }
    }

    public static void d(String... msg) {
        log(LEVEL.debug, new Throwable().getStackTrace(), msg);
        if (saveFile && d) {
            saveLogToFile(LEVEL.debug, msg);
        }
    }

    public static void i(String... msg) {
        log(LEVEL.info, new Throwable().getStackTrace(), msg);
        if (saveFile && i) {
            saveLogToFile(LEVEL.info, msg);
        }
    }

    public static void w(String... msg) {
        log(LEVEL.warn, new Throwable().getStackTrace(), msg);
        if (saveFile && w) {
            saveLogToFile(LEVEL.warn, msg);
        }
    }

    public static void e(String... msg) {
        log(LEVEL.error, new Throwable().getStackTrace(), msg);
        if (saveFile && e) {
            saveLogToFile(LEVEL.error, msg);
        }
    }

    public static void e(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        log(LEVEL.error, new Throwable().getStackTrace(), stringWriter.toString());
        if (saveFile && e) {
            saveLogToFile(LEVEL.error, stringWriter.toString());
        }
    }

    public static void e(String tag, Exception throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        log(LEVEL.error, new Throwable().getStackTrace(), tag, stringWriter.toString());
        if (saveFile && e) {
            saveLogToFile(LEVEL.error, stringWriter.toString());
        }
    }

    public static class DateFormatUtil {
        public static final String FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
        public static final String FORMAT_2 = "yyyyMMdd";

        public DateFormatUtil() {
        }

        public static String format() {
            return format("yyyy-MM-dd HH:mm:ss");
        }

        public static String format(String format, Date date) {
            String formatDate = (new SimpleDateFormat(format)).format(new Date());
            return formatDate;
        }

        public static String format(String format, long ms) {
            return format(format, new Date(ms));
        }

        public static String format(String format) {
            return format(format, new Date());
        }
    }
}
