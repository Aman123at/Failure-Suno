package com.application.failuresuno;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginEActivity extends AppCompatActivity {

  EditText username, password;
    String email;
    Button btn_login;
    ProgressDialog progressDialog;


    FirebaseAuth auth;
    TextView forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_e);


        auth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        forgot_password = findViewById(R.id.forgot_password);

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginEActivity.this, RegisterActivity.class);

                startActivity(intent);
            }
        });
        getWindow().setStatusBarColor(getResources().getColor(R.color.gray));

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginEActivity.this, ResetPasswordActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                progressDialog=new ProgressDialog(LoginEActivity.this);
                progressDialog.setMessage("Please Wait.....");
                progressDialog.show();
                progressDialog.setCancelable(false);

                if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(LoginEActivity.this, "All fields are required..!!", Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                } else {

                    auth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        if(auth.getCurrentUser().isEmailVerified())
                                        {
                                            progressDialog.dismiss();

                                            Intent intent = new Intent(LoginEActivity.this, HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(LoginEActivity.this, "Please Verify Your Email ID", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }

                                    } else {
                                        Toast.makeText(LoginEActivity.this, "Authentication failed..!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });



                }
            }
        });
    }
}
