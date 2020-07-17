package com.application.failuresuno;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.yalantis.filter.adapter.FilterAdapter;
import com.yalantis.filter.listener.FilterListener;
import com.yalantis.filter.widget.Filter;
import com.yalantis.filter.widget.FilterItem;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements FilterListener<Tag> {
    CarouselView carouselView;

    List<HomeModel> homeModels;
    HomeModel homeModel;
    Adapter_Home adapter_home;
    FirebaseAuth firebaseAuth;
Api api;
View playout;
CircleImageView imageView;
String filter="All categories";
ImageView play;
    private Filter<Tag> mFilter;
    private int[] mColors;
    private String[] mTitles;
    List<String> mImages;
    RecyclerView recyclerView;

    TextView current, duration;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();

    ProgressDialog progressDialog;
    boolean intials=true;
    boolean playpause;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

  api=new Api();
        firebaseAuth=FirebaseAuth.getInstance();
        bottomNavigationView.setItemIconTintList(null);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mColors = getResources().getIntArray(R.array.colors);
        imageView=findViewById(R.id.uimage);
        mTitles = getResources().getStringArray(R.array.job_titles);
        mFilter = (Filter<Tag>) findViewById(R.id.filter);
        mFilter.setAdapter(new Adapter(getTags()));
        mFilter.setListener(HomeActivity.this);
        mFilter.setNoSelectedItemText(getString(R.string.str_all_selected));
        mFilter.build();
        if(firebaseAuth.getCurrentUser()!=null)
        {
         getProfileImage();
        }

        recyclerView = findViewById(R.id.list);
        homeModels = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this,LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(layoutManager);

        adapter_home = new Adapter_Home(homeModels);
        recyclerView.setAdapter(adapter_home);
        getData();

        findViewById(R.id.readstory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AllPostActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.listenstory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AllAudioPostActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.x).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AllPostActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.y).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AllAudioPostActivity.class);
                startActivity(intent);
            }
        });



        mImages = new ArrayList<>();
        mImages.add("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/1.jpg?alt=media&token=9909159f-ae55-4391-bb24-4611cb6dfc4a");
        mImages.add("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/2.png?alt=media&token=b1304d50-9c61-4255-a4f5-076e8c9cd462");
        mImages.add("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/3.png?alt=media&token=54635550-6835-45c2-b16c-cc9d98f9a9ea");
        mImages.add("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/4.jpg?alt=media&token=2a44e90c-b9fe-4ca3-8312-48a31c72a8be");
        mImages.add("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/5.png?alt=media&token=90ac37c7-6538-46cd-9b0b-b3455d9c9ccc");
        mImages.add("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/6.png?alt=media&token=a6bf13fa-231e-4041-9ebe-e0816c9b69cf");
        mImages.add("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/7.png?alt=media&token=8a556423-98ee-44ba-9432-98cdf5e1740d");


        carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(7);
        carouselView.setImageListener(imageListener);



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        Intent intentc=new Intent(HomeActivity.this,HomeActivity.class);
                        startActivity(intentc);
                        finish();
                        return  true;



                    case R.id.navigation_share:

                        if(firebaseAuth.getCurrentUser()==null)

                        {
                            Toast.makeText(HomeActivity.this, "Login to continue..!!", Toast.LENGTH_SHORT).show();
//                            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
//                            startActivity(intent);

                        }

                             else

                                 {


getProfile();




                                 }

                        return true;

                    case R.id.navigation_profile:

                        if(firebaseAuth.getCurrentUser()==null)
                        {
                            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                            intent.putExtra("t","Sign Up");
                            startActivity(intent);

                        }

                        else
                        {

                            Intent intent=new Intent(HomeActivity.this,ProfileActivity.class);
                            startActivity(intent);
                        }

                        return true;


                }

                return true;
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_login:
                FirebaseAuth firebaseAuth1=FirebaseAuth.getInstance();
                if(firebaseAuth1.getCurrentUser()==null) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);

                    final LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                    View quantity_layout = inflater.inflate(R.layout.login_type_chooser ,null);

                    alertDialog.setView(quantity_layout);

                    TextView text=quantity_layout.findViewById(R.id.text);
                    TextView audio=quantity_layout.findViewById(R.id.audio);

                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                            intent.putExtra("t","Sign In");
                            startActivity(intent);

                        }
                    });
                    audio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(HomeActivity.this,LoginEActivity.class);

                            startActivity(intent);

                        }
                    });
                    alertDialog.show();





                }
                else
                {
                    Toast.makeText(this, "You Are Logged In..!", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.navigation_signup:

                FirebaseAuth firebaseAuth2=FirebaseAuth.getInstance();
                if(firebaseAuth2.getCurrentUser()==null) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);

                    final LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                    View quantity_layout = inflater.inflate(R.layout.login_type_chooser ,null);

                    alertDialog.setView(quantity_layout);

                    TextView text=quantity_layout.findViewById(R.id.text);
                    TextView audio=quantity_layout.findViewById(R.id.audio);

                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
intent.putExtra("t","Sign Up");
                            startActivity(intent);

                        }
                    });
                    audio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(HomeActivity.this,RegisterActivity.class);

                            startActivity(intent);

                        }
                    });
                    alertDialog.show();





                }
                else
                {
                    Toast.makeText(this, "Logout to access..!! ", Toast.LENGTH_SHORT).show();

                }

                break;

            case R.id.navigation_logout:

               FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

               if(firebaseAuth.getCurrentUser()!=null) {
                   firebaseAuth.signOut();


                   Intent intent1 = new Intent(HomeActivity.this, HomeActivity.class);
                   startActivity(intent1);
               }
               else
               {
                   Toast.makeText(this, "User not registered", Toast.LENGTH_SHORT).show();
               }


                break;

            case  R.id.navigation_contactus:
