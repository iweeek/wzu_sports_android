/*
 * Copyright 2014 Thomas Hoffmann
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.application.library.log.DLOG;

public class PowerReceiver extends BroadcastReceiver {
    private static final String TAG = "PowerReceiver";
    @Override
    public void onReceive(final Context context, final Intent intent) {
        SharedPreferences prefs =
                context.getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
        if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction()) &&
                !prefs.contains("pauseCount")) {

            DLOG.d(TAG, "onReceive: not contains pauseCount ");

            // if power connected & not already paused, then pause now
            context.startService(new Intent(context, SensorService.class)
                    .putExtra("action", SensorService.ACTION_PAUSE));
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction()) &&
                prefs.contains("pauseCount")) {
            // if power disconnected & currently paused, then resume now
            DLOG.d(TAG, "onReceive:  contains pauseCount ");

            context.startService(new Intent(context, SensorService.class)
                    .putExtra("action", SensorService.ACTION_PAUSE));
        }
    }
}
