package com.example.myapplication1;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private OnTaskInteractionListener listener;

    public interface OnTaskInteractionListener {
        void onTaskCompleted(Task task, boolean isCompleted);
        void onTaskDeleted(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskInteractionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle, textDescription, textPriority;
        private CheckBox checkboxCompleted;
        private ImageButton btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            textPriority = itemView.findViewById(R.id.textPriority);
            checkboxCompleted = itemView.findViewById(R.id.checkboxCompleted);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Task task) {
            textTitle.setText(task.getTitle());
            textDescription.setText(task.getDescription());
            textPriority.setText(task.getPriority());
            checkboxCompleted.setChecked(task.isCompleted());

            updateTaskAppearance(task.isCompleted());
            updatePriorityColor(task.getPriority());

            checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
                updateTaskAppearance(isChecked);
                if (listener != null) {
                    listener.onTaskCompleted(task, isChecked);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskDeleted(task);
                }
            });
        }

        private void updateTaskAppearance(boolean isCompleted) {
            if (isCompleted) {
                textTitle.setPaintFlags(textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textDescription.setPaintFlags(textDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
                textDescription.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
            } else {
                textTitle.setPaintFlags(textTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                textDescription.setPaintFlags(textDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                textTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.black));
                textDescription.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
            }
        }

        private void updatePriorityColor(String priority) {
            int color;
            switch (priority.toUpperCase()) {
                case "HIGH":
                    color = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark);
                    break;
                case "MEDIUM":
                    color = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_orange_dark);
                    break;
                case "LOW":
                    color = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark);
                    break;
                default:
                    color = ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray);
                    break;
            }
            textPriority.setBackgroundColor(color);
        }
    }
}