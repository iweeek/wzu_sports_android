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

package com.tim.app.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.tim.app.sport.SensorService;
import com.tim.app.util.DownloadAppUtils;


public class UpdateAppReceiver extends BroadcastReceiver {
    public UpdateAppReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 处理下载完成
        Cursor c = null;
        try {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                if (DownloadAppUtils.downloadUpdateApkId >= 0) {
                    long downloadId = DownloadAppUtils.downloadUpdateApkId;
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    DownloadManager downloadManager = (DownloadManager) context
                            .getSystemService(Context.DOWNLOAD_SERVICE);
                    c = downloadManager.query(query);
                    if (c.moveToFirst()) {
                        int status = c.getInt(c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_FAILED) {
                            downloadManager.remove(downloadId);

                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            if (DownloadAppUtils.downloadUpdateApkFilePath != null) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setDataAndType(
                                        Uri.parse("file://"
                                                + DownloadAppUtils.downloadUpdateApkFilePath),
                                        "application/vnd.android.package-archive");
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                            }
                        }
                    }
                }
            } /*else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
                DownloadManager downloadManager = (DownloadManager) context
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
                //点击通知栏取消下载
                downloadManager.remove(ids);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
}

