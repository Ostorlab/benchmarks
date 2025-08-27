package com.example.myapplication1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TextView taskCountText;
    private int nextTaskId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        createMockTasks();
        updateTaskCount();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        taskCountText = findViewById(R.id.taskCountText);
        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);
        
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void setupRecyclerView() {
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
    }

    private void createMockTasks() {
        taskList.add(new Task(nextTaskId++, "Complete project proposal", "Write and submit the quarterly project proposal document", "HIGH"));
        taskList.add(new Task(nextTaskId++, "Review code changes", "Review pull requests from team members and provide feedback", "MEDIUM"));
        taskList.add(new Task(nextTaskId++, "Update documentation", "Update API documentation with recent changes", "LOW"));
        taskList.add(new Task(nextTaskId++, "Team meeting preparation", "Prepare slides and agenda for next week's team meeting", "MEDIUM"));
        taskList.add(new Task(nextTaskId++, "Bug fixes", "Fix the login authentication issue reported by QA", "HIGH"));
        taskList.add(new Task(nextTaskId++, "Database optimization", "Optimize slow database queries in the analytics module", "MEDIUM"));
        taskList.add(new Task(nextTaskId++, "Unit tests", "Write unit tests for the new payment processing feature", "LOW"));
        taskList.add(new Task(nextTaskId++, "Security audit", "Conduct security review of the user authentication system", "HIGH"));
        
        taskAdapter.updateTasks(taskList);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        EditText titleInput = new EditText(this);
        titleInput.setHint("Task title");
        layout.addView(titleInput);

        EditText descriptionInput = new EditText(this);
        descriptionInput.setHint("Task description");
        layout.addView(descriptionInput);

        Spinner prioritySpinner = new Spinner(this);
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, new String[]{"LOW", "MEDIUM", "HIGH"});
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);
        layout.addView(prioritySpinner);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String priority = prioritySpinner.getSelectedItem().toString();

            if (!title.isEmpty()) {
                Task newTask = new Task(nextTaskId++, title, description, priority);
                taskList.add(0, newTask);
                taskAdapter.updateTasks(taskList);
                updateTaskCount();
                Toast.makeText(this, "Task added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onTaskCompleted(Task task, boolean isCompleted) {
        updateTaskCount();
        String message = isCompleted ? "Task marked as completed!" : "Task marked as pending!";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskDeleted(Task task) {
        taskList.remove(task);
        taskAdapter.updateTasks(taskList);
        updateTaskCount();
        Toast.makeText(this, "Task deleted!", Toast.LENGTH_SHORT).show();
    }

    private void updateTaskCount() {
        int pendingTasks = 0;
        for (Task task : taskList) {
            if (!task.isCompleted()) {
                pendingTasks++;
            }
        }
        String countText = pendingTasks == 1 ? 
            "You have 1 task pending" : 
            "You have " + pendingTasks + " tasks pending";
        taskCountText.setText(countText);
    }
}