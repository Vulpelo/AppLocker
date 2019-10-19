package com.example.cwiczenie1;

import android.app.Dialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cwiczenie1.database.AppDatabase;
import com.example.cwiczenie1.database.AppLockerDbHelper;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AppLockService extends Service {
    private Toast toast;
    private Timer timer;
    private TimerTask timerTask;
    Intent activityChangeIntent;

    String lastToLockApp = "";
    static boolean passwordCorrect = false;
    boolean lockDialogNotOpened = true;


    private AppLockerDbHelper dbHelper;

    private class LockerTask extends TimerTask {

        @Override
        public void run() {
            String currentToLockForegroundProcess  = currentInForeground();
            if (!currentToLockForegroundProcess .isEmpty() && appToBeLocked(currentToLockForegroundProcess )) {
                // not the same foreground app so reset
                if (!currentToLockForegroundProcess.contentEquals(lastToLockApp)) {
                    passwordCorrect = false;
                    lastToLockApp = currentToLockForegroundProcess ;
                }
                // the same app or different app
                // password wasn't entered correctly last time for given app
                if (!passwordCorrect) {
                    if (lockDialogNotOpened) {
                        lockDialogNotOpened = false;
                        startActivity(activityChangeIntent);
                    }
                } else {
                    lockDialogNotOpened = true;
                }
            } else {
                lockDialogNotOpened = true;
            }
        }

        private boolean appToBeLocked(String packageName) {
            AppDatabase appDatabase = new AppDatabase(getBaseContext());
            AppElement appElement = appDatabase.getByName(packageName);
            return appElement.isProtected;
        }

        private String currentInForeground() {
            String currentApp = "";
            UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
            return currentApp;
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
        activityChangeIntent = new Intent( this, PasswordEnter.class);
        activityChangeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        dbHelper = new AppLockerDbHelper(getBaseContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clearTimerSchedule();
        timerTask = new LockerTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 1000, 10);

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
        clearTimerSchedule();
        super.onDestroy();
    }
}
