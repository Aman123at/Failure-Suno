package com.application.failuresuno;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.hugomatilla.audioplayerview.AudioPlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class Adapter_Home extends RecyclerView.Adapter<Adapter_Home.ViewHolder> {
    Context context;

    List<HomeModel> homemodel;
    Api api = new Api();
    MediaPlayer mediaPlayer;
    TextView current, duration;
    Handler handler = new Handler();
    SeekBar seekBar;

    ProgressDialog progressDialog;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth  firebaseAuth;
boolean intials=true;
boolean playpause;
    public Adapter_Home(List<HomeModel> homemodel) {
        this.homemodel = homemodel;
    }

    @NonNull
    @Override
    public Adapter_Home.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Adapter_Home.ViewHolder holder, final int position) {

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        holder.setIsRecyclable(false);
        if(firebaseAuth.getCurrentUser()!=null)
        {
            if(homemodel.get(position).getUserid().equals(firebaseAuth.getCurrentUser().getUid()))
            holder.delete.setVisibility(View.VISIBLE);
        }
        if (homemodel.get(position).getPosttype().equals("Text")) {
            holder.type.setBackgroundResource(R.drawable.notepad);
            holder.play.setVisibility(View.GONE);

            holder.story.setText(homemodel.get(position).getPostdata());
        } else {
            holder.type.setBackgroundResource(R.drawable.headphones);
            holder.story.setVisibility(View.GONE);
            holder.play.setVisibility(View.VISIBLE);
            holder.relativeLayout.setVisibility(View.VISIBLE);
            holder.more.setVisibility(View.INVISIBLE);

        }
holder.userdp.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(context,ViewProfileActivity.class);
        intent.putExtra("userid",homemodel.get(position).getUserid());
        context.startActivity(intent);
    }
});
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homemodel.get(position).getPosttype().equals("Text")) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Read this story which i found  on FailureSuno App." + "\n" + "Download The App Now" + "\n" + homemodel.get(position).getPostdata());
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                } else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Listen this story which i found  on FailureSuno App." + "\n" + "Download The App Now" + "\n" + homemodel.get(position).getPostdata());
                    sendIntent.setType("text/plain");

                    context.startActivity(sendIntent);
                }
            }
        });

        holder.title.setText(homemodel.get(position).getTitle());
        holder.timestamp.setText(homemodel.get(position).getposttimestamp());
        holder.category.setText("#" + homemodel.get(position).getCategory());
     //   holder.likes.setText(homemodel.get(position).getLikes() + " Likes");
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homemodel.get(position).getPosttype().equals("Text")) {
                    Intent intent = new Intent(context, ReadStory.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("title", homemodel.get(position).getTitle());
                    intent.putExtra("category", homemodel.get(position).getCategory());
                    intent.putExtra("story", homemodel.get(position).getPostdata());
                    intent.putExtra("image", homemodel.get(position).getThumbnail());
                    context.startActivity(intent);
                }

            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    firebaseFirestore.collection("Posts").document(homemodel.get(position).getId()).collection("Likes").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.getResult().exists()) {
                                Map<String, Object> likemap = new HashMap<>();
                                likemap.put("timestamp", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("Posts").document(homemodel.get(position).getId()).collection("Likes").document(firebaseAuth.getCurrentUser().getUid()).set(likemap);

                            } else {
                                firebaseFirestore.collection("Posts").document(homemodel.get(position).getId()).collection("Likes").document(firebaseAuth.getCurrentUser().getUid()).delete();
                            }
                        }
                    });
                }
            }
        });

        firebaseFirestore.collection("Posts").document(homemodel.get(position).getId()).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    int count = queryDocumentSnapshots.size();
                    setlikes(homemodel.get(position).getId(),count+"");
                    holder.likescount.setText(count + " " + "Likes");


                } else {
                    holder.likescount.setText("0" + " " + "Likes");
                    setlikes(homemodel.get(position).getId(),"0");

                }
            }
        });

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseFirestore.collection("Posts").document(homemodel.get(position).getId()).collection("Likes").document(firebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @SuppressLint("NewApi")
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        holder.like.setImageDrawable(context.getDrawable(R.drawable.heart));
                    } else {
                        holder.like.setImageDrawable(context.getDrawable(R.drawable.ic_heart));
                    }
                }
            });

    }

        holder.play.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
               if(homemodel.get(position).getPosttype().equals("Audio")){
                  Intent intent = new Intent(context, ListenStory.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  intent.putExtra("title", homemodel.get(position).getTitle());
                  intent.putExtra("category", homemodel.get(position).getCategory());
                  intent.putExtra("story", homemodel.get(position).getPostdata());
                  intent.putExtra("image", homemodel.get(position).getThumbnail());
                  context.startActivity(intent);
              }
          }
      });

      holder.report.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              AlertDialog alertDialog= new AlertDialog.Builder(context)
                      .setTitle("Report Story")
                      .setMessage("Are you sure you want to report this story?")

                      // Specifying a listener allows you to take an action before dismissing the dialog.
                      // The dialog is automatically dismissed when a dialog button is clicked.
                      .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int which) {
                              final ProgressDialog progressDialog=new ProgressDialog(context);
                              progressDialog.setMessage("Reporting...");
                              progressDialog.show();
                              StringRequest request=new StringRequest(Request.Method.POST, api.reportpost, new Response.Listener<String>() {
                                  @Override
                                  public void onResponse(String response) {

                                      Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();
                                                                          progressDialog.dismiss();



                                  }
                              }, new Response.ErrorListener() {
                                  @Override
                                  public void onErrorResponse(VolleyError error) {

                                      Toast.makeText(context ,""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                  }
                              }


                              ){

                                  @Override
                                  protected Map<String, String> getParams() throws AuthFailureError {


                                      Map<String, String> params = new HashMap<>();
                                      params.put("id", homemodel.get(position).getId());

                                      // params.put("cnumber",cnumber.getText().toString());


                                      return params;
                                  }
                              };


                              RequestQueue requestQueue= Volley.newRequestQueue(context);
                              requestQueue.add(request);


                          }
                      })

                      // A null listener allows the button to dismiss the dialog
                      .setIcon(android.R.drawable.ic_dialog_alert)
                      .show();
          }
      });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog alertDialog= new AlertDialog.Builder(context)
                        .setTitle("Delete Story")
                        .setMessage("Are you sure you want to delete this story?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog progressDialog=new ProgressDialog(context);
                                progressDialog.setMessage("Deleting...");
                                progressDialog.show();
                                StringRequest request=new StringRequest(Request.Method.POST, api.deletepost, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();
                                      Intent intent=new Intent(context,context.getClass());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);



                                        progressDialog.dismiss();



                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Toast.makeText(context ,""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }


                                ){

                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {


                                        Map<String, String> params = new HashMap<>();
                                        params.put("id", homemodel.get(position).getId());

                                        // params.put("cnumber",cnumber.getText().toString());


                                        return params;
                                    }
                                };


                                RequestQueue requestQueue= Volley.newRequestQueue(context);
                                requestQueue.add(request);


                            }
                        })

                        // A null listener allows the button to dismiss the dialog
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });







        StringRequest request = new StringRequest(Request.Method.POST, api.getprofile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(ProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Gson gson = new Gson();

//
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Log.e("state", success);

                    if (success.equals("1")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            holder.name.setText(object.getString("name"));

                            Glide.with(context).load("https://failuresuno.com/" + object.getString("image")).into(holder.userdp);


                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }


        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String string = dateFormat.format(new Date());

                Map<String, String> params = new HashMap<>();
                params.put("userid", homemodel.get(position).getUserid());

                // params.put("cnumber",cnumber.getText().toString());


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);


    }

    @Override
    public int getItemCount() {
        return homemodel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView category, timestamp, title, likes, story;
        ImageView type;
        TextView more;
        CircleImageView userdp;
        TextView name;

        RelativeLayout relativeLayout;
ImageView report;
ImageView share;
ImageView like,delete;

       LottieAnimationView play;
       TextView likescount;
        // ArgPlayerSmallView argMusicPlayer;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            type = mView.findViewById(R.id.type);
            category = mView.findViewById(R.id.category);
            timestamp = mView.findViewById(R.id.text_date);
            share=mView.findViewById(R.id.share);
            title = mView.findViewById(R.id.text_title);
            story = mView.findViewById(R.id.text_story);
            likes = mView.findViewById(R.id.likescount);
            more = mView.findViewById(R.id.view);
            name = mView.findViewById(R.id.text_name);
            userdp = mView.findViewById(R.id.avatar);
            relativeLayout = mView.findViewById(R.id.frame);
            report=mView.findViewById(R.id.reportpost);
            like=mView.findViewById(R.id.heart);
            likescount=mView.findViewById(R.id.likescount);
            play = mView.findViewById(R.id.play);
            delete=mView.findViewById(R.id.delete);



        }
    }

public void setlikes(final String id, final String likes)
{
    StringRequest request=new StringRequest(Request.Method.POST, api.setlikes, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //Toast.makeText(ProfileActivity.this, ""+response, Toast.LENGTH_SHORT).show();

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }


    ){

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {

            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String string  = dateFormat.format(new Date());

            Map<String,String>params=new HashMap<>();
            params.put("id",id);
            params.put("likes",likes);

            // params.put("cnumber",cnumber.getText().toString());


            return params;
        }
    };

    RequestQueue requestQueue= Volley.newRequestQueue(context);
    requestQueue.add(request);

}




}




