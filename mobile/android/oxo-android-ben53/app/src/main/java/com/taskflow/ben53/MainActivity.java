package com.taskflow.ben53;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText titleInput;
    private EditText descriptionInput;
    private EditText assigneeInput;
    private Spinner prioritySpinner;
    private Button createTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        assigneeInput = findViewById(R.id.assigneeInput);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        createTaskButton = findViewById(R.id.createTaskButton);

        setupPrioritySpinner();

        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });
    }

    private void setupPrioritySpinner() {
        String[] priorities = {"Low Priority", "Medium Priority", "High Priority", "Critical"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
    }

    private void createTask() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String assignee = assigneeInput.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show();
            return;
        }

        int priority = prioritySpinner.getSelectedItemPosition() + 1;
        long dueDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L);
        boolean isCompleted = false;

        Task task = new Task(title, priority, description, isCompleted, dueDate, assignee);

        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("task", task);
        startActivity(intent);
    }
}
