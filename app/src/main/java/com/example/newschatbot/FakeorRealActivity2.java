package com.example.newschatbot;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_fakeor_real2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        this.newstext = (EditText) findViewById(R.id.editTextTextMultiLine);
        this.detect = (ImageView) findViewById(R.id.imageButton_detect);
        this.detecttext = (TextView) findViewById(R.id.textView_detect);
        this.whatsapp = (ImageView) findViewById(R.id.imageView);

        this.detect.setVisibility(View.INVISIBLE);
        try {
            this.tflite = new Interpreter(loadModelFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.detect.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!FakeorRealActivity2.this.newstext.getText().toString().isEmpty()) {
                    FakeorRealActivity2 fakeorRealActivity2 = FakeorRealActivity2.this;
                    fakeorRealActivity2.stringToPython = fakeorRealActivity2.newstext.getText().toString();
                    fakeorRealActivity2 = FakeorRealActivity2.this;
                    if (((double) fakeorRealActivity2.doInference(fakeorRealActivity2.stringToPython)) > 0.5d) {
                        //fake news
                        Intent intent = new Intent(getApplicationContext(), DatabaseCheckingAnimFake.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //real news
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Chat.class);
        startActivity(intent);
        finish();

    }
}
