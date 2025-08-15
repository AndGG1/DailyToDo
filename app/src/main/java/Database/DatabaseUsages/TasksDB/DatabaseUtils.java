package Database.DatabaseUsages.TasksDB;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DatabaseUtils {

    public static void updateDailyTasks(Context context) {
        DatabaseManager dbManager = new DatabaseManager(context);
        try {
            dbManager.open();

            for (int day = 1; day <= 3; day++) {
                updateTasksForDay(dbManager, day);
            }
        } catch (Exception e) {
            Log.e("DatabaseUtils", "Error updating tasks", e);
        } finally {
            dbManager.close();
        }
    }

    private static void updateTasksForDay(DatabaseManager dbManager, int day) {

        //TODO: Finish logic!!!
        Cursor cursor = dbManager.fetch(day);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //logic...
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}