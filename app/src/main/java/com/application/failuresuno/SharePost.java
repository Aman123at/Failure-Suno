package com.application.failuresuno;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.yalantis.filter.adapter.FilterAdapter;
import com.yalantis.filter.listener.FilterListener;
import com.yalantis.filter.widget.Filter;
import com.yalantis.filter.widget.FilterItem;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharePost extends AppCompatActivity implements FilterListener<Tag> {

    private Filter<Tag> mFilter;
    private int[] mColors;
    private String[] mTitles;
    ImageView imageView;
    Button button;
    String category="null";
    EditText title,data;
    Uri postimageuri;
    String encoded_image;
 Bitmap bitmap;
    Api api;

String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_post);
        api=new Api();
        title=findViewById(R.id.title);
        data=findViewById(R.id.story);

        imageView=findViewById(R.id.imageview);
        button=findViewById(R.id.submitbutton);

        mColors = getResources().getIntArray(R.array.colors);
        mTitles = getResources().getStringArray(R.array.job_titles);

        type=getIntent().getStringExtra("type");
      //  Toast.makeText(this, ""+type
            //    , Toast.LENGTH_SHORT).show();
        mFilter = (Filter<Tag>) findViewById(R.id.filter);
        mFilter.setAdapter(new Adapter(getTags()));
        mFilter.setListener(SharePost.this);

        //the text to show when there's no selected items
        mFilter.setNoSelectedItemText(getString(R.string.str_all_selected));
        mFilter.build();
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
//
//            }
//        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category.equals("null"))
                {
                    // Toast.makeText(getActivity(), "Enter Username", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SharePost.this, "Select Your Category", Toast.LENGTH_SHORT).show();
                }
                else if(title.getText().equals(""))
                {
                    Toast.makeText(SharePost.this, "Enter Your Title", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(data.getText().toString()))
                {

                }
                else {
                    //  Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                    final ProgressDialog progressDialog=new ProgressDialog(SharePost.this);
                    progressDialog.setMessage("Posting...");
                    progressDialog.show();
//
                    StringRequest request=new StringRequest(Request.Method.POST, api.addpost, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(SharePost.this, ""+response, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent=new Intent(SharePost.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SharePost.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent=new Intent(SharePost.this,SharePost.class);
                            startActivity(intent);
                            finish();
                        }
                    }


                    ){

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String string  = dateFormat.format(new Date());

                            Map<String,String>params=new HashMap<>();

                            params.put("type",type);
                            params.put("category",category);
                            params.put("userid",firebaseAuth.getCurrentUser().getUid()+"");
                            params.put("title",title.getText().toString());
                            params.put("postdata",data.getText().toString());
                            params.put("reported","0");
                            params.put("likes","0");
                            params.put("timestamp", string);
                            params.put("thumbnail","xx");

                            //params.put("cnumber",cnumber.getText().toString());


                            return params;
                        }
                    };

                    RequestQueue requestQueue= Volley.newRequestQueue(SharePost.this);
                    requestQueue.add(request);
                }

            }
        });

    }

    private List<Tag> getTags() {
        List<Tag> tags = new ArrayList<>();

        for (int i = 0; i < mTitles.length; ++i) {
            tags.add(new Tag(mTitles[i], mColors[i]));
        }

        return tags;
    }

    @Override
    public void onFilterDeselected(Tag tag) {

    }

    @Override
    public void onFilterSelected(Tag item) {
        if (item.getText().equals(mTitles[0])) {
            mFilter.deselectAll();
            //    mFilter.collapse();
        }
        else {
            category=item.getText().toString();
            Toast.makeText(this, ""+item.getText(), Toast.LENGTH_SHORT).show();
            mFilter.deselectAll();
              mFilter.collapse();

            mFilter.setNoSelectedItemText(item.getText());
        }
    }

    @Override
    public void onFiltersSelected(@NotNull ArrayList<Tag> arrayList) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    class Adapter extends FilterAdapter<Tag> {

        Adapter(@NotNull List<? extends Tag> items) {
            super(items);
        }

        @NotNull
        @Override
        public FilterItem createView(int position, Tag item) {
            FilterItem filterItem = new FilterItem(SharePost.this);

            // filterItem.setStrokeColor(mColors[0]);
            filterItem.setTextColor(ContextCompat.getColor(SharePost.this, android.R.color.white));
            filterItem.setStrokeColor(ContextCompat.getColor(SharePost.this, android.R.color.black));
            //  filterItem.setTextColor(mColors[0]);
            filterItem.setCornerRadius(14);
            filterItem.setCheckedTextColor(ContextCompat.getColor(SharePost.this, android.R.color.white));
            // filterItem.setColor(ContextCompat.getColor(HomeActivity.this, android.R.color.darker_gray));
            filterItem.setColor(mColors[position]);

            filterItem.setCheckedColor(mColors[position]);
            filterItem.setText(item.getText());
            filterItem.deselect();

            return filterItem;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            postimageuri = data.getData();
            try {

                bitmap= MediaStore.Images.Media.getBitmap(SharePost.this.getContentResolver(),postimageuri);
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
        //  Toast.makeText(getContext(), "fdd"+encoded_image, Toast.LENGTH_SHORT).show();
        Log.e("info", encoded_image);
        return  encoded_image;


    }
}


