package Database.BatchWorker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
        databaseManager = dbManager;

        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(
                        NotificationWorker.class,
                        18,
                        TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                        "NotificationWorker",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        workRequest
                );
    }
}