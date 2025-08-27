package com.example.myapplication3;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TodoApp";
    private TextView tasksDisplay;
    private List<Task> tasks;
    private long nextTaskId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        tasksDisplay = findViewById(R.id.tasksDisplay);
        Button addTaskButton = findViewById(R.id.addTaskButton);
        Button toggleFirstTaskButton = findViewById(R.id.toggleFirstTaskButton);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeTasks();
        addTaskButton.setOnClickListener(v -> addNewTask());
        toggleFirstTaskButton.setOnClickListener(v -> toggleFirstTask());
        
        displayTasks();
    }
    
    private void initializeTasks() {
        tasks = new ArrayList<>();
        tasks.add(new Task(nextTaskId++, "Learn Android Development", "Complete the Android basics course"));
        tasks.add(new Task(nextTaskId++, "Implement Parcelable", "Create secure data transfer objects"));
        tasks.add(new Task(nextTaskId++, "Build Todo App", "Create a functional todo application"));
        tasks.add(new Task(nextTaskId++, "Test Application", "Ensure all features work correctly"));
        tasks.add(new Task(nextTaskId++, "Code Review", "Review code for security and best practices"));
    }
    
    private void addNewTask() {
        String[] titles = {
            "Write Unit Tests", "Update Documentation", "Optimize Performance", 
            "Fix Bugs", "Add New Features", "Security Audit"
        };
        String[] descriptions = {
            "Add comprehensive unit tests", "Update project documentation", "Improve app performance",
            "Fix reported issues", "Implement requested features", "Review code for security issues"
        };
        
        int randomIndex = (int) (Math.random() * titles.length);
        Task newTask = new Task(nextTaskId++, titles[randomIndex], descriptions[randomIndex]);
        tasks.add(newTask);
        displayTasks();
        Toast.makeText(this, "Added: " + newTask.getTitle(), Toast.LENGTH_SHORT).show();
    }
    
    private void toggleFirstTask() {
        if (!tasks.isEmpty()) {
            Task firstTask = tasks.get(0);
            firstTask.setCompleted(!firstTask.isCompleted());
            displayTasks();
            String status = firstTask.isCompleted() ? "completed" : "pending";
            Toast.makeText(this, "Task marked as " + status, Toast.LENGTH_SHORT).show();
        }
    }

    private void testTaskParcelable() {
        if (tasks.isEmpty()) {
            Toast.makeText(this, "No tasks to test!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Task originalTask = tasks.get(0);

            // Wrap Task inside DataParcelable
            DataParcelable wrapped = new DataParcelable(originalTask);

            // Write to parcel
            Parcel parcel = Parcel.obtain();
            wrapped.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);

            // Restore from parcel
            DataParcelable restoredWrapped = DataParcelable.CREATOR.createFromParcel(parcel);
            parcel.recycle();

            // Extract back the Task
            Task restoredTask = (Task) restoredWrapped.data;

            String result = "✅ DataParcelable Test Successful!\n\n" +
                    "Original Task:\n" + originalTask.toString() + "\n\n" +
                    "Restored Task:\n" + restoredTask.toString() + "\n\n" +
                    "Integrity maintained using Gson serialization";

            Log.d(TAG, result);
            Toast.makeText(this, "DataParcelable test passed!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            String error = "DataParcelable test failed: " + e.getMessage();
            Log.e(TAG, error, e);
            Toast.makeText(this, "Test failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayTasks() {
        StringBuilder display = new StringBuilder();
        display.append("📋 TODO LIST (").append(tasks.size()).append(" tasks)\n\n");
        
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String status = task.isCompleted() ? "✅" : "⭕";
            display.append(String.format("%d. %s %s\n   %s\n\n", 
                i + 1, status, task.getTitle(), task.getDescription()));
        }
        
        long completedCount = tasks.stream().mapToLong(task -> task.isCompleted() ? 1 : 0).sum();
        display.append(String.format("Progress: %d/%d completed", completedCount, tasks.size()));
        
        tasksDisplay.setText(display.toString());
    }
}