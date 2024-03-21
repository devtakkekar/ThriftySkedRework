package com.example.thriftyskedrework;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Exclude;

public class ScheduleTaskId {

    @Exclude
    public String ScheduleTask;
    public <T extends ScheduleTaskId> T withId(@NonNull final String id) {
        this.ScheduleTask = id;
        return (T) this;
    }
}
