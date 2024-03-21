package com.example.thriftyskedrework;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.thriftyskedrework.databinding.ActivityScheduleBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity implements OnDialogCloseListener{

    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private FirebaseFirestore firestore;
    private ScheduleAdapter adapter;
    private List<ScheduleModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;


    ActivityScheduleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.scheduleBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ScheduleActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();

            }
        });

        recyclerView=findViewById(R.id.recyclerView);
        mFab = findViewById(R.id.floatingActionButton2);

        firestore = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ScheduleActivity.this));

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(),AddNewTask.TAG);

            }
        });

        mList = new ArrayList<>();
        adapter = new ScheduleAdapter(ScheduleActivity.this,mList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
        showData();
    }
    private void showData(){
        query =  firestore.collection("task").orderBy("time", Query.Direction.DESCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        String id = documentChange.getDocument().getId();
                        ScheduleModel scheduleModel = documentChange.getDocument().toObject(ScheduleModel.class).withId(id);

                        mList.add(scheduleModel);
                        adapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();


            }
        });

    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}