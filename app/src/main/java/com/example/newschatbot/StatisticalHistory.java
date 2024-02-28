package com.example.newschatbot;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class StatisticalHistory extends AppCompatActivity {

    private DatabaseReference botChatRef;
    private DatabaseReference openCountRef;
    private DatabaseReference newsHistoryRef;
    private TextView botChatPercentageTextView;
    private TextView openCountPercentageTextView;
    private TextView newsHistoryPercentageTextView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistical_history);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Chat.class);
                startActivity(intent);
                finish();
            }
        });

        // Get the PieChart view
        PieChart pieChart = findViewById(R.id.pieChart);

        // Get TextViews for displaying percentages
        botChatPercentageTextView = findViewById(R.id.ChatBot);
        openCountPercentageTextView = findViewById(R.id.news);
        newsHistoryPercentageTextView = findViewById(R.id.news_detect);

        // Initialize Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            botChatRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("bot_chat");
            openCountRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("open_count");
            newsHistoryRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("news_detect_input_history");

            // Fetch counts from Firebase and update the PieChart and TextViews
            fetchCountsAndUpdateChart(pieChart);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCountsAndUpdateChart(PieChart pieChart) {
        botChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long botChatCount = dataSnapshot.getChildrenCount();
                // Calculate percentage and add the botChatCount to the PieChart
                double botChatPercentage = calculatePercentage(botChatCount);
                pieChart.addPieSlice(new PieModel("Bot Chat Count", (float) botChatPercentage, getResources().getColor(R.color.red)));

                // Update TextView
                botChatPercentageTextView.setText("ChatBot: "+String.format("%.2f%%", botChatPercentage));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StatisticalHistory.this, "Failed to fetch bot chat count", Toast.LENGTH_SHORT).show();
            }
        });

        openCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long openCount = dataSnapshot.getValue(Long.class);
                    // Calculate percentage and add the openCount to the PieChart
                    double openCountPercentage = calculatePercentage(openCount);
                    pieChart.addPieSlice(new PieModel("Open Count", (float) openCountPercentage, getResources().getColor(R.color.green)));

                    // Update TextView
                    openCountPercentageTextView.setText("News Feed: "+String.format("%.2f%%", openCountPercentage));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StatisticalHistory.this, "Failed to fetch open count", Toast.LENGTH_SHORT).show();
            }
        });

        newsHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long newsHistoryCount = dataSnapshot.getChildrenCount();
                // Calculate percentage and add the newsHistoryCount to the PieChart
                double newsHistoryPercentage = calculatePercentage(newsHistoryCount);
                pieChart.addPieSlice(new PieModel("News History Count", (float) newsHistoryPercentage, getResources().getColor(R.color.bluesplash)));

                // Update TextView
                newsHistoryPercentageTextView.setText("Fake News Detection: "+String.format("%.2f%%", newsHistoryPercentage));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StatisticalHistory.this, "Failed to fetch news history count", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double calculatePercentage(long count) {
        // Assuming the total count is 100 for percentage calculation
        // You can adjust this value based on your requirements
        double totalCount = 100.0;
        return (count / totalCount) * 100.0;
    }
}
