package com.example.myapplication.MainLogic.Data.Repository;

import android.content.Context;
import android.database.Cursor;

import com.example.myapplication.MainLogic.Data.Model.TaskItemBean;

import java.util.ArrayList;
import java.util.List;

import Database.DatabaseUsages.TasksDB.DatabaseManager;

public class TaskRepository {
    DatabaseManager dbManager;
    public TaskRepository(Context context) {
        dbManager = new DatabaseManager(context).open();
    }

    public List<TaskItemBean> loadTasksForDay(int dayValue) {
        List<TaskItemBean> taskList = new ArrayList<>();

        Cursor cursor = dbManager.fetch(dayValue);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                String text = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String checked = cursor.getString(cursor.getColumnIndexOrThrow("checked"));

                TaskItemBean task = new TaskItemBean(text);
                task.setDbId(id);
                task.setImportant("important".equals(desc));
                task.setCompleted("1".equals(checked));
                task.setPos(taskList.size());

                taskList.add(task);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return taskList;
    }

    public long insertTask(TaskItemBean task, int dayValue) {
        String desc = task.isImportant() ? "important" : "regular";
        String checked = task.isCompleted() ? "1" : "0";
        long id = dbManager.insert(task.getText(), desc, checked, dayValue);
        task.setDbId(id);

        return id;
    }

    public void deleteTask(TaskItemBean task, int dayValue) {
        dbManager.delete(task.getDbId(), dayValue);
    }

    public void updateTask(TaskItemBean task, int dayValue) {
        String desc = task.isImportant() ? "important" : "regular";
        String checked = task.isCompleted() ? "1" : "0";
        dbManager.update(task.getDbId(), task.getText(), desc, checked, dayValue);
    }
}