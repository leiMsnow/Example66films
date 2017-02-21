package cn.com.films66.app.base;

import android.os.Build;
import android.os.Environment;
import android.os.Looper;

import com.shuyu.core.uils.AppUtils;
import com.shuyu.core.uils.DateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 */
public class MCrashHandler implements UncaughtExceptionHandler {

    private static MCrashHandler mInstance = new MCrashHandler();

    private boolean cancelUp = false;
    private UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> mLogInfo = new HashMap<>();

    private MCrashHandler() {

    }

    public static MCrashHandler getInstance() {
        return mInstance;
    }

    public void init() {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
        if (!handleException(paramThrowable) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(paramThread, paramThrowable);
        } else {
            try {
                // 如果处理了，让程序继续运行秒再退出，保证文件保存并上传到服务器
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    private boolean handleException(Throwable paramThrowable) {
        if (paramThrowable == null)
            return false;
        new Thread() {
            public void run() {
                Looper.prepare();
                Looper.loop();
            }
        }.start();
        // 获取设备参数信息
        getDeviceInfo();
        // 保存日志文件
        saveCrashLogToFile(paramThrowable);
        return true;
    }

    private void getDeviceInfo() {

        mLogInfo.put("versionName", AppUtils.getVersionName());
        mLogInfo.put("versionCode", String.valueOf(AppUtils.getAppVersion()));

        // 反射机制
        Field[] mFields = Build.class.getDeclaredFields();
        // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
        for (Field field : mFields) {
            try {
                field.setAccessible(true);
                mLogInfo.put(field.getName(), field.get("").toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCrashLogToFile(Throwable paramThrowable) {
        // 保存文件，设置文件名
        final String time = DateUtils.longToString(System.currentTimeMillis()
                , "yyyy-MM-dd HH:mm:ss");
        saveCrashLogToFile(paramThrowable, String.valueOf(time));
    }

    private void saveCrashLogToFile(Throwable paramThrowable, String logName) {
        StringBuffer mStringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : mLogInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mStringBuffer.append(key + "=" + value + "\r\n");
        }
        Writer mWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mWriter);
        paramThrowable.printStackTrace(mPrintWriter);
        paramThrowable.printStackTrace();
        Throwable mThrowable = paramThrowable.getCause();
        final String time = DateUtils.longToString(System.currentTimeMillis()
                , "yyyy-MM-dd HH:mm:ss");
        mPrintWriter.append("\r\n-----------------------\r\n");
        mPrintWriter.append("当前时间：[" + time + "]\r\n");
        // 迭代栈队列把所有的异常信息写入writer中
        while (mThrowable != null) {
            mThrowable.printStackTrace(mPrintWriter);
            // 换行 每个个异常栈之间换行
            mPrintWriter.append("\r\n");
            mThrowable = mThrowable.getCause();
        }
        mPrintWriter.append("\r\n-----------------------\r\n");
        // 记得关闭
        mPrintWriter.close();
        String mResult = mWriter.toString();
        mStringBuffer.append(mResult);
        String mFileName = logName + ".txt";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                // 文件存储路径
                String filePath = Environment.getExternalStorageDirectory().toString();
                File mDirectory = new File(filePath + "/LOGS/" + mFileName);
                if (!mDirectory.exists())
                    mDirectory.createNewFile();
                FileOutputStream mFileOutputStream = new FileOutputStream(mDirectory);
                mFileOutputStream.write(mStringBuffer.toString().getBytes());
                mFileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                return;
            }
        }
    }


}
