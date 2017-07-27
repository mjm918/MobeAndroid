package akash.com.mobe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import akash.com.mobe.Constructor.FeedBack;
import akash.com.mobe.Constructor.NewsFeed;
import akash.com.mobe.Helper.HTTPHelper;
import akash.com.mobe.Helper.NewsFeedListAdapter;
import akash.com.mobe.Helper.SwipeDetector;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static akash.com.mobe.Helper.Config.ACTIVITY_TAG;
import static akash.com.mobe.Helper.Config.API_DATE;
import static akash.com.mobe.Helper.Config.API_EMAIL;
import static akash.com.mobe.Helper.Config.API_EMO;
import static akash.com.mobe.Helper.Config.API_EMP_DATA;
import static akash.com.mobe.Helper.Config.API_ERROR;
import static akash.com.mobe.Helper.Config.API_FAIL;
import static akash.com.mobe.Helper.Config.API_FNAME;
import static akash.com.mobe.Helper.Config.API_INFO_LINK;
import static akash.com.mobe.Helper.Config.API_INSERT_ERROR;
import static akash.com.mobe.Helper.Config.API_LNAME;
import static akash.com.mobe.Helper.Config.API_NEWSFEED_LINK;
import static akash.com.mobe.Helper.Config.API_TIME;
import static akash.com.mobe.Helper.Config.EMPLOYEE_ACTIVITY;
import static akash.com.mobe.Helper.Config.MANAGER_ACTIVITY;

public class Newsfeed extends AppCompatActivity {

    private String PREVIOUS_ACTIVITY;

    private CoordinatorLayout coordinatorLayout;
    private ListView listView;
    private TextView tv_loading;
    private MaterialProgressBar progressBar;
    private RelativeLayout relativeLayout;

    private List<NewsFeed> newsFeeds;
    private NewsFeedListAdapter adapter;

    private Context context = this;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        Bundle bundle = getIntent().getExtras();
        PREVIOUS_ACTIVITY = bundle.getString(ACTIVITY_TAG);

        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.layout_news);
        listView = (ListView) this.findViewById(R.id.listView);
        tv_loading = (TextView) this.findViewById(R.id.tv_loading);
        progressBar = (MaterialProgressBar) this.findViewById(R.id.progress);
        relativeLayout = (RelativeLayout)this.findViewById(R.id.layout_hide);

        newsFeeds = new ArrayList<NewsFeed>();
        adapter = new NewsFeedListAdapter(Newsfeed.this,newsFeeds);
        listView.setAdapter(adapter);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.open);

        final SwipeDetector swipeDetector = new SwipeDetector();
        listView.setOnTouchListener(swipeDetector);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if(swipeDetector.swipeDetected()) {
                            if(swipeDetector.getAction() == SwipeDetector.Action.LR) {
                                GoBack();
                            }else{
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Swipe left to right to go back.", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }

                        view.startAnimation(animation);

                    }
                });
            }
        });

        GetFeed();

    }

    public void GetFeed(){

        class feedExec extends AsyncTask<String, Void, String>{

            private HTTPHelper ruc = new HTTPHelper();

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("flag",params[0]);

                return ruc.sendPostRequest(API_NEWSFEED_LINK,data);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                ShowProgress(true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                ShowProgress(false);

                System.out.println("RESPONSE -> "+s);

                switch (s){
                    case API_ERROR: {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "API Error. Please contact the back office", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    case API_FAIL:{

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShowProgress(false);
                                tv_loading.setVisibility(View.VISIBLE);
                                tv_loading.setText(context.getResources().getString(R.string.empty));
                            }
                        });

                        break;
                    }
                    case API_INSERT_ERROR:{
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    default:
                        parseJSON(s);
                        break;
                }
                if(newsFeeds.size() == 0){
                    GetFeed();

                    System.out.println("CALLING THE API AGAIN -> "+s);
                }
            }
        }
        feedExec fd = new feedExec();
        fd.execute("1");
    }

    public void parseJSON(String json){

        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray(API_EMP_DATA);

            for(int i = 0 ; i < array.length() ; i++){

                NewsFeed newsFeed = new NewsFeed();

                JSONObject object = array.getJSONObject(i);

                newsFeed.setFname(object.getString(API_FNAME));
                newsFeed.setLname(object.getString(API_LNAME));
                newsFeed.setEmo(object.getString(API_EMO));
                newsFeed.setDay(object.getString(API_DATE));
                newsFeed.setEmail(object.getString(API_EMAIL));
                newsFeed.setTime(object.getString(API_TIME));

                newsFeeds.add(newsFeed);

            }
            adapter.notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void GoBack(){
        switch (PREVIOUS_ACTIVITY){
            case MANAGER_ACTIVITY:
                Intent intent = new Intent(context, ManagerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                finish();
                break;
            case EMPLOYEE_ACTIVITY:
                Intent intentM = new Intent(context, EmployeeActivity.class);
                intentM.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intentM);
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                finish();
                break;
        }
    }

    public void ShowProgress(boolean command){
        if(!command){
            relativeLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            tv_loading.setVisibility(View.INVISIBLE);
        }else{
            relativeLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            tv_loading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        GoBack();
    }
}
