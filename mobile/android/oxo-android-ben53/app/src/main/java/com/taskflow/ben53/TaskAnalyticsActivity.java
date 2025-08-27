package com.taskflow.ben53;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskAnalyticsActivity extends AppCompatActivity {
    private TextView analyticsTitle;
    private TextView taskSummary;
    private TextView performanceMetrics;
    private TextView riskAssessment;
    private Button backButton;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_analytics);

        analyticsTitle = findViewById(R.id.analyticsTitle);
        taskSummary = findViewById(R.id.taskSummary);
        performanceMetrics = findViewById(R.id.performanceMetrics);
        riskAssessment = findViewById(R.id.riskAssessment);
        backButton = findViewById(R.id.backButton);

        task = getIntent().getParcelableExtra("task");

        if (task != null) {
            displayAnalytics();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void displayAnalytics() {
        analyticsTitle.setText("Task Analytics Dashboard");

        StringBuilder summary = new StringBuilder();
        summary.append("Task Overview\n\n");
        summary.append("Title: ").append(task.getTitle()).append("\n");
        summary.append("Assignee: ").append(task.getAssignee()).append("\n");
        summary.append("Priority Level: ").append(task.getPriority()).append("\n");
        summary.append("Status: ").append(task.isCompleted() ? "Completed" : "In Progress").append("\n\n");

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        summary.append("Due Date: ").append(sdf.format(new Date(task.getDueDate())));

        taskSummary.setText(summary.toString());

        StringBuilder metrics = new StringBuilder();
        metrics.append("Performance Metrics\n\n");

        long timeRemaining = task.getDueDate() - System.currentTimeMillis();
        int daysRemaining = (int) (timeRemaining / (24 * 60 * 60 * 1000));

        metrics.append("Days Remaining: ").append(Math.max(0, daysRemaining)).append("\n");
        metrics.append("Complexity Score: ").append(task.getDescription().length() / 10 + task.getPriority()).append("\n");
        metrics.append("Completion Rate: ").append(task.isCompleted() ? "100%" : "0%").append("\n");

        performanceMetrics.setText(metrics.toString());

        StringBuilder risk = new StringBuilder();
        risk.append("Risk Assessment\n\n");

        if (task.getPriority() >= 3 && !task.isCompleted()) {
            risk.append("⚠️ HIGH PRIORITY TASK PENDING\n");
        }

        if (daysRemaining < 0) {
            risk.append("🔴 TASK OVERDUE\n");
        } else if (daysRemaining <= 2) {
            risk.append("🟡 TASK DUE SOON\n");
        } else {
            risk.append("🟢 TASK ON TRACK\n");
        }

        if (task.getAssignee().isEmpty()) {
            risk.append("⚠️ NO ASSIGNEE SET\n");
        }

        risk.append("\nRecommendation: ");
        if (task.getPriority() >= 3 && !task.isCompleted()) {
            risk.append("Focus on this high-priority task immediately.");
        } else if (task.isCompleted()) {
            risk.append("Task completed successfully!");
        } else {
            risk.append("Continue working at steady pace.");
        }

        riskAssessment.setText(risk.toString());
    }
}
