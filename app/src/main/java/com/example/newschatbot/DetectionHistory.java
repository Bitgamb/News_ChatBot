package com.example.newschatbot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetectionHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsInputHistoryAdapter adapter;
    private List<NewsInputHistory> historyList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String uid = user.getUid();
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_history);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        back = findViewById(R.id.back);

        historyList = new ArrayList<>();
        adapter = new NewsInputHistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FakeorRealActivity2.class);
                startActivity(intent);
                finish();
            }
        });

        // Fetch data from Firebase and update the RecyclerView
        fetchDataFromFirebase();

    }

    private void fetchDataFromFirebase() {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("users/" + uid + "/news_detect_input_history");

        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int serialNumber = 1; // Initial serial number

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    String value = childSnapshot.getValue(String.class);

                    NewsInputHistory historyItem = new NewsInputHistory(key, value, serialNumber);
                    historyList.add(historyItem);

                    serialNumber++; // Increment serial number for the next item
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FakeorRealActivity2.class);
        startActivity(intent);
        finish();
    }
}
