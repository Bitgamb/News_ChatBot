package com.example.newschatbot;


import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail,editTextPassword;
    ImageView buttonLogin,otpBtn,textView;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView emailSyntax,passwordSyntax,forgotPass;

    private boolean isPasswordVisible = false;
    private TextInputEditText passwordEditText;




    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://news-chatbot-15410-default-rtdb.firebaseio.com/");
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), NewsDisplay.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        editTextEmail=findViewById(R.id.email);
        editTextPassword= findViewById(R.id.password);
        progressBar=findViewById(R.id.progressBar);
        textView= findViewById(R.id.registernow);
        buttonLogin= findViewById(R.id.btn_login);
        passwordEditText = findViewById(R.id.password);

        forgotPass=findViewById(R.id.forgotpass);

        emailSyntax=findViewById(R.id.LoginActivityEmailError);
        passwordSyntax=findViewById(R.id.passwordsyntax);


        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
                finish();

            }
        });





        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String email, password;

                email= String.valueOf(editTextEmail.getText());
                password=String.valueOf(editTextPassword.getText());
                emailSyntax.setText("");
                passwordSyntax.setText("");



                //email validation
                if(TextUtils.isEmpty(email)){

                    Toast.makeText(Login.this,"Enter Email",Toast.LENGTH_SHORT).show();
                    emailSyntax.setText("Enter Email Address");
                    progressBar.setVisibility(View.GONE);
                    return;

                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    Toast.makeText(Login.this,"Enter Valid Email Address",Toast.LENGTH_SHORT).show();
                    emailSyntax.setText("Enter Valid Email Address");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //password validation
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this,"Enter Password",Toast.LENGTH_SHORT).show();
                    passwordSyntax.setText("Enter Password");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                boolean hasSymbol = false;
                boolean hasUppercase = false;
                boolean hasLowercase = false;
                boolean hasNumber = false;
                boolean hasMinimumLength = false;

                String symbols = "!@#$%^&*()_-+=<>?";
                int minimumLength = 8;
                for (int i = 0; i < password.length(); i++) {
                    char ch = password.charAt(i);

                    if (symbols.contains(String.valueOf(ch))) {
                        hasSymbol = true;
                    } else if (Character.isUpperCase(ch)) {
                        hasUppercase = true;
                    } else if (Character.isLowerCase(ch)) {
                        hasLowercase = true;
                    } else if (Character.isDigit(ch)) {
                        hasNumber = true;
                    }
                }

                if (password.length() >= minimumLength) {
                    hasMinimumLength = true;
                }

                // Check if the password meets the requirements
                if (hasSymbol && hasUppercase && hasLowercase && hasNumber && hasMinimumLength) {
                    Toast.makeText(Login.this,"Password Meets the requirements",Toast.LENGTH_SHORT).show();





                }
                //special symbol syntax error display
                else {
                    Toast.makeText(Login.this,"Password should have uppercase,lowercase,symbol and numbers!",Toast.LENGTH_SHORT).show();
                    passwordSyntax.setText("Password should have uppercase,lowercase,symbol and numbers!");
                    return;
                }




                //firebase authentication
                mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Login Successfull !", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), SuccessfulLoginAnim.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Toast.makeText(Login.this, "Login failed !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

        });

    }

    public void togglePasswordVisibility(View view) {
        isPasswordVisible = !isPasswordVisible;

        // Change the input type based on visibility state
        if (isPasswordVisible) {
            passwordEditText.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
        } else {
            passwordEditText.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            );
        }

        // Move the cursor to the end of the text for a better user experience
        passwordEditText.setSelection(passwordEditText.getText().length());
    }
}



