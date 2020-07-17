package com.application.failuresuno;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.application.failuresuno.HomeActivity;
import com.application.failuresuno.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  imageView.startAnimation(medalAnimation);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent=new Intent(MainActivity.this, HomeActivity.class);

                startActivity(intent);
                finish();
            }
        }, 2000);

    }
}
