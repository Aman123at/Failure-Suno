package com.application.failuresuno;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

public class ListenStory extends AppCompatActivity {
    TextView current, duration;
    SeekBar seekBar;
    ImageView play;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    String category;
    CircleImageView imageView;
    String text1,text2,text3;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    boolean intials=true;
    boolean playpause;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_story);
        text1=getIntent().getStringExtra("title");
        progressBar=findViewById(R.id.bar);

        text2=getIntent().getStringExtra("story");
        text3=getIntent().getStringExtra("image");
        category=getIntent().getStringExtra("category");
        current = findViewById(R.id.textCurrenttime);
        duration = findViewById(R.id.texttotalduration);
        seekBar = findViewById(R.id.seekbar);
        play = findViewById(R.id.play);
        imageView=findViewById(R.id.imageview);
        TextView textView=findViewById(R.id.title);
        textView.setText(text1);
    //    Glide.with(ListenStory.this).load("https://failuresuno.com/" + text3).into(imageView);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        progressDialog=new ProgressDialog(this);
        seekBar.setMax(100);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!playpause)
                {
                    play.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    if(intials)
                    {
                        prepreMediaPlayer();

                    }
                    else
                    {
                        if (!mediaPlayer.isPlaying())
                        {
                            mediaPlayer.start();
                            play.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                        }

                    }
                    playpause=true;
                }

                else

                    {

                    play.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);


                    if(mediaPlayer.isPlaying())

                    {
                        handler.removeCallbacks(updater);
                        mediaPlayer.pause();



                    }
                    playpause=false;

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
//                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        seekBar.setProgress(0);
//                        play.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
//                        current.setText("0:00");
//                       mediaPlayer.reset();
//                        prepreMediaPlayer();
//                    }
//                });

    }

    public void prepreMediaPlayer() {

        new Player().execute(text2);
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
            if(mediaPlayer.isPlaying()) {
                updateseekbar();
                long currentDuration = mediaPlayer.getCurrentPosition();
                current.setText(millisecondToTimer(currentDuration));
            }
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
     mediaPlayer.release();
     mediaPlayer=new MediaPlayer();
      //  Toast.makeText(this, "Cliked", Toast.LENGTH_SHORT).show();



    }

    class  Player extends  AsyncTask<String,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(String... strings) {

            Boolean prepared=false;
            try{
                mediaPlayer.setDataSource(strings[0]);
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
    protected void onPause() {


        super.onPause();
        mediaPlayer.release();
        mediaPlayer=new MediaPlayer();
//        if(mediaPlayer!=null)
//        {
//            mediaPlayer.reset();
//            mediaPlayer.release();
//            mediaPlayer=null;
//        }
    }
}


