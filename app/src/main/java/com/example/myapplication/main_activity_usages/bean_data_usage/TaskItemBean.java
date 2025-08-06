package com.example.myapplication.main_activity_usages.bean_data_usage;

import java.security.SecureRandom;
import java.util.UUID;

public class TaskItemBean {
    private String text;
    private int pos;
    private boolean isCompleted;
    private boolean isImportant;
    private final String task_id = generateUniqueId();
    private long dbId = -1;

    public TaskItemBean(String text) {
        this.text = text;
        this.isCompleted = false;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getPos() { return pos; }
    public void setPos(int pos) { this.pos = pos; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }

    public boolean isImportant() { return isImportant; }
    public void setImportant(boolean value) { this.isImportant = value; }

    public String getTask_id() { return task_id; }

    public long getDbId() { return dbId; }
    public void setDbId(long dbId) { this.dbId = dbId; }

    public static String generateUniqueId() {
        long timestamp = System.nanoTime();
        UUID uuid = UUID.randomUUID();
        int randomInt = new SecureRandom().nextInt();
        return timestamp + "-" + uuid.toString() + "-" + randomInt;
    }
}