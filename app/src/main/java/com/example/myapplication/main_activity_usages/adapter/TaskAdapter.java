package com.example.myapplication.main_activity_usages.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.main_activity_usages.FragmentUsage.EmptyFragment;
import com.example.myapplication.main_activity_usages.FragmentUsage.TaskSettingsFragment;
import com.example.myapplication.main_activity_usages.bean_data_usage.TaskItemBean;
import com.example.myapplication.main_activity_usages.time_class.Days;

import java.util.Collections;
import java.util.List;

import Database.DatabaseUsages.TasksDB.DatabaseManager;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<TaskItemBean> taskList;
    private final DatabaseManager dbManager;
    private String newestSettingsPos = "";
    private final Days days;

    public TaskAdapter(Context context, List<TaskItemBean> taskList, DatabaseManager dbManager, Days days) {
        this.taskList = taskList;
        this.dbManager = dbManager;
        this.days = days;
    }

    private void sortTasks() {
        Collections.sort(taskList, (a, b) -> {
            int priorityComparison = Boolean.compare(b.isImportant(), a.isImportant());
            if (priorityComparison != 0) return priorityComparison;
            return Integer.compare(a.getPos(), b.getPos());
        });
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        EditText taskInput;
        CheckBox taskCheckbox;
        ImageButton regularButton, importantButton;
        ImageView arrowButton;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskInput = itemView.findViewById(R.id.taskInput);
            taskCheckbox = itemView.findViewById(R.id.taskCheckbox);
            regularButton = itemView.findViewById(R.id.regularButton);
            importantButton = itemView.findViewById(R.id.importantButton);
            arrowButton = itemView.findViewById(R.id.expandArrow);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskItemBean item = taskList.get(position);

        holder.taskCheckbox.setOnCheckedChangeListener(null);
        holder.taskInput.setText("");

        holder.taskInput.setText(item.getText());
        holder.taskCheckbox.setChecked(item.isCompleted());

        holder.taskInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newText = holder.taskInput.getText().toString();
                item.setText(newText);
                dbManager.update(
                        item.getDbId(),
                        newText,
                        item.isImportant() ? "important" : "regular",
                        item.isCompleted() ? "1" : "0",
                        days.getCurrentDay()
                );
            }
        });

        holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            dbManager.update(
                    item.getDbId(),
                    item.getText(),
                    item.isImportant() ? "important" : "regular",
                    isChecked ? "1" : "0",
                    days.getCurrentDay()
            );
        });

        if (item.isImportant()) {
            holder.taskInput.setTextColor(Color.parseColor("#812BE0"));
            holder.itemView.setBackgroundResource(R.drawable.task_background_important);
        } else {
            holder.taskInput.setTextColor(Color.parseColor("#333333"));
            holder.itemView.setBackgroundResource(R.drawable.task_background);
        }

        holder.regularButton.setOnClickListener(v -> {
            item.setImportant(false);
            dbManager.update(
                    item.getDbId(),
                    item.getText(),
                    "regular",
                    item.isCompleted() ? "1" : "0",
                    days.getCurrentDay()
            );
            sortTasks();
        });

        holder.importantButton.setOnClickListener(v -> {
            item.setImportant(true);
            dbManager.update(
                    item.getDbId(),
                    item.getText(),
                    "important",
                    item.isCompleted() ? "1" : "0",
                    days.getCurrentDay()

            );
            sortTasks();
        });

        holder.arrowButton.setOnClickListener(v -> {
            Context context = v.getContext();

            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                TaskSettingsFragment fragment = new TaskSettingsFragment();
                if (!holder.arrowButton.getContentDescription().toString().equals("down_arrow")) {
                    holder.arrowButton.setContentDescription("down_arrow");
                    holder.arrowButton.setImageResource(R.drawable.semicircle_arrow_down);

                    if (newestSettingsPos.equals(item.getTask_id())) {
                        transaction.replace(R.id.taskSettingsHost, new EmptyFragment());
                        transaction.commit();
                    }
                } else {
                    newestSettingsPos = item.getTask_id();
                    holder.arrowButton.setContentDescription("up_arrow");
                    holder.arrowButton.setImageResource(R.drawable.semicircle_arrow_up);

                    transaction.setCustomAnimations(
                            R.anim.slide_down,
                            R.anim.fade_out
                    );
                    transaction.replace(R.id.taskSettingsHost, fragment);
                    transaction.commit();
                }

                View host = activity.findViewById(R.id.taskSettingsHost);
                if (host != null) {
                    host.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e("Adapter", "Context is not an AppCompatActivity");
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}

