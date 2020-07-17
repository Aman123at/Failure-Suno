package com.application.failuresuno;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class ShareAudioPost extends AppCompatActivity implements FilterListener<Tag> {

    private Filter<Tag> mFilter;
    private int[] mColors;
    private String[] mTitles;
    ImageView imageView;
    Button button,uploadbtn;
    String category="null";
    EditText title,data;
    Uri postimageuri,postaudiouri;
    String encoded_image;
 Bitmap bitmap;
    Api api;
    StorageReference storagerefrence;

String type;
    TextView current, duration;
    SeekBar seekBar;
    ImageView play;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    ProgressDialog progressDialog;
    boolean intials=true;
    boolean playpause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_audio__post);

        current = findViewById(R.id.textCurrenttime);
        duration = findViewById(R.id.texttotalduration);
        seekBar = findViewById(R.id.seekbar);
        play = findViewById(R.id.play);
        imageView=findViewById(R.id.imageview);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog=new ProgressDialog(this);
        seekBar.setMax(100);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if(postaudiouri!=null) {
    if (!playpause) {
        play.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
        if (intials) {
            prepreMediaPlayer();

        } else {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                play.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            }

        }
        playpause = true;
    } else {

        play.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);


        if (mediaPlayer.isPlaying()) {
            handler.removeCallbacks(updater);
            mediaPlayer.pause();


        }
        playpause = false;

    }


}
else {
    Toast.makeText(ShareAudioPost.this, "Select a Audio File", Toast.LENGTH_SHORT).show();
}
            }
        });




        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                SeekBar seekBar=(SeekBar) v;
                int playpos=(mediaPlayer.getDuration()/100)*seekBar.getProgress();
                mediaPlayer.seekTo(playpos);
                current.setText(millisecondToTimer(mediaPlayer.getCurrentPosition()));

                return false;
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBar.setSecondaryProgress(percent);

            }
        });










        api=new Api();
        title=findViewById(R.id.title);
        data=findViewById(R.id.story);

        imageView=findViewById(R.id.imageview);
        button=findViewById(R.id.submitbutton);
        uploadbtn=findViewById(R.id.uploadaudio);
        storagerefrence = FirebaseStorage.getInstance().getReference();

        mColors = getResources().getIntArray(R.array.colors);
        mTitles = getResources().getStringArray(R.array.job_titles);

        type=getIntent().getStringExtra("type");

        mFilter = (Filter<Tag>) findViewById(R.id.filter);
        mFilter.setAdapter(new Adapter(getTags()));
        mFilter.setListener(ShareAudioPost.this);

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



        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), 2);

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
                if(category.equals("null"))
                {
                    // Toast.makeText(getActivity(), "Enter Username", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ShareAudioPost.this, "Select Your Category", Toast.LENGTH_SHORT).show();
                }
                else if(title.getText().equals(""))
                {
                    Toast.makeText(ShareAudioPost.this, "Enter Your Title", Toast.LENGTH_SHORT).show();
                }

                else {
                    //  Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                    final ProgressDialog progressDialog=new ProgressDialog(ShareAudioPost.this);
                    progressDialog.setMessage("Posting...");
                    progressDialog.show();
                    final String randomName = UUID.randomUUID().toString();
                    final UploadTask filepath = storagerefrence.child("Audio").child(randomName + ".mp3").putFile(postaudiouri);
                    filepath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {

                                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                     final String   downloadurl1 = uri.toString();
                                        StringRequest request=new StringRequest(Request.Method.POST, api.addpost, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Toast.makeText(ShareAudioPost.this, ""+response, Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                Intent intent=new Intent(ShareAudioPost.this,HomeActivity.class);
                                            startActivity(intent);
                                            finish();

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(ShareAudioPost.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                Intent intent=new Intent(ShareAudioPost.this,ShareAudioPost.class);
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
                                                params.put("postdata",downloadurl1);
                                                params.put("reported","0");
                                                params.put("likes","0");
                                                params.put("timestamp", string);
                                                params.put("thumbnail","xx");

                                                //params.put("cnumber",cnumber.getText().toString());


                                                return params;
                                            }
                                        };

                                        RequestQueue requestQueue= Volley.newRequestQueue(ShareAudioPost.this);
                                        requestQueue.add(request);

                                    }
                                });




                            } else {
                         progressDialog.dismiss();
                                Toast.makeText(ShareAudioPost.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


//

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
            FilterItem filterItem = new FilterItem(ShareAudioPost.this);

            // filterItem.setStrokeColor(mColors[0]);
            filterItem.setTextColor(ContextCompat.getColor(ShareAudioPost.this, android.R.color.white));
            filterItem.setStrokeColor(ContextCompat.getColor(ShareAudioPost.this, android.R.color.black));
            //  filterItem.setTextColor(mColors[0]);
            filterItem.setCornerRadius(14);
            filterItem.setCheckedTextColor(ContextCompat.getColor(ShareAudioPost.this, android.R.color.white));
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

                bitmap= MediaStore.Images.Media.getBitmap(ShareAudioPost.this.getContentResolver(),postimageuri);
                imageView.setImageURI(postimageuri);
                imagestore(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
       else if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            postaudiouri = data.getData();
            //encoded_image=getPath(postimageuri);
            Toast.makeText(this, "Audio Selected", Toast.LENGTH_SHORT).show();

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
    public void prepreMediaPlayer() {

        new Player().execute("fdfd");
//
//    try
//
//    {
//
//
//        mediaPlayer.setDataSource(text2);
//        mediaPlayer.prepare();
//
//
//        }
//    catch(Exception exception){
//
//    }

    }
    public Runnable updater=new Runnable() {
        @Override
        public void run() {
            updateseekbar();
            long currentDuration=mediaPlayer.getCurrentPosition();
            current.setText(millisecondToTimer(currentDuration));

        }
    };

    public void updateseekbar(){
        if(mediaPlayer.isPlaying())
        {
            seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100));
            handler.postDelayed(updater,1000);


        }


    }
    public String millisecondToTimer(long millisecond)
    {
        String timerString="",secondString;
        int hours=(int)(millisecond/(1000*60*60));
        int minutes=(int)(millisecond % (1000*60*60))/(1000*60);
        int second=(int)((millisecond % (1000*60*60))%(1000*60)/1000);
        if(hours>0)
        {
            timerString=hours+":";
        }
        if(second<10)
            secondString="0"+second;
        else
            secondString=""+second;

        timerString=timerString+minutes+":"+secondString;
        return  timerString;
    }





    class  Player extends AsyncTask<String,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(String... strings) {

            Boolean prepared=false;
            try{
                mediaPlayer.setDataSource(getApplicationContext(),postaudiouri);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        intials=true;
                        playpause=false;
                        seekBar.setProgress(0);
                        play.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                        current.setText("0:00");
                        mediaPlayer.stop();
                        mediaPlayer.reset();

                        //   prepreMediaPlayer();

                    }
                });
                mediaPlayer.prepare();
                prepared=true;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(progressDialog.isShowing())
            {
                progressDialog.cancel();
            }

            duration.setText(millisecondToTimer(mediaPlayer.getDuration()));
            mediaPlayer.start();
            intials=false;
            updateseekbar();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Buffering.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null)
        {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }


}


