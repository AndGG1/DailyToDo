package Database.BatchWorker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDate;
import java.time.ZoneId;

import Database.DatabaseUsages.TasksDB.DatabaseUtils;

public class DailyBatchWorker extends Worker {

    public DailyBatchWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        // Check if a new day has passed
        if (hasNewDayPassed(context)) {
            // 🔁 Run your batch update
            DatabaseUtils.updateDailyTasks(context);

            // Save today's date
            saveToday(context);
        }

        return Result.success();
    }

    private boolean hasNewDayPassed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("batch_prefs", Context.MODE_PRIVATE);
        String lastDate = prefs.getString("last_date", "");
        String today = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            today = LocalDate.now(ZoneId.systemDefault()).toString();
        }
        return !today.equals(lastDate);
    }

    private void saveToday(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("batch_prefs", Context.MODE_PRIVATE);
        String today = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            today = LocalDate.now(ZoneId.systemDefault()).toString();
        }
        prefs.edit().putString("last_date", today).apply();
    }
}