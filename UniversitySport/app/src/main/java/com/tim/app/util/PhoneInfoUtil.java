package com.tim.app.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.tim.app.server.logic.UserManager;
import com.tim.app.server.net.NetWorkRequestParams;
import com.application.library.log.DLOG;
import com.application.library.util.PackageUtil;
import com.application.library.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * 设备相关工具类.
 */
public class PhoneInfoUtil {


    /**
     * 获取屏幕宽高
     *
     * @param activity the activity
     * @return the phone screen size
     */
    @SuppressLint("NewApi")
    public static String getPhoneScreenSize(Activity activity) {
        String display = "";
        int width = 0;
        int height = 0;
        if (Version.hasHoneycombMR2()) {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            width = size.x;
            height = size.y;
        } else {
            width = activity.getWindowManager().getDefaultDisplay().getWidth();
            height = activity.getWindowManager().getDefaultDisplay().getHeight();
        }
        if (width > 0 && height > 0) {
            display = width + "|" + height;
        }
        return display;
    }


    /**
     * 通知是否打开
     * @param context
     * @return
     */
//    public static boolean isNotificationEnabled(Context context) {
//
//        String CHECK_OP_NO_THROW = "checkOpNoThrow";
//        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
//
//        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//        ApplicationInfo appInfo = context.getApplicationInfo();
//        String pkg = context.getApplicationContext().getPackageName();
//        int uid = appInfo.uid;
//
//        Class appOpsClass = null;
//     /* Context.APP_OPS_MANAGER */
//        try {
//            appOpsClass = Class.forName(AppOpsManager.class.getDBName());
//            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
//                    String.class);
//            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
//
//            int value = (Integer) opPostNotificationValue.get(Integer.class);
//            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 跳转到设置页面
     * @param context
     */
    public static void goToSetting(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 运行系统在5.x环境使用
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
            return;
        }
    }

    /**
     * 是否root
     *
     * @return
     */
    private static synchronized boolean isRoot() {
//        Process process = null;
//        DataOutputStream os = null;
//        try {
//            process = Runtime.getRuntime().exec("su");
//            os = new DataOutputStream(process.getOutputStream());
//            os.writeBytes("exit\n");
//            os.flush();
//            int exitValue = process.waitFor();
//            if (exitValue == 0) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            DLOG.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
//                    + e.getMessage());
//            return false;
//        } finally {
//            try {
//                if (os != null) {
//                    os.close();
//                }
//                if (null != process) {
//                    process.destroy();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return false;
    }

    /**
     * 返回mac地址
     *
     * @return
     */
    private static String getMacAddress(Context context) {
        String macAddress = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getMacAddress() != null) {
            macAddress = wifiInfo.getMacAddress();
        }
        return macAddress;
    }

    /**
     * 手机的序列号
     *
     * @return
     */
    private static String getSerialNumber() {
        return android.os.Build.SERIAL;
    }

    /**
     * service providers获取手机服务商信息
     *
     * @param context
     * @return
     */
    private static String getProvidersName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String ProvidersName = null;
        // 返回唯一的用户ID;就是这张卡的编号神马的
        String imsi = telephonyManager.getSubscriberId();
        if (TextUtils.isEmpty(imsi)) {
            return "";
        }
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
            ProvidersName = "中国移动";
        } else if (imsi.startsWith("46001")) {
            ProvidersName = "中国联通";
        } else if (imsi.startsWith("46003")) {
            ProvidersName = "中国电信";
        }
        return ProvidersName;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取系统版本
     *
     * @return
     */
    private static String getOsVersion() {
        return android.os.Build.VERSION.RELEASE;
    }


    private static final int ERROR = -1;

    /**
     * SDCARD是否存
     */
    private static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    private static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    private static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取SDCARD剩余存储空间
     *
     * @return
     */
    private static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    /**
     * 获取imsi
     *
     * @param context
     * @return
     */
    public static String getPhoneImsi(Context context) {
        String imsi = "";
        if (context != null) {
            try {
                TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(
                        Context.TELEPHONY_SERVICE);
                if (context.getApplicationContext().checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED) {
                    imsi = tm.getSubscriberId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null == imsi) {
                    imsi = "";
                }
            }
        }
        return imsi;
    }

    /**
     * 获取imei
     *
     * @param context
     * @return
     */
    public static String getPhoneImei(Context context) {
        String imei = "";
        if (context != null) {
            try {
                TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(
                        Context.TELEPHONY_SERVICE);
                if (context.getApplicationContext().checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED) {
                    imei = tm.getDeviceId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(imei)) {
            imei = getUniquePsuedoID();
        }
        return imei;
    }

    /**
     * 获取SDCARD总的存储空间
     *
     * @return
     */
    private static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    /**
     * 获取可用内存
     *
     * @param context
     * @return
     */
    private static String getAvailMemory(Context context) {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
    }

    /**
     * 获取总内存
     *
     * @param context
     * @return
     */
    private static String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                DLOG.d(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {

        }
        return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 获取网络状态
     *
     * @param context
     * @return
     */
    private static String getNeworkType(Context context) {
        //获取网络连接管理者
        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = telephonyManager.getNetworkType();
        return String.valueOf(networkType);
    }

    /**
     * 获取CPU信息
     *
     * @return
     */
    private static String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo[0];
    }

    /**
     * 返回语言
     *
     * @return
     */
    public static String getLanguage() {
        return Locale.getDefault().getLanguage().toString();
    }

    /**
     * 返回位置信息
     *
     * @return
     */
    private static String getLocation(Context context) {
        if (!TextUtils.isEmpty(getGPS(context))) {
            return getGPS(context);
        } else {
            return getGsmCellLocation(context);
        }
    }


    /**
     * 获取GPS定位.
     *
     * @return the gps
     */
    private static String getGPS(Context context) {
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getApplicationContext().getSystemService(
                    Context.LOCATION_SERVICE);
            // 查找到服务信息
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
            String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
            if (provider != null) {
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
                        if (location != null) {
                            return String.valueOf(location.getLatitude()).concat("|")
                                    .concat(String.valueOf(location.getLongitude()));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }


    /**
     * 获取基站定位,不存在返回-1. <br>
     * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
     *
     * @return the gsm cell location
     */
    private static String getGsmCellLocation(Context context) {
        if (context != null) {
            try {
                TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(
                        Context.TELEPHONY_SERVICE);
                CellLocation gcl = tm.getCellLocation();
                if (gcl != null) {
                    if (gcl instanceof GsmCellLocation) {
                        return String.valueOf(((GsmCellLocation) gcl).getCid())
                                .concat("|").concat(String.valueOf(((GsmCellLocation) gcl).getLac()));
                    } else if (gcl instanceof CdmaCellLocation) {
                        return String.valueOf(((CdmaCellLocation) gcl).getBaseStationLatitude())
                                .concat("|")
                                .concat(String.valueOf(((CdmaCellLocation) gcl).getBaseStationLongitude()));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取app版本
     *
     * @param context
     * @return
     */
    private static int getAppVersion(Context context) {
        return PackageUtil.getVersionCode(context);
    }

    /**
     * 获取app渠道号
     *
     * @param context
     * @return
     */
    private static String getAppChannel(Context context) {
        return PackageUtil.getApplicationData(context, "UMENG_CHANNEL");
    }

    /**
     * 获取初始化要上传的参数
     *
     * @param context
     * @return
     */
    public static HashMap<String, Object> getInitParams(Context context) {
        HashMap<String, Object> params = new HashMap<>();
        try {
            params.put(NetWorkRequestParams.IMSI, getPhoneImsi(context));
            params.put(NetWorkRequestParams.IMEI, getPhoneImei(context));
            params.put(NetWorkRequestParams.OS, "android");
//            params.put(NetWorkRequestParams.DISPLAY, UserManager.instance().getPhoneDisplay());
            params.put(NetWorkRequestParams.OS_VERSION, getOsVersion());
            params.put(NetWorkRequestParams.MAC, getMacAddress(context));
            params.put(NetWorkRequestParams.SERIAL_NUMBER, getSerialNumber());
            params.put(NetWorkRequestParams.NETWORK_STATE, getNeworkType(context));
            params.put(NetWorkRequestParams.MODEL, getDeviceModel());
            params.put(NetWorkRequestParams.IS_OFFICIAL, isRoot());
            params.put(NetWorkRequestParams.STORAGE, getTotalInternalMemorySize());
            params.put(NetWorkRequestParams.MEMORY, getTotalMemory(context));
            params.put(NetWorkRequestParams.CPU, getCpuInfo());
            params.put(NetWorkRequestParams.LANGUAGE, getLanguage());
            params.put(NetWorkRequestParams.LOCATION, getLocation(context));
            params.put(NetWorkRequestParams.CHANNEL, getAppChannel(context));
            params.put(NetWorkRequestParams.VERSION, getAppVersion(context));
            params.put(NetWorkRequestParams.CHANNEL_ID, UserManager.instance().getPushChannelId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
//        int versioncode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
//            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            DLOG.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID() {
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
