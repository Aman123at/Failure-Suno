package com.application.failuresuno;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ReadStory extends AppCompatActivity {
    TextView title,data;
    String category;
    ImageView imageView;
    String text1,text2,text3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_story);

        text1=getIntent().getStringExtra("title");

        text2=getIntent().getStringExtra("story");
        text3=getIntent().getStringExtra("image");
        category=getIntent().getStringExtra("category");
        title=findViewById(R.id.title);
        data=findViewById(R.id.data);
        imageView=findViewById(R.id.imageview);

            title.setText(text1);
            data.setText(text2);
            // Toast.makeText(this, ""+text2, Toast.LENGTH_SHORT).show();
            Glide.with(ReadStory.this).load("https://failuresuno.com/" + text3).into(imageView);


    }
}
