package com.example.newschatbot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    TextInputEditText editTextEmail,editTextPassword,editTextConfirmPassword,editTextName,editTextPhone;
    ImageView buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView,confirmpasswordSyntax,emailSyntax,passwordSyntax,nameSyntax,phoneSyntax;




    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://news-chatbot-15410-default-rtdb.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mAuth= FirebaseAuth.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        setContentView(R.layout.activity_register);
//edit text field value fetching
        editTextEmail=findViewById(R.id.email);
        editTextConfirmPassword= findViewById(R.id.confirmPassword);
        editTextPassword= findViewById(R.id.password);
        editTextName=findViewById(R.id.textInputName);
        editTextPhone=findViewById(R.id.textInputPhoneReg);

        progressBar=findViewById(R.id.progressBar);
        textView= findViewById(R.id.loginnow);
        buttonReg= findViewById(R.id.button_register);


//ui red font error syntax for displaying textview
        confirmpasswordSyntax=findViewById(R.id.confirmpasswordsyntax);
        passwordSyntax=findViewById(R.id.passwordsyntax);
        emailSyntax=findViewById(R.id.email_syntax);
        nameSyntax=findViewById(R.id.namesyntax);
        phoneSyntax=findViewById(R.id.phonesyntax);



//login page redirect textview("already registered?login now")
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
//register button
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, confirmPassword,name,phone;

                email= String.valueOf(editTextEmail.getText());
                confirmPassword=String.valueOf(editTextConfirmPassword.getText());
                password=String.valueOf(editTextPassword.getText());
                name =String.valueOf(editTextName.getText());
                phone=String.valueOf(editTextPhone.getText());

                //to set the text values null again
                emailSyntax.setText("");
                passwordSyntax.setText("");
                confirmpasswordSyntax.setText("");
                nameSyntax.setText("");
                phoneSyntax.setText("");



                //name validation
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(Register.this,"Enter Name",Toast.LENGTH_SHORT).show();
                    nameSyntax.setText("Enter Name");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //email validation
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this,"Enter Email Address",Toast.LENGTH_SHORT).show();
                    emailSyntax.setText("Enter Email Address");
                    progressBar.setVisibility(View.GONE);
                    return;

                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    Toast.makeText(Register.this,"Enter Valid Email Address",Toast.LENGTH_SHORT).show();
                    emailSyntax.setText("Enter Valid Email Address");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                //phone validation
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(Register.this,"Enter Phone Number",Toast.LENGTH_SHORT).show();
                    phoneSyntax.setText("Enter Phone Number");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                //password validation
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this,"Enter Password",Toast.LENGTH_SHORT).show();
                    passwordSyntax.setText("Enter Password");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(Register.this,"Enter Confirm Password",Toast.LENGTH_SHORT).show();
                    confirmpasswordSyntax.setText("Enter Confirm Password");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if(!password.equals(confirmPassword)){
                    Toast.makeText(Register.this,"Passwords Do Not Match",Toast.LENGTH_SHORT).show();
                    confirmpasswordSyntax.setText("Passwords Do Not Match");
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
                    Toast.makeText(Register.this,"Password Meets the requirements",Toast.LENGTH_SHORT).show();
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(phone)){
                                Toast.makeText(Register.this,"Number Already Exists",Toast.LENGTH_SHORT).show();
                                phoneSyntax.setText("Number Already Exists");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                //special symbol syntax error display
                else {
                    Toast.makeText(Register.this,"Password should have uppercase,lowercase,symbol and numbers!",Toast.LENGTH_SHORT).show();
                    passwordSyntax.setText("Password should have uppercase,lowercase,symbol and numbers!");
                    return;
                                    }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    String uid = currentUser.getUid();
                                    databaseReference.child(uid).child("name").setValue(name);
                                    databaseReference.child(uid).child("email").setValue(email);
                                    databaseReference.child(uid).child("phone").setValue(phone);
                                    databaseReference.child(uid).child("password").setValue(password);
                                    databaseReference.child(uid).child("orderId").setValue("");

                                    Toast.makeText(Register.this, "Account Registered !", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();

                                } else {

                                    Toast.makeText(Register.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });









                }



        });




    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();

    }
}
