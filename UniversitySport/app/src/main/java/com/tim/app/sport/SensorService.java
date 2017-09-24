/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tim.app.sport;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.application.library.log.DLOG;
import com.application.library.runtime.event.EventManager;
import com.tim.app.constant.EventTag;


/**
 * Background service which keeps the step-sensor listener alive to always get
 * the number of steps since boot.
 * <p/>
 * This service won't be needed any more if there is a way to read the
 * step-value without waiting for a sensor event
 */
public class SensorService extends Service implements SensorEventListener {

    private static final String TAG = "SensorService";

    private final static int NOTIFICATION_ID = 1;
    private final static long MICROSECONDS_IN_ONE_MINUTE = 60000;
    //时间间隔
    private final static long SAVE_OFFSET_TIME = AlarmManager.INTERVAL_HOUR;
    private final static int SAVE_OFFSET_STEPS = 1;

    public final static String ACTION_PAUSE = "pause";

    /**
     * 自系统启动时清零
     */
    private static int steps;
    private static int lastSaveSteps;
    private static long lastSaveTime;
    private static long lastTime;

    public final static String ACTION_UPDATE_NOTIFICATION = "updateNotificationState";

    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // nobody knows what happens here: step value might magically decrease
        // when this method is called...
    }

    /**
     * 这里的event中应该有最新的自启动以来的步数。
     *
     * @param event
     */
    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.values[0] > Integer.MAX_VALUE) {
            return;
        } else {
            steps = (int) event.values[0];
            DLOG.d(TAG, "onSensorChanged: steps = " + steps);
            EventManager.ins().sendEvent(EventTag.ON_STEP_CHANGE, 0, 0, steps);

            /*
            updateIfNecessary();
            if(lastTime == 0 || System.currentTimeMillis() - lastTime > MICROSECONDS_IN_ONE_MINUTE ) {
                // DLOG.d(TAG, "if sensor has changed, each minute insert into database.");
                Database.getInstance(this).saveDaySteps(steps);
                lastTime = System.currentTimeMillis();
            }
            */
        }
    }

    /**
     * 获取上次暂停时记录的步数，根据当前步数计算得出暂停时走的步数。插入新的记录到数据库，并且更新SP。
     * steps是自启动后的步数
     */
    private void updateIfNecessary() {
        if (steps > lastSaveSteps + SAVE_OFFSET_STEPS ||
                (steps > 0 && System.currentTimeMillis() > lastSaveTime + SAVE_OFFSET_TIME)) {
            Database db = Database.getInstance(this);
            //为true 代表 今天的步数还未存入数据库中。
            if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {
                int pauseDifference = steps -
                        getSharedPreferences("pedometer", Context.MODE_PRIVATE)
                                .getInt("pauseCount", steps);
                db.insertNewDay(Util.getToday(), steps - pauseDifference);
                if (pauseDifference > 0) {
                    // update pauseCount for the new day
                    getSharedPreferences("pedometer", Context.MODE_PRIVATE).edit()
                            .putInt("pauseCount", steps).commit();
                }
            }
            //保存自系统启动时到现在的步数
            DLOG.d(TAG, "自系统启动时到现在的步数steps:" + steps);
            db.saveCurrentSteps(steps);
            db.close();
            lastSaveSteps = steps;
            lastSaveTime = System.currentTimeMillis();
            updateNotificationState();

            Database db1 = Database.getInstance(this);
            // 上次启动时更新的步数 + 系统启动后到现在的步数 = 今天的总步数
            int steps = Math.max(db1.getCurrentSteps() + db1.getSteps(Util.getToday()), 0);
            db1.close();
            //内部会调用注册了的eventListener 的 handleMessage()方法
            EventManager.ins().sendEvent(EventTag.ON_STEP_CHANGE, 0, 0, steps);
        }
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return messenger.getBinder();
    }

    public static final int MSG_SAY_HELLO = 1;
    public static final int MSG_REPLY_ACTIVITY = 2;

    Messenger messenger = new Messenger(new SensorHandler());

    public class SensorHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    DLOG.d(TAG, msg.obj.toString());
                    Messenger sportDetailActivity = msg.replyTo;
                    Message message = new Message();
                    message.what = MSG_REPLY_ACTIVITY;
                    message.obj = "this from SensorService!";

                    try {
                        sportDetailActivity.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        /*
        if (intent != null && ACTION_PAUSE.equals(intent.getStringExtra("action"))) {
            // 开机
            if (steps == 0) {
                Database db = Database.getInstance(this);
                steps = db.getCurrentSteps();
                db.close();
            }
            SharedPreferences prefs = getSharedPreferences("pedometer", Context.MODE_PRIVATE);

            // TODO 这里对应的应该是拔掉插头，应该是没有
            if (prefs.contains("pauseCount")) { // resume counting，connect the power.
                int difference = steps -
                        prefs.getInt("pauseCount", steps); // number of steps taken during the pause
                // DLOG.d(TAG, "difference:" + difference);
                // DLOG.d(TAG, "steps：" + steps + "pauseCount:" + prefs.getInt("pauseCount", steps));
                Database db = Database.getInstance(this);
                db.addToLastEntry(-difference);
                db.close();
                prefs.edit().remove("pauseCount").apply();
                updateNotificationState();
            } else { // pause counting   // TODO 这里对应的应该是插上插头，这样stopSelf关闭服务是没有问题的
                // cancel restart
                ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE))
                        .cancel(PendingIntent.getService(getApplicationContext(), 2,
                                new Intent(this, SensorService.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));
                // 防止学生在充电的时候摇手机刷步数。
                prefs.edit().putInt("pauseCount", steps).apply();
                updateNotificationState();
                // TODO
                stopSelf();
                return START_NOT_STICKY;
            }
        }
        // TODO 更新通知
        if (intent != null && intent.getBooleanExtra(ACTION_UPDATE_NOTIFICATION, false)) {
            updateNotificationState();
        } else {
            updateIfNecessary();
        }

        // restart service every hour to save the current step count
        ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, Math.min(Util.getTomorrow(),
                        System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR), PendingIntent
                        .getService(getApplicationContext(), 2,
                                new Intent(this, SensorService.class),
                                PendingIntent.FLAG_UPDATE_CURRENT));
         */

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerSensor();
        updateNotificationState();
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        // Restart service in 500 ms
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + 500, PendingIntent
                        .getService(this, 3, new Intent(this, SensorService.class), 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNotificationState() {
        //        if (BuildConfig.DEBUG) Logger.log("SensorService updateNotificationState");
        //        SharedPreferences prefs = getSharedPreferences("pedometer", Context.MODE_PRIVATE);
        //        NotificationManager nm =
        //                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //        if (prefs.getBoolean("notification", true)) {
        //            int goal = prefs.getInt("goal", 10000);
        //            Database db = Database.getInstance(this);
        //            int today_offset = db.getSteps(Util.getToday());
        //            if (steps == 0)
        //                steps = db.getCurrentSteps(); // use saved value if we haven't anything better
        //            db.close();
        //            Notification.Builder notificationBuilder = new Notification.Builder(this);
        //            if (steps > 0) {
        //                if (today_offset == Integer.MIN_VALUE) today_offset = -steps;
        //                notificationBuilder.setProgress(goal, today_offset + steps, false).setContentText(
        //                        today_offset + steps >= goal ? getString(R.string.goal_reached_notification,
        //                                NumberFormat.getInstance(Locale.getDefault())
        //                                        .format((today_offset + steps))) :
        //                                getString(R.string.notification_text,
        //                                        NumberFormat.getInstance(Locale.getDefault())
        //                                                .format((goal - today_offset - steps))));
        //            } else { // still no step value?
        //                notificationBuilder
        //                        .setContentText(getString(R.string.your_progress_will_be_shown_here_soon));
        //            }
        //            boolean isPaused = prefs.contains("pauseCount");
        //            notificationBuilder.setPriority(Notification.PRIORITY_MIN).setShowWhen(false)
        //                    .setContentTitle(isPaused ? getString(R.string.ispaused) :
        //                            getString(R.string.notification_title)).setContentIntent(PendingIntent
        //                    .getActivity(this, 0, new Intent(this, Activity_Main.class),
        //                            PendingIntent.FLAG_UPDATE_CURRENT))
        //                    .setSmallIcon(R.drawable.ic_notification)
        //                    .addAction(isPaused ? R.drawable.ic_resume : R.drawable.ic_pause,
        //                            isPaused ? getString(R.string.resume) : getString(R.string.pause),
        //                            PendingIntent.getService(this, 4, new Intent(this, SensorService.class)
        //                                            .putExtra("action", ACTION_PAUSE),
        //                                    PendingIntent.FLAG_UPDATE_CURRENT)).setOngoing(true);
        //            nm.notify(NOTIFICATION_ID, notificationBuilder.build());
        //        } else {
        //            nm.cancel(NOTIFICATION_ID);
        //        }
    }

    private void registerSensor() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        // try {
        //     sm.unregisterListener(this);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        //单次有效计步
        Sensor stepCountSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // enable batching with delay of max 5 min
        sm.registerListener(this, stepCountSensor,
                SensorManager.SENSOR_DELAY_NORMAL, (int) (5 * MICROSECONDS_IN_ONE_MINUTE));
    }
}
