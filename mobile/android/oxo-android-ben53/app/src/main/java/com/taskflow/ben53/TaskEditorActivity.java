package com.taskflow.ben53;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TaskEditorActivity extends AppCompatActivity {
    private EditText titleEdit;
    private EditText descriptionEdit;
    private EditText assigneeEdit;
    private Spinner prioritySpinner;
    private CheckBox completedCheckbox;
    private Button saveButton;
    private Button backButton;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor);

        titleEdit = findViewById(R.id.titleEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        assigneeEdit = findViewById(R.id.assigneeEdit);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        completedCheckbox = findViewById(R.id.completedCheckbox);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        setupPrioritySpinner();

        task = getIntent().getParcelableExtra("task");

        if (task != null) {
            populateFields();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupPrioritySpinner() {
        String[] priorities = {"Low Priority", "Medium Priority", "High Priority", "Critical"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
    }

    private void populateFields() {
        titleEdit.setText(task.getTitle());
        descriptionEdit.setText(task.getDescription());
        assigneeEdit.setText(task.getAssignee());

        int priority = task.getPriority();
        if (priority >= 1 && priority <= 4) {
            prioritySpinner.setSelection(priority - 1);
        }

        completedCheckbox.setChecked(task.isCompleted());
    }

    private void saveChanges() {
        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();
        String assignee = assigneeEdit.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show();
            return;
        }

        task.setTitle(title);
        task.setDescription(description);
        task.setAssignee(assignee);
        task.setPriority(prioritySpinner.getSelectedItemPosition() + 1);
        task.setCompleted(completedCheckbox.isChecked());

        Intent resultIntent = new Intent(this, TaskDetailActivity.class);
        resultIntent.putExtra("task", task);
        startActivity(resultIntent);
        finish();
    }
}
