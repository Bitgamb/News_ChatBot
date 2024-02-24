package com.example.newschatbot;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class FakeorRealActivity2 extends AppCompatActivity {
    public float[] data = new float[500];
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ImageView detect;
    private TextView detecttext;
    private ImageButton help;
    Map<String, String> map = new HashMap();
    Map<String, Integer> map2 = new HashMap();
    public int maxlen = 500;
    Hashtable<String, String> my_dict = new Hashtable();
    private EditText newstext;
    ArrayList<String> numberlist = new ArrayList();
    private String path;
    public String[] separated;
    private String stringToPython;
    public Interpreter tflite;
    private ImageView whatsapp;
    ImageView viewHistory;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_fakeor_real2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        viewHistory = findViewById(R.id.view_history);
        if (!isInternetAvailable()) {
            Intent intent = new Intent(getApplicationContext(), FakeorRealActivity2NoInternet.class);
            startActivity(intent);
            finish();
        }





        DatabaseReference historyRef = mDatabase.child("users").child(uid).child("news_detect_input_history");
        DatabaseReference userRef = mDatabase.child("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("orderId").exists()) {
                    String orderId = snapshot.child("orderId").getValue().toString();
                    if (orderId.isEmpty()) {
                        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() > 10) {
                                    // If the number of child nodes is greater than 10, navigate to MainActivity
                                    Intent intent = new Intent(getApplicationContext(), TrialExpired.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle onCancelled if needed
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });



        viewHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), DetectionHistory.class);
                startActivity(intent);
                finish();
            }
        });

        this.newstext = (EditText) findViewById(R.id.editTextTextMultiLine);
        this.detect = (ImageView) findViewById(R.id.imageButton_detect);

        this.whatsapp = (ImageView) findViewById(R.id.imageView);



        this.detect.setVisibility(View.INVISIBLE);
        try {
            this.tflite = new Interpreter(loadModelFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.detect.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                // Get the text from the EditText
                String editTextValue = FakeorRealActivity2.this.newstext.getText().toString();



                String fake = "Fake : "+ editTextValue ;
                String real = "Real : "+ editTextValue ;

                // Set the value of the EditText in the generated key

                if (!FakeorRealActivity2.this.newstext.getText().toString().isEmpty()) {
                    FakeorRealActivity2 fakeorRealActivity2 = FakeorRealActivity2.this;
                    fakeorRealActivity2.stringToPython = fakeorRealActivity2.newstext.getText().toString();
                    fakeorRealActivity2 = FakeorRealActivity2.this;
                    if (((double) fakeorRealActivity2.doInference(fakeorRealActivity2.stringToPython)) > 0.5d) {
                        //fake news
                        DatabaseReference inputHistoryRef = mDatabase.child("users").child(uid).child("news_detect_input_history").push();
                        inputHistoryRef.setValue(fake);
                        Intent intent = new Intent(getApplicationContext(), DatabaseCheckingAnimFake.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //real news
                        DatabaseReference inputHistoryRef = mDatabase.child("users").child(uid).child("news_detect_input_history").push();
                        inputHistoryRef.setValue(real);
                        Intent intent = new Intent(getApplicationContext(), DatabaseCheckingAnimReal.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void onStart() {
        super.onStart();
        readcsvraw();
        this.detect.setVisibility(View.VISIBLE);
    }

    private void readcsvraw() {
        String str = "ReadCSVraw";
        Log.d(str, "Reaching here");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.tokenizer_500), StandardCharsets.UTF_8));
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split(",");
                this.map2.put(split[0], Integer.valueOf(Integer.parseInt(split[1])));
                System.out.println(this.map2.get(split[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(str, String.valueOf(this.map2.get("african")));
    }

    private void splittextandint(String str) {
        System.out.println(str.replaceAll("[^0-9]", ""));
    }

    private float doInference(String str) {
        int i;
        Class cls = float.class;
        String[] split = this.newstext.getText().toString().toLowerCase().split("[^\\D]+");
        System.out.println(Arrays.toString(split));
        String str2 = "DoInference";
        Log.d(str2, "Reaching here");
        System.out.println(split.length);
        System.out.println(split[0]);
        split = split[0].split("\\s+");
        for (i = 0; i < split.length; i++) {
            split[i] = split[i].replaceAll("[^\\w]", "");
        }
        System.out.println(split.length);
        for (i = 0; i < this.maxlen; i++) {
            if (i >= split.length) {
                this.data[i] = 0.0f;
            } else if (this.map2.get(split[i]) != null) {
                this.data[i] = (float) Integer.parseInt(String.valueOf(this.map2.get(split[i])));
                Log.d(str2, "Reaching here in IF");
                System.out.println(split[i]);
            } else {
                this.data[i] = 0.0f;
            }
        }
        System.out.println(Arrays.toString(this.data));
        float[][] fArr = new float[1][this.maxlen];
        for (i = 0; i < this.maxlen; i++) {
            fArr[0][i] = this.data[i];
            PrintStream printStream = System.out;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("itemTF = ");
            stringBuilder.append(fArr[0][i]);
            printStream.println(stringBuilder.toString());
        }
        float[][] fArr2 = (float[][]) Array.newInstance(cls, new int[]{1, 1});
        this.tflite.run(fArr, fArr2);
        return fArr2[0][0];
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor openFd = getAssets().openFd("model_whatsapp_fakenews_500.tflite");
        return new FileInputStream(openFd.getFileDescriptor()).getChannel().map(MapMode.READ_ONLY, openFd.getStartOffset(), openFd.getDeclaredLength());
    }
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Chat.class);
        startActivity(intent);
        finish();

    }
}
