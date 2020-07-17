package com.application.failuresuno;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;

import com.bumptech.glide.Glide;

public class AboutusActivity extends AppCompatActivity {
    CircleImageView imageView1,imageView2,imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        imageView1=findViewById(R.id.image1);  imageView2=findViewById(R.id.image2);
        imageView3=findViewById(R.id.image3);

        Glide.with(AboutusActivity.this).load("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/Tarun%20kumar%2C%20founder%20%26%20CEO.jpg?alt=media&token=19801f48-de28-4693-8f0d-bb9b81da15fe").into(imageView1);
        Glide.with(AboutusActivity.this).load("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/WhatsApp%20Image%202020-07-10%20at%2008.09.39.jpeg?alt=media&token=751c2371-e1f4-4985-b524-6d04d2dfce80").into(imageView2);
        Glide.with(AboutusActivity.this).load("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/aman%20tiwari%20%2C%20technical%20member.jpg?alt=media&token=b23b21fd-036d-49ef-89fa-4365b4e8c180").into(imageView3);





    }
}
