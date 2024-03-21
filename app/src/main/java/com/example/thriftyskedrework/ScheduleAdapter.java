package com.example.thriftyskedrework;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {

    private List<ScheduleModel> scheduleList;
    private ScheduleActivity activity;
    private FirebaseFirestore firestore;

    public ScheduleAdapter(ScheduleActivity scheduleActivity, List<ScheduleModel> scheduleList){
        this.scheduleList = scheduleList;
        activity=scheduleActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.each_task,parent,false);
        firestore =FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    public void deleteTask(int position){
        ScheduleModel scheduleModel = scheduleList.get(position);
        firestore.collection("task").document(scheduleModel.ScheduleTask).delete();
        scheduleList.remove(position);
        notifyItemRemoved(position);
    }
    public Context getContext(){
        return activity;
    }

    public void editTask(int position){
        ScheduleModel scheduleModel = scheduleList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("task" , scheduleModel.getTask());
        bundle.putString("due" , scheduleModel.getDue());
        bundle.putString("id" , scheduleModel.ScheduleTask);

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager() , addNewTask.getTag());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ScheduleModel scheduleModel=scheduleList.get(position);
        holder.mCheckBox.setText(scheduleModel.getTask());
        holder.mDueDateTV.setText("Due On: "+ scheduleModel.getDue());

        holder.mCheckBox.setChecked(toBoolean(scheduleModel.getStatus()));

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    firestore.collection("task").document(scheduleModel.ScheduleTask).update("status",1);
                }else {
                    firestore.collection("task").document(scheduleModel.ScheduleTask).update("status",0);
                }
            }
        });

    }

    private boolean toBoolean(int status){
        return status !=0;
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mDueDateTV;
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mDueDateTV = itemView.findViewById(R.id.dueDateTV);
            mCheckBox = itemView.findViewById(R.id.mCheckBoxTick);
        }
    }
}
