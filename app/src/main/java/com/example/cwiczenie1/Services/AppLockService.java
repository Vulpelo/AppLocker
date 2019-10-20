package com.example.cwiczenie1.Services;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

import com.example.cwiczenie1.AppElement;
import com.example.cwiczenie1.PasswordEnter;
import com.example.cwiczenie1.database.AppDatabase;
import com.example.cwiczenie1.database.AppLockerDbHelper;
import com.example.cwiczenie1.database.ResetWhen;

import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AppLockService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    Intent activityChangeIntent;

    String lastToLockApp = "";
    public static boolean passwordCorrect = false;
    boolean lockDialogNotOpened = true;

    private static String packageName;
    private final BroadcastReceiver mResetDatabaseReceiver;

    private AppLockerDbHelper dbHelper;

    public static AppElement appActualToLockElement = new AppElement("none");
    public static AppElement appPreviousToLockElement = new AppElement("none");

    private class LockerTask extends TimerTask {
        @Override
        public void run() {
            String currentToLockForegroundProcess  = currentInForeground();
            if (!currentToLockForegroundProcess.isEmpty()) {
                AppDatabase appDatabase = new AppDatabase(getApplicationContext());

                if (!currentToLockForegroundProcess.equals(packageName)) {
                    lockDialogNotOpened = true;
                    // reset all besides opened one
                    AppElement appToLockElementTmp = appDatabase.getByName(currentToLockForegroundProcess);
                    appDatabase.resetEnteredPasswordWhen(ResetWhen.ON_CLOSE);
                    if (appToLockElementTmp != null) {
                        appDatabase.updateElement(appToLockElementTmp);
                    }

                    if (appToBeLocked(currentToLockForegroundProcess)) {
                        appActualToLockElement = appToLockElementTmp;

                        if (!appActualToLockElement.enteredPass) {
                            if (lockDialogNotOpened) {
                                lockDialogNotOpened = false;
                                startActivity(activityChangeIntent);
                            }
                        } else {
                            lockDialogNotOpened = true;
                        }
                    }
                }

            } else {
                lockDialogNotOpened = true;
            }
        }

        private boolean appToBeLocked(String packageName) {
            AppDatabase appDatabase = new AppDatabase(getBaseContext());
            AppElement appElement = appDatabase.getByName(packageName);
            if (appElement == null) {
                return false;
            }
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
        mResetDatabaseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AppDatabase appDatabase = new AppDatabase(context);
                appDatabase.resetEnteredPassword();
            }
        };
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

        packageName = getApplicationContext().getPackageName();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(mResetDatabaseReceiver, filter);

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
