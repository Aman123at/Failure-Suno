package com.application.failuresuno;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

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
import android.widget.ImageView;
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
import com.google.firestore.admin.v1beta1.Progress;
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

public class ProfileActivity extends AppCompatActivity {
    CircleImageView imageView;
    EditText name,mobile,profession,bio;
    Button upload;
    Uri postimageuri;
    String encoded_image,encoded_image1;
    Bitmap bitmap;
    String cc="c";
    Api api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageView = findViewById(R.id.imageview);
        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobileno);
        profession = findViewById(R.id.profession);
        bio = findViewById(R.id.bio);
        upload = findViewById(R.id.update);
        api=new Api();
        getProfile();
FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
//mobile.setText(firebaseAuth.getCurrentUser().getPhoneNumber()+"");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(name.getText().toString()))
                {
                    // Toast.makeText(getActivity(), "Enter Username", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ProfileActivity.this, "Enter Your Name", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(profession.getText().toString()))
                {
                    Toast.makeText(ProfileActivity.this, "Enter Your Profession", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(mobile.getText().toString()))
                {
                    Toast.makeText(ProfileActivity.this, "Enter Your Mobile Number", Toast.LENGTH_SHORT).show();

                }
                else if(TextUtils.isEmpty(bio.getText().toString()))
                {
                    Toast.makeText(ProfileActivity.this, "Write  Your Bio", Toast.LENGTH_SHORT).show();

                }
                else if(cc.equals("c"))
                {
                    Toast.makeText(ProfileActivity.this, "Select Your Image", Toast.LENGTH_SHORT).show();

                }
                else {
                    //  Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                    final ProgressDialog progressDialog=new ProgressDialog(ProfileActivity.this);
                    progressDialog.setMessage("Uploading...");
                    progressDialog.show();
//
                    StringRequest request=new StringRequest(Request.Method.POST, api.addprofile, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(ProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();

                         Intent intent=new Intent(ProfileActivity.this,HomeActivity.class);
                         startActivity(intent);
                         finish();
                            progressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }


                    ){

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                            String string  = dateFormat.format(new Date());
                            final SharedPreferences prefs =ProfileActivity.this.getSharedPreferences("xy", Context.MODE_PRIVATE);

                            if(prefs.getString("encodeurl", null)!=null){
                                encoded_image1=prefs.getString("encodeurl",null);

                            }

                            Map<String,String>params=new HashMap<>();
                            params.put("userid",firebaseAuth.getCurrentUser().getUid());
                            params.put("name",name.getText().toString());
                            params.put("mobileno",mobile.getText().toString());
                            params.put("prof",profession.getText().toString());
                            params.put("bio",bio.getText().toString());

                            if(cc.equals("n"))
                            {
                                params.put("image",encoded_image1);
                            }
                            else
                            {
                                params.put("image","m");
                            }

                            // params.put("cnumber",cnumber.getText().toString());


                            return params;
                        }
                    };

                    RequestQueue requestQueue= Volley.newRequestQueue(ProfileActivity.this);
                    requestQueue.add(request);
                }

            }
        });



    }
        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                postimageuri = data.getData();
                cc="n";
                try {

                    bitmap= MediaStore.Images.Media.getBitmap(ProfileActivity.this.getContentResolver(),postimageuri);
                    imageView.setImageURI(postimageuri);
                    imagestore(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //  Bitmap bitmap = MediaStore.Images.Media.getBitmap( postimageuri);
                //  addimage.setImageBitmap(bitmap);


            }
        }

        public String imagestore(Bitmap bitmap) {

//        Toast.makeText(getActivity(), "Enter", Toast.LENGTH_SHORT).show();
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte[] imagebytes=stream.toByteArray();
            encoded_image= Base64.encodeToString(imagebytes, Base64.DEFAULT);
            SharedPreferences.Editor editor = ProfileActivity.this.getSharedPreferences("xy", Context.MODE_PRIVATE).edit();
            editor.putString("encodeurl",encoded_image);
            editor.apply();
            //  Toast.makeText(getContext(), "fdd"+encoded_image, Toast.LENGTH_SHORT).show();
            Log.e("info", encoded_image);
            return  encoded_image;


        }

    @Override
    protected void onStart() {
        super.onStart();


}
public void  getProfile()
{
    final ProgressDialog progressDialog=new ProgressDialog(ProfileActivity.this);
    progressDialog.setMessage("Fetching...");
    progressDialog.show();
    StringRequest request=new StringRequest(Request.Method.POST, api.getprofile, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //Toast.makeText(ProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();
            try {
                JSONObject jsonObject=new JSONObject(response);
                Gson gson = new Gson();

//                    DashboardModel dashboardModell= gson.fromJson(jsonObject.getJSONObject("data").toString(), DashboardModel.class);
////                    dashboardModel.add(dashboardModell);
//                    dashboardAdapter.notifyDataSetChanged();
                String success =jsonObject.getString("success");
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                Log.e("state",success);

                if(success.equals("1")){
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject object=jsonArray.getJSONObject(i);
                        name.setText(object.getString("name"));
                        mobile.setText(object.getString("mobileno"));
                        profession.setText(object.getString("prof"));
                        bio.setText(object.getString("bio"));
                        Glide.with(ProfileActivity.this).load("https://failuresuno.com/"+object.getString("image")).into(imageView);

                        cc=object.getString("image");



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
            Toast.makeText(ProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }


    ){

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {

            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String string  = dateFormat.format(new Date());

            Map<String,String>params=new HashMap<>();
            params.put("userid",firebaseAuth.getCurrentUser().getUid());

            // params.put("cnumber",cnumber.getText().toString());


            return params;
        }
    };

    RequestQueue requestQueue= Volley.newRequestQueue(ProfileActivity.this);
    requestQueue.add(request);

}
}



