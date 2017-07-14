package com.application.library.log;

import android.content.Context;
import android.util.Log;

import com.application.library.util.TimeUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import static com.application.library.log.DLOG.D;
import static com.lzy.okhttputils.utils.OkLogger.tag;

/**
 * Created by zhaokaiqiang on 15/11/18.
 */
public class FileLog {
    private static final String TAG = "FileLog";
    private static FileOutputStream outputStream = null;
    private static Context context;
    private static FileOutputStream outputStreamInteralFile = null;
    private static OutputStreamWriter outputStreamWriterInternalFile = null;
    private static InputStream inputStreamInternalFile = null;
    private static InputStreamReader inputStreamReaderInternalFile = null;
    private static String internalLogFileName = "log_file";


    public static void writeToFile(String tag, File targetDirectory, String fileName, String headString, String msg) {

        fileName = (fileName == null) ? getDefaultFileName() : fileName;
        if (write(targetDirectory, fileName, msg)) {
            Log.d(tag, headString + " write log success ! location is >>>" + targetDirectory.getAbsolutePath() + "/" + fileName);
        } else {
            Log.e(tag, headString + "write log fails !");
        }
    }

    public static boolean openStreamInternalFile(Context context) {
        try {
            if (outputStreamInteralFile == null) {
                outputStreamInteralFile = context.openFileOutput(internalLogFileName, Context.MODE_PRIVATE);
            }

            if (outputStreamWriterInternalFile == null) {
                outputStreamWriterInternalFile = new OutputStreamWriter(outputStreamInteralFile, "UTF-8");
            }

            if (inputStreamInternalFile == null) {
                inputStreamInternalFile = context.openFileInput(internalLogFileName);
            }

            if (inputStreamReaderInternalFile == null) {
                inputStreamReaderInternalFile = new InputStreamReader(inputStreamInternalFile);
            }

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean closeStreamInternalFile() {
        try {
            if (outputStreamInteralFile != null) {
                outputStreamInteralFile.close();
                outputStreamInteralFile = null;
            }

            if (inputStreamInternalFile != null) {
                inputStreamInternalFile.close();
                inputStreamInternalFile = null;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean writeToInternalFile(String msg) {
        try {
            outputStreamWriterInternalFile.write(TimeUtil.getDateToString(System.currentTimeMillis()) + ": ");
            outputStreamWriterInternalFile.write(msg);
            outputStreamWriterInternalFile.write(System.getProperty("line.separator"));
            outputStreamWriterInternalFile.flush();
            DLOG.d(TAG, "writeToInternalFile: " + msg);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean readFromInternalFile(String content) {
        try {
            BufferedReader bufferedReader = new BufferedReader(inputStreamReaderInternalFile);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                DLOG.d(TAG, "line: " + line);
                content += line;
            }
            DLOG.d(TAG, "content: " + content);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean write(File dicName, String fileName, String msg) {

        File file = new File(dicName, fileName);

        try {
            OutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            outputStreamWriter.write(msg);
            outputStreamWriter.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Boolean read(File dicName, String fileName, String content) {

        File file = new File(dicName, fileName);

        try {
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while ((content += bufferedReader.readLine()) != null) {
                DLOG.d(TAG, "logFileLine: " + content);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static String getDefaultFileName() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder("KLog_");
        stringBuilder.append(Long.toString(System.currentTimeMillis() + random.nextInt(10000)).substring(4));
        stringBuilder.append(".txt");
        return stringBuilder.toString();
    }

}
