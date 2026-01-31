package Database.BatchWorker;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import Database.DatabaseUsages.TasksDB.DatabaseManager;

public class NotificationWorker extends Worker {

    private static DatabaseManager databaseManager = null;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (databaseManager != null) {
            databaseManager.changeDatabases();
        }

        return Result.success();
    }

    public static void runBatchNotif(DatabaseManager dbManager) {
        Log.d("test+ngaa", calculateInitialDelay()+"");
        databaseManager = dbManager;

        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(
                        NotificationWorker.class,
                        24,
                        TimeUnit.HOURS)
                        .setInitialDelay(calculateInitialDelay(), TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                        "NotificationWorker",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        workRequest
                );
    }

    private static long calculateInitialDelay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime tomorrowMin = LocalDateTime.now().plusDays(1).withMinute(0);
            LocalDateTime tomorrowHrs = LocalDateTime.now().plusDays(1).withHour(0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return Duration.between(today, tomorrowMin).toMinutesPart()
                        + ((Duration.between(today, tomorrowHrs).toHoursPart()-1) * 60L);
            }
        }
        return 0;
    }
}
