package com.example.myapplication.MainLogic.UI;

import static com.example.myapplication.MainLogic.Data.Repository.APIRepositoryKt.getRes;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainLogic.UI.Fragments.EmptyFragment;
import com.example.myapplication.MainLogic.UI.Fragments.TaskSettingsFragment;
import com.example.myapplication.MainLogic.UI.ViewModels.EmojiViewModel;
import com.example.myapplication.MainLogic.UI.ViewModels.TaskViewModel;
import com.example.myapplication.R;
import com.example.myapplication.MainLogic.Data.Model.TaskItemBean;
import com.example.myapplication.MainLogic.Data.Model.Days;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final EmojiViewModel emojiViewModel;
    private final List<TaskItemBean> taskList;
    private final TaskViewModel viewModel;
    private final Days days;
    private String newestSettingsPos = "";

    public TaskAdapter(Context context, List<TaskItemBean> taskList, Days days) {
        this.taskList = taskList;
        this.days = days;
        emojiViewModel = new EmojiViewModel();
        viewModel = new TaskViewModel(context);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        EditText taskInput;
        CheckBox taskCheckbox;
        ImageButton regularButton, importantButton;
        ImageView arrowButton;
        final String _id = UUID.randomUUID().toString();

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskInput = itemView.findViewById(R.id.taskInput);
            taskCheckbox = itemView.findViewById(R.id.taskCheckbox);
            regularButton = itemView.findViewById(R.id.regularButton);
            importantButton = itemView.findViewById(R.id.importantButton);
            arrowButton = itemView.findViewById(R.id.expandArrow);

            emojiViewModel.addNewState(_id);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        return new TaskViewHolder(view);
    }



    //Core functionality
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskItemBean item = taskList.get(position);
        item.setTask_id(holder._id);

        holder.taskCheckbox.setOnCheckedChangeListener(null);
        holder.taskInput.setText("");

        holder.taskInput.setText(item.getText());
        holder.taskCheckbox.setChecked(item.isCompleted());

        holder.taskInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newText = holder.taskInput.getText().toString();
                item.setText(newText);
                viewModel.updateTask(item, days.getCurrentDay());

                getRes(holder.taskInput.getText().toString(), holder.taskInput);
            }
        });

        holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            viewModel.updateTask(item, days.getCurrentDay());
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
            viewModel.updateTask(item, days.getCurrentDay());
            sortTasks();
        });

        holder.importantButton.setOnClickListener(v -> {
            item.setImportant(true);
            viewModel.updateTask(item, days.getCurrentDay());
            sortTasks();
        });

        holder.arrowButton.setOnClickListener(v -> {
            Context context = v.getContext();

            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                Fragment fragment = new TaskSettingsFragment(); // Use your Compose-based fragment

                if (!"down_arrow".equals(holder.arrowButton.getContentDescription().toString())) {
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

    //Additional methods
    public void sortTasks() {
        Collections.sort(taskList, (a, b) -> {
            int priorityComparison = Boolean.compare(b.isImportant(), a.isImportant());
            if (priorityComparison != 0) return priorityComparison;
            return Integer.compare(a.getPos(), b.getPos());
        });
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public TaskViewModel getViewModel() {
        return viewModel;
    }

    public EmojiViewModel getEmojiViewModel() {
        return emojiViewModel;
    }
}