//                Intent intentx=new Intent(HomeActivity.this,ListenStory.class);
//                startActivity(intentx);
                String url1 = "https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/contactus.txt?alt=media&token=f285497c-3d77-4baa-b30c-8a9d24a05240";
                Intent ii = new Intent(Intent.ACTION_VIEW);
                ii.setData(Uri.parse(url1));
                startActivity(ii);

                break;

            case R.id.navigation_privacy:

                String url2= "https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/privacy%20policy.txt?alt=media&token=9499ac34-5eb1-4500-934f-4022c0802ea8";
                Intent ii2 = new Intent(Intent.ACTION_VIEW);
                ii2.setData(Uri.parse(url2));
                startActivity(ii2);

                break;
            case R.id.navigation_aboutus:

            //    String url1 = "https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/privacy%20policy.txt?alt=media&token=9499ac34-5eb1-4500-934f-4022c0802ea8";
                Intent iiq = new Intent(this,AboutusActivity.class);

                startActivity(iiq);

                break;


        }

         return true;

    }

    private List<Tag> getTags()

    {
        List<Tag> tags = new ArrayList<>();

        for (int i = 0; i < mTitles.length; ++i) {
            tags.add(new Tag(mTitles[i], mColors[i]));
        }

        return tags;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Glide.with(HomeActivity.this)
                    .load(mImages.get(position))
                    .centerCrop()
                    .placeholder(R.color.demo_dark_transparent)
                    .into(imageView);
        }
    };


    @Override
    public void onFilterSelected(Tag item) {
        filter=item.getText();
        //   Toast.makeText(this, ""+item.getText(), Toast.LENGTH_SHORT).show();
        mFilter.deselectAll();
        mFilter.collapse();

        mFilter.setNoSelectedItemText(item.getText());
        getData();
        homeModels.clear();
        recyclerView.setAdapter(adapter_home);

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

    @Override
    public void onFilterDeselected(Tag tag) {

    }

    class Adapter extends FilterAdapter<Tag> {

        Adapter(@NotNull List<? extends Tag> items) {
            super(items);
        }

        @NotNull
        @Override
        public FilterItem createView(int position, Tag item) {


            FilterItem filterItem = new FilterItem(HomeActivity.this);

            // filterItem.setStrokeColor(mColors[0]);
            filterItem.setTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.white));
            filterItem.setStrokeColor(ContextCompat.getColor(HomeActivity.this, android.R.color.black));
            //  filterItem.setTextColor(mColors[0]);
            filterItem.setCornerRadius(14);
            filterItem.setCheckedTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.white));
            // filterItem.setColor(ContextCompat.getColor(HomeActivity.this, android.R.color.darker_gray));
            filterItem.setColor(mColors[position]);
            filterItem.setCheckedColor(mColors[position]);
            filterItem.setText(item.getText());
            filterItem.deselect();




            return filterItem;
        }
    }
    public  void getData()
    {
        final ProgressDialog progressDialog=new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Fetching...");
        progressDialog.show();
        StringRequest request=new StringRequest(Request.Method.POST, api.retreive, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                homeModels.clear();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                 //   Gson gson = new Gson();

                    String success =jsonObject.getString("success");
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    Log.e("state",success);

                    if(success.equals("1")){
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            String id=object.getString("id");
                            String posttype=object.getString("posttype");
                            String category=object.getString("category");
                            String userid=object.getString("userid");
                            String title =object.getString("title");
                            String postdata=object.getString("postdata");
                            String  reported=object.getString("reported");
                            String likes=object.getString("likes");
                            String posttimestamp=object.getString("posttimestamp");
                            String thumbnail=object.getString("thumbnail");



                            if(filter.equals("All categories")) {
                                //Toast.makeText(AllPostActivity.this, ""+filter, Toast.LENGTH_SHORT).show();
                                homeModel = new HomeModel(id, posttype, category, userid, title, postdata, reported, likes, posttimestamp, thumbnail);
                                homeModels.add(homeModel);
                                adapter_home.notifyDataSetChanged();
                            }
                            else {
                                if (category.equals(filter)) {
                                    // Toast.makeText(AllPostActivity.this, "matched", Toast.LENGTH_SHORT).show();
                                    homeModel = new HomeModel(id, posttype, category, userid, title, postdata, reported, likes, posttimestamp, thumbnail);
                                    homeModels.add(homeModel);
                                    adapter_home.notifyDataSetChanged();
                                }
                            }




                        }

                    }


                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                if(homeModels.size()==0)
                {
                    Toast.makeText(HomeActivity.this, "No Post Found", Toast.LENGTH_SHORT).show();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(HomeActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


        );

        RequestQueue requestQueue= Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(request);


    }
    public void prepreMediaPlayer() {

       new Player().execute("https://firebasestorage.googleapis.com/v0/b/failuresuno-f9110.appspot.com/o/Audio%2Fe8dae3f0-82fa-4a23-a08a-fe63f6b8c2ec.mp3?alt=media&token=b6bb9b6d-1913-4949-80f2-a026e2a42929");

    }
    public Runnable updater=new Runnable() {
        @Override
        public void run() {
            updateseekbar();
            if(mediaPlayer!=null) {
                long currentDuration = mediaPlayer.getCurrentPosition();
                current.setText(millisecondToTimer(currentDuration));
            }

        }
    };

    public void updateseekbar(){
        if( mediaPlayer!=null)
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


         mediaPlayer=null;


    }

    @Override
    protected void onStop() {

        super.onStop();
        mediaPlayer=null;


    }


    public void  getProfile()
    {
        final String[] name = new String[1];
        name[0]="X";
        final String[] image = new String[1];
        image[0]="X";
        image[0]="X";
        final String[] blocked = new String[1];
        blocked[0]="X";
        final ProgressDialog progressDialog=new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Fetching...");
        progressDialog.show();
        StringRequest request=new StringRequest(Request.Method.POST, api.getprofile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
              //  Toast.makeText(HomeActivity.this, ""+response, Toast.LENGTH_SHORT).show();


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
                            name[0] =object.getString("name");
                            image[0] =object.getString("image");
                            blocked[0] =object.getString("blocked");


                       }




                    }


                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
          //      Toast.makeText(HomeActivity.this, ""+name, Toast.LENGTH_SHORT).show();

                    if (name[0].equals("X") || image[0].equals("X")) {
                        Toast.makeText(HomeActivity.this, "Please Complete Your Profile..!!", Toast.LENGTH_SHORT).show();

                    } else {

                        if(blocked[0].equals("0")) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);

                            final LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                            View quantity_layout = inflater.inflate(R.layout.type_chooser, null);

                            alertDialog.setView(quantity_layout);

                            TextView text = quantity_layout.findViewById(R.id.text);
                            TextView audio = quantity_layout.findViewById(R.id.audio);

                            text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(HomeActivity.this, SharePost.class);
                                    intent.putExtra("type", "Text");
                                    startActivity(intent);

                                }
                            });
                            audio.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(HomeActivity.this, ShareAudioPost.class);
                                    intent.putExtra("type", "Audio");
                                    startActivity(intent);

                                }
                            });
                            alertDialog.show();
                        }
                        else {
                            Toast.makeText(HomeActivity.this, "Account Blocked..! Contact Support", Toast.LENGTH_SHORT).show();

                        }                    }


                    progressDialog.dismiss();
          }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue= Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(request);

    }
    public void  getProfileImage()
    {
        final String[] name = new String[1];
        name[0]="X";
        final String[] image = new String[1];

        final ProgressDialog progressDialog=new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Fetching...");
        progressDialog.show();
        StringRequest request=new StringRequest(Request.Method.POST, api.getprofile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  Toast.makeText(HomeActivity.this, ""+response, Toast.LENGTH_SHORT).show();


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
                            name[0] =object.getString("name");
                            image[0] =object.getString("image");


                        }




                    }


                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
                //      Toast.makeText(HomeActivity.this, ""+name, Toast.LENGTH_SHORT).show();
            if(name[0].equals("X")||image[0].equals("X"))
                {
                }

                else
                {
                    Glide.with(HomeActivity.this).load("https://failuresuno.com/" + image[0]).into(imageView);


                }





                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue= Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(request);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();

    }
}
