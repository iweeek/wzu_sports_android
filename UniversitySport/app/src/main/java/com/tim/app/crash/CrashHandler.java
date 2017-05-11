package com.tim.app.logic.crash;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Looper;
import android.text.format.Formatter;
import android.text.format.Time;
import android.view.Gravity;
import android.widget.Toast;

import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.util.PhoneInfoUtil;
import com.tim.app.util.PreferenceHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;

/**
 *  截获异常闪退并记录异常 
 */
public class CrashHandler implements UncaughtExceptionHandler
{

    private UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler INSTANCE;
    private Context mContext;

    private HashMap<String, String> mDeviceCrashInfo = new HashMap<String, String>();
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";

    /** 错误报告文件的扩展名 */
    private static final String CRASH_REPORTER_EXTENSION = ".cre";

    private CrashHandler()
    {}

    public static CrashHandler getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    public void init(Context ctx)
    {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        // TODO Auto-generated method stub

        if (!handleException(ex) && mDefaultHandler != null)
        {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else
        {
            // Sleep一会后结束程序
            try
            {
                Thread.sleep(500);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            // CommonUtil.exitApp(mContext, true);
            try
            {
                System.exit(10);
                android.os.Process.killProcess(android.os.Process.myPid());
            } catch (Throwable e)
            {
                
            }
        }

    }

    private boolean handleException(final Throwable ex)
    {
        if (ex == null)
        {
            return true;
        }
        // final String msg = ex.getLocalizedMessage();
        // if (msg == null)
        // {
        // return false;
        // }
        // 使用Toast来显示异常信息
        new Thread("crashHandler")
        {
            @Override
            public void run()
            {
                try
                {
                    Looper.prepare();

                    Toast toast = Toast.makeText(mContext, R.string.crashhandler_sorry_error, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Looper.loop();
                } catch (Exception e)
                {
                    e.printStackTrace();
                } catch (Error e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
        collectCrashDeviceInfo(mContext);
        saveCrashInfoToFile(ex, null);
        return true;
    }

    private String getAvailMemory()
    {// 获取android当前可用内存大小

        ActivityManager am = (ActivityManager) RT.application.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);

        return Formatter.formatFileSize(RT.application, mi.availMem);
    }

    public String saveCrashInfoToFile(Throwable ex, Writer writer)
    {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null)
        {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = info.toString();
        printWriter.close();
        String exstr = ex.getLocalizedMessage();
        if (exstr == null)
            exstr = ex.getClass().getName();
        mDeviceCrashInfo.put("EXEPTION", exstr);
        mDeviceCrashInfo.put(STACK_TRACE, result);
        try
        {
            // long timestamp = System.currentTimeMillis();
            Time t = new Time();
            t.setToNow();

            String date = t.year + "-" + (t.month + 1) + "-" + t.monthDay;
            String time = t.hour + "-" + t.minute + "-" + t.second;
            String fileName = "crash-" + date + "_" + time + CRASH_REPORTER_EXTENSION;
            Writer trace = null;
            if (writer != null)
            {
                trace = writer;
            } else
            {
                File file = new File(RT.defaultLog + "/" + fileName);
                if (!file.getParentFile().exists())
                {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists())
                {
                    file.createNewFile();
                }
                trace = new FileWriter(file);

            }
            traceErrorLog(trace);

            trace.flush();
            trace.close();
            return fileName;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void traceErrorLog(Writer trace) throws IOException
    {
        try
        {
            trace.write("#UID :".concat(PreferenceHelper.ins().getStringShareData("mfcreuid", "0")));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        trace.write("#CLIENTVERSION :" + PhoneInfoUtil.getAppVersionName(RT.application));// 版本名
        trace.write("#CHANNELCODE :" + RT.AppInfo.Channel);// 渠道号
        trace.write("#USERAGENT : " + PhoneInfoUtil.getDeviceModel());// 手机型号
        trace.write("#IMEI : " + PhoneInfoUtil.getPhoneImei(RT.application));
        trace.write("#IMSI : " + PhoneInfoUtil.getPhoneImsi(RT.application));
        trace.write("#AvailMemory:" + getAvailMemory());
        trace.write("#SDK : " + android.os.Build.VERSION.SDK_INT);

        trace.write("#" + VERSION_NAME + ":");
        trace.write(mDeviceCrashInfo.get(VERSION_NAME));

        trace.write("#" + VERSION_CODE + ":");
        trace.write(mDeviceCrashInfo.get(VERSION_CODE));

        trace.write("#" + "EXEPTION" + ":");
        trace.write(mDeviceCrashInfo.get("EXEPTION"));

        trace.write("#" + STACK_TRACE + ":");
        trace.write(mDeviceCrashInfo.get(STACK_TRACE));
    }

    public void collectCrashDeviceInfo(Context ctx)
    {
        try
        {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null)
            {
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, "" + pi.versionCode);
            }
        } catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }

    }

}
