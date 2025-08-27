package com.taskflow.ben53;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {
    private TextView titleDisplay;
    private TextView descriptionDisplay;
    private TextView assigneeDisplay;
    private TextView priorityDisplay;
    private TextView dueDateDisplay;
    private TextView statusDisplay;
    private Button editButton;
    private Button analyticsButton;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        titleDisplay = findViewById(R.id.titleDisplay);
        descriptionDisplay = findViewById(R.id.descriptionDisplay);
        assigneeDisplay = findViewById(R.id.assigneeDisplay);
        priorityDisplay = findViewById(R.id.priorityDisplay);
        dueDateDisplay = findViewById(R.id.dueDateDisplay);
        statusDisplay = findViewById(R.id.statusDisplay);
        editButton = findViewById(R.id.editButton);
        analyticsButton = findViewById(R.id.analyticsButton);

        task = getIntent().getParcelableExtra("task");

        if (task != null) {
            displayTask();
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskDetailActivity.this, TaskEditorActivity.class);
                intent.putExtra("task", task);
                startActivity(intent);
            }
        });

        analyticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskDetailActivity.this, TaskAnalyticsActivity.class);
                intent.putExtra("task", task);
                startActivity(intent);
            }
        });
    }

    private void displayTask() {
        titleDisplay.setText("Title: " + task.getTitle());
        descriptionDisplay.setText("Description: " + task.getDescription());
        assigneeDisplay.setText("Assigned to: " + task.getAssignee());

        String[] priorityLabels = {"", "Low", "Medium", "High", "Critical"};
        int priority = task.getPriority();
        String priorityText = (priority >= 1 && priority <= 4) ? priorityLabels[priority] : "Unknown";
        priorityDisplay.setText("Priority: " + priorityText);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        dueDateDisplay.setText("Due: " + sdf.format(new Date(task.getDueDate())));

        statusDisplay.setText("Status: " + (task.isCompleted() ? "Completed" : "Pending"));
    }
}
