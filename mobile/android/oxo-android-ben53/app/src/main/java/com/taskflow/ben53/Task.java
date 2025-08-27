package com.taskflow.ben53;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    private String title;
    private int priority;
    private String description;
    private boolean isCompleted;
    private long dueDate;
    private String assignee;

    public Task(String title, int priority, String description, boolean isCompleted, long dueDate, String assignee) {
        this.title = title;
        this.priority = priority;
        this.description = description;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.assignee = assignee;
    }

    protected Task(Parcel in) {
        title = in.readString();
        description = in.readString();
        priority = in.readInt();
        dueDate = in.readLong();
        assignee = in.readString();
        isCompleted = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(priority);
        dest.writeString(description);
        dest.writeByte((byte) (isCompleted ? 1 : 0));
        dest.writeLong(dueDate);
        dest.writeString(assignee);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }

    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
}
