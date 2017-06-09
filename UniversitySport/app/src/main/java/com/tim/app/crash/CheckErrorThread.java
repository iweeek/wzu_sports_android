package com.tim.app.logic.crash;

import com.tim.app.RT;
import com.tim.app.util.PreferenceHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


/** The check error thread. */
public class CheckErrorThread extends Thread
{
    @Override
    public void run()
    {
        try
        {
            File errorFile = new File(RT.defaultLog);

            // 控制日志上传频率
            long errorloglasttime = PreferenceHelper.ins().getLongShareData("errorloglasttime", 0);
            long diff = Math.abs(System.currentTimeMillis() - errorloglasttime);
            if (diff < 0 || diff > 1800000)
            {
                // 30分钟后
                PreferenceHelper.ins().storeLongShareData("errorloglasttime", System.currentTimeMillis());
                PreferenceHelper.ins().commit();
                if (errorFile != null)
                {
                    final File[] errors = errorFile.listFiles();
                    if (errors != null && errors.length > 0)
                    {
                        StringBuffer sb = new StringBuffer();
                        int max = 2, count = 0; // 控制日志大小
                        for (File e : errors)
                        {
                            if (count < max)
                            {
                                try
                                {
                                    sb.append(e.getName());
                                    BufferedReader fr = new BufferedReader(new FileReader(e));
                                    String line = fr.readLine();
                                    while (line != null)
                                    {

                                        sb.append(line);
                                        line = fr.readLine();
                                    }
                                    fr.close();
                                    e.delete();
                                } catch (Exception e2)
                                {
                                    e2.printStackTrace();
                                    continue;
                                }

                            } else
                            {
                                try
                                {
                                    e.delete();
                                } catch (Exception e1)
                                {
                                    e1.printStackTrace();
                                }
                            }
                            count++;
                        }

                        // APIReport.instance().sendErrorLog(sb.toString(), new StringResponseCallback()
                        // {
                        // public boolean onStringResponse(String result, int errcode, String errmsg, int id,
                        // boolean fromcache)
                        // {
                        // DLOG.d("error_report", "errcode=" + errcode + "\n result=" + result);
                        // return false;
                        // }
                        // });
                    }
                }

            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    };
}