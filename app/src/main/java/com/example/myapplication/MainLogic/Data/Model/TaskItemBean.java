package com.example.myapplication.MainLogic.Data.Model;

public class TaskItemBean {
    private String text;
    private int pos;
    private boolean isCompleted;
    private boolean isImportant;
    private String task_id = "NOT_INIT";
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

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }
}