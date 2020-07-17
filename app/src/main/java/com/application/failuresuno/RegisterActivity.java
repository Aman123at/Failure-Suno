package com.application.failuresuno;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

   EditText cnfrmp, email, password;
    Button btn_register;
    RelativeLayout main;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    ProgressDialog progressDialog;


    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setStatusBarColor(getResources().getColor(R.color.gray));




        main=findViewById(R.id.mainlayout);





        email = findViewById(R.id.email);
        cnfrmp = findViewById(R.id.confirmpassword);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginEActivity.class);

                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog=new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Registering....");
                progressDialog.show();
                progressDialog.setCancelable(false);

                final String txt_email = email.getText().toString();
                final String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)||TextUtils.isEmpty(cnfrmp.getText().toString())){

                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (txt_password.length() < 6 ){
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else if (!txt_password.equals(cnfrmp.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Password don't matched", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else {

                    register(txt_email, txt_password);

                }
            }
        });
    }

    private void register(final String email, String password){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){


                            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(RegisterActivity.this, "Check Mail To Verify Email", Toast.LENGTH_SHORT).show();

Intent intent=new Intent(RegisterActivity.this,LoginEActivity.class);
startActivity(intent);

                                }
                            });

                        } else {
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
