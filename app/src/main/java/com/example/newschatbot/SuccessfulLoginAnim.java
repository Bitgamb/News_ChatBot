package com.example.newschatbot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


public class SuccessfulLoginAnim extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_successful_login_anim);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Chat.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, 4000);

    }
}