package com.example.newschatbot;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newschatbot.Model.Articles;
import com.example.newschatbot.Model.Headlines;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsDisplay extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText etQuery;
    Button btnSearch, btnAboutUs;
    Dialog dialog;
    final String API_KEY = "9290f2828b9f415289a6239ccb654f9f";
    Adapter adapter;
    private AdView mAdView;
    List<Articles> articles = new ArrayList<>();
    private DatabaseReference userDatabase;
    private String uid;
    private String currentCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_display);

        swipeRefreshLayout = findViewById(R.id.srl);
        recyclerView = findViewById(R.id.recyclerView);
        etQuery = findViewById(R.id.etQuery);
        btnSearch = findViewById(R.id.btnSearch);
        btnAboutUs = findViewById(R.id.aboutUs);
        dialog = new Dialog(NewsDisplay.this);
        getCategoryNews("", "business");

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String orderId = dataSnapshot.child("orderId").getValue(String.class);
                    if (orderId != null && !orderId.isEmpty()) {
                        mAdView.setVisibility(View.GONE);
                    } else {
                        mAdView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Set the default category to "business" and display news
        getCategoryNews("", "business");

        final String country = getCountry();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            retrieveJson("", country, API_KEY);
        });

        retrieveJson("", country, API_KEY);

        btnSearch.setOnClickListener(v -> {
            if (!etQuery.getText().toString().equals("")) {
                swipeRefreshLayout.setOnRefreshListener(() -> {
                    retrieveJson(etQuery.getText().toString(), currentCategory, API_KEY);
                });
                retrieveJson(etQuery.getText().toString(), currentCategory, API_KEY);
            } else {
                swipeRefreshLayout.setOnRefreshListener(() -> {
                    retrieveJson("", currentCategory, API_KEY);
                });
                retrieveJson("", currentCategory, API_KEY);
            }
        });

        btnAboutUs.setOnClickListener(v -> showDialog());
    }

    public void categoryClicked(View view) {
        String category = view.getTag().toString();


        if (!etQuery.getText().toString().equals("") && !category.equals(currentCategory)) {
            getCategoryNews(etQuery.getText().toString(), category);
        } else if (!category.equals(currentCategory)) {
            getCategoryNews("", category);
        }

        currentCategory = category;
    }

    private void getCategoryNews(String query, String category) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            retrieveJson(query, category, API_KEY);
        });
        retrieveJson(query, category, API_KEY);
    }


    public void retrieveJson(String query, String category, String apiKey) {
        swipeRefreshLayout.setRefreshing(true);
        String country = getCountry();

        Call<Headlines> call;
        if (!query.equals("")) {
            call = ApiClient.getInstance().getApi().getSpecificData(query, apiKey);
        } else {
            call = ApiClient.getInstance().getApi().getCategoryNews(country, category, apiKey);
        }

        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Articles> newArticles = response.body().getArticles();
                    updateRecyclerView(newArticles);
                } else {
                    Toast.makeText(NewsDisplay.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(NewsDisplay.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerView(List<Articles> newArticles) {
        articles.clear();
        articles.addAll(newArticles);

        if (adapter == null) {
            adapter = new Adapter(NewsDisplay.this, articles);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }




    public String getCountry() {
        return "in"; // "in" is the country code for India
    }

    public void showDialog() {
        Button btnClose;
        dialog.setContentView(R.layout.about_us_pop_up);
        dialog.show();
        btnClose = dialog.findViewById(R.id.close);

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Chat.class);
        startActivity(intent);
        finish();
    }
}
