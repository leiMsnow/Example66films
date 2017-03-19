package com.shuyu.core.uils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.shuyu.core.CoreApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 跟App相关的辅助类
 */
public class AppUtils {

    private AppUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取系统版本号
     *
     * @return 获取系统版本号
     */
    public static int getAppVersion() {
        try {
            PackageInfo info = CoreApplication.getApplication().getPackageManager().getPackageInfo(
                    CoreApplication.getApplication().getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取应用程序名称
     *
     * @return 应用程序名称
     */
    public static String getAppName() {
        try {
            PackageManager packageManager = CoreApplication.getApplication().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    CoreApplication.getApplication().getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return CoreApplication.getApplication().getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPackageName() {
        try {
            return CoreApplication.getApplication().getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName() {
        try {
            PackageManager packageManager = CoreApplication.getApplication().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    CoreApplication.getApplication().getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getIMSI() {
        TelephonyManager mTelephonyMgr = (TelephonyManager)
                CoreApplication.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId() == null ? "" : mTelephonyMgr.getSubscriberId();
    }

    public static String getIMEI() {
        TelephonyManager mTelephonyMgr = (TelephonyManager)
                CoreApplication.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getDeviceId() == null ? "000000000000000" : mTelephonyMgr.getDeviceId();
    }

    public static String getTelNumber() {
        TelephonyManager mTelephonyMgr = (TelephonyManager)
                CoreApplication.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number() == null ? "" : mTelephonyMgr.getLine1Number();
    }

    public static String getSerialNumber() {
        TelephonyManager mTelephonyMgr = (TelephonyManager)
                CoreApplication.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSimSerialNumber() == null ? "" : mTelephonyMgr.getSimSerialNumber();
    }

    public static String getUUID() {
        String androidId = Settings.Secure.getString(CoreApplication.getApplication().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        UUID deiceUUID = new UUID(androidId.hashCode(), ((long) getIMEI().hashCode() << 32) |
                getSerialNumber().hashCode());
        return "client_" + deiceUUID.toString();
    }


    public static String getCPUInfo() {
        String str1 = "/proc/cpuinfo";
        String str2;
        String cpuInfo = "";  //1-cpu型号
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo = cpuInfo + arrayOfString[i] + " ";
            }
            //2-cpu频率
//            str2 = localBufferedReader.readLine();
//            arrayOfString = str2.split("\\s+");
//            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo;
    }


    /**
     * 发送短信
     *
     * @param context context
     */
    public static void sendMsg(Context context) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        context.startActivity(intent);
    }

    public static void openBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void install(Context context, String filePath) {
        chmod("777", filePath);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) CoreApplication.getApplication()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(CoreApplication.getApplication().getPackageName())) {
                return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }
}
