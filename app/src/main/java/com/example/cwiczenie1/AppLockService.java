package com.example.cwiczenie1;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.Time;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AppLockService extends Service {
    private Toast toast;
    private Timer timer;
    private TimerTask timerTask;

    private class FrequentTask extends TimerTask {
        @Override
        public void run() {
            String foregroundAppName = currentInForeground();

            if (appToLock(foregroundAppName)) {
                // TODO: Lock screen
            }

            Log.w("Service", "Still running");
        }

        private String currentInForeground() {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo rpi: runningAppProcessInfo) {
                if(rpi.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return rpi.processName;
                }
            }
            return null;
        }

        private boolean appToLock(String processName) {
            // TODO: look to SQLite if given processName is listed
            return false;
        }

        public void confirmPassword(View view) {
            // TODO: pass not correct
            // leave dialog on top

            // TODO: pass correct
            // dismiss
            return;
        }

    }

    public AppLockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("Service", "Create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("Service", "StartCommand");

        clearTimerSchedule();
        timerTask = new FrequentTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 10000, 2000);

        return super.onStartCommand(intent, flags, startId);
    }

    private void clearTimerSchedule() {
        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }

    @Override
    public void onDestroy() {
        Log.w("Service", "Destroy");
        clearTimerSchedule();
        super.onDestroy();
    }
}
