package com.application.failuresuno;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.yalantis.filter.adapter.FilterAdapter;
import com.yalantis.filter.listener.FilterListener;
import com.yalantis.filter.widget.Filter;
import com.yalantis.filter.widget.FilterItem;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllAudioPostActivity extends AppCompatActivity implements FilterListener<Tag> {
    private Filter<Tag> mFilter;
    private int[] mColors;
    private String[] mTitles;

    List<HomeModel> homeModels;
    HomeModel homeModel;
    String filter="All categories";
    Adapter_Home adapter_home;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    Api api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_audio_post);
        mColors = getResources().getIntArray(R.array.colors);
        mTitles = getResources().getStringArray(R.array.job_titles);

        mFilter = (Filter<Tag>) findViewById(R.id.filter);
        mFilter.setAdapter(new Adapter(getTags()));
        mFilter.setListener(AllAudioPostActivity.this);

        //the text to show when there's no selected items
        mFilter.setNoSelectedItemText(getString(R.string.str_all_selected));
        mFilter.build();

        //mFilter.expand();
        api=new Api();
        firebaseAuth=FirebaseAuth.getInstance();
        // Passing each menu ID as a set of Ids because each

      recyclerView = findViewById(R.id.list);

        homeModels = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(AllAudioPostActivity.this,LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(layoutManager);

        adapter_home = new Adapter_Home(homeModels);
        recyclerView.setAdapter(adapter_home);
        getData();



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
    class Adapter extends FilterAdapter<Tag> {

        Adapter(@NotNull List<? extends Tag> items) {
            super(items);
        }

        @NotNull
        @Override
        public FilterItem createView(int position, Tag item) {
            FilterItem filterItem = new FilterItem(AllAudioPostActivity.this);

            // filterItem.setStrokeColor(mColors[0]);
            filterItem.setTextColor(ContextCompat.getColor(AllAudioPostActivity.this, android.R.color.white));
            filterItem.setStrokeColor(ContextCompat.getColor(AllAudioPostActivity.this, android.R.color.black));
            //  filterItem.setTextColor(mColors[0]);
            filterItem.setCornerRadius(14);
            filterItem.setCheckedTextColor(ContextCompat.getColor(AllAudioPostActivity.this, android.R.color.white));
            // filterItem.setColor(ContextCompat.getColor(HomeActivity.this, android.R.color.darker_gray));
            filterItem.setColor(mColors[position]);

            filterItem.setCheckedColor(mColors[position]);
            filterItem.setText(item.getText());
            filterItem.deselect();

            return filterItem;
        }
    }
    public void getData(){
        final ProgressDialog progressDialog=new ProgressDialog(AllAudioPostActivity.this);
        progressDialog.setMessage("Fetching...");
        progressDialog.show();
        StringRequest request=new StringRequest(Request.Method.POST, api.retreive, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                homeModels.clear();

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

if(posttype.equals("Audio")) {
    if (filter.equals("All categories")) {
        //Toast.makeText(AllPostActivity.this, ""+filter, Toast.LENGTH_SHORT).show();
        homeModel = new HomeModel(id, posttype, category, userid, title, postdata, reported, likes, posttimestamp, thumbnail);
        homeModels.add(homeModel);
        adapter_home.notifyDataSetChanged();
    } else {
        if (category.equals(filter)) {
            // Toast.makeText(AllPostActivity.this, "matched", Toast.LENGTH_SHORT).show();
            homeModel = new HomeModel(id, posttype, category, userid, title, postdata, reported, likes, posttimestamp, thumbnail);
            homeModels.add(homeModel);
            adapter_home.notifyDataSetChanged();
        }
    }
}





                        }

                    }


                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }

progressDialog.dismiss();
                if(homeModels.size()==0)
                {
                    Toast.makeText(AllAudioPostActivity.this, "No Post Found..!", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AllAudioPostActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


        );

        RequestQueue requestQueue= Volley.newRequestQueue(AllAudioPostActivity.this);
        requestQueue.add(request);


    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }
}
