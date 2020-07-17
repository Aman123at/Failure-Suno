package com.application.failuresuno;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {
    CircleImageView imageView;
 TextView name,mobile,profession,bio;
String userid;
    Api api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        imageView = findViewById(R.id.imageview);
        name = findViewById(R.id.name);

        profession = findViewById(R.id.profession);
        bio = findViewById(R.id.bio);
        api=new Api();
        userid=getIntent().getStringExtra("userid");
        getProfile(userid);


        }

    @Override
    protected void onStart() {
        super.onStart();


}
public void  getProfile(final String userid)
{
    final ProgressDialog progressDialog=new ProgressDialog(ViewProfileActivity.this);
    progressDialog.setMessage("Fetching...");
    progressDialog.show();
    StringRequest request=new StringRequest(Request.Method.POST, api.getprofile, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //Toast.makeText(ProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();
            try {
                JSONObject jsonObject=new JSONObject(response);
                Gson gson = new Gson();

                String success =jsonObject.getString("success");
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                Log.e("state",success);

                if(success.equals("1")){
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject object=jsonArray.getJSONObject(i);
                        name.setText(object.getString("name"));

                        profession.setText(object.getString("prof"));
                        bio.setText(object.getString("bio"));
                        Glide.with(ViewProfileActivity.this).load("https://failuresuno.com/"+object.getString("image")).into(imageView);





                    }

                }


            }catch (JSONException e)
            {
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(ViewProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }


    ){

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {


            Map<String,String>params=new HashMap<>();
            params.put("userid",userid);

            // params.put("cnumber",cnumber.getText().toString());


            return params;
        }
    };

    RequestQueue requestQueue= Volley.newRequestQueue(ViewProfileActivity.this);
    requestQueue.add(request);

}
}



