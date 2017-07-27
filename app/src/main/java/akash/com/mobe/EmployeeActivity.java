package akash.com.mobe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import akash.com.mobe.Constructor.FeedBack;
import akash.com.mobe.Helper.FeedBackListAdapter;
import akash.com.mobe.Helper.HTTPHelper;
import akash.com.mobe.Helper.Helper;
import akash.com.mobe.Helper.SwipeDetector;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static akash.com.mobe.Helper.Config.ACTIVITY_TAG;
import static akash.com.mobe.Helper.Config.API_APPROVE;
import static akash.com.mobe.Helper.Config.API_DATE;
import static akash.com.mobe.Helper.Config.API_DEPARTMENT;
import static akash.com.mobe.Helper.Config.API_DOB;
import static akash.com.mobe.Helper.Config.API_EMAIL;
import static akash.com.mobe.Helper.Config.API_EMO;
import static akash.com.mobe.Helper.Config.API_EMP_DATA;
import static akash.com.mobe.Helper.Config.API_ERROR;
import static akash.com.mobe.Helper.Config.API_FAIL;
import static akash.com.mobe.Helper.Config.API_FEEDBACK_LINK;
import static akash.com.mobe.Helper.Config.API_FNAME;
import static akash.com.mobe.Helper.Config.API_INFO_LINK;
import static akash.com.mobe.Helper.Config.API_INSERT_ERROR;
import static akash.com.mobe.Helper.Config.API_LNAME;
import static akash.com.mobe.Helper.Config.API_SHARE_LINK;
import static akash.com.mobe.Helper.Config.API_SUCCESS;
import static akash.com.mobe.Helper.Config.API_USER_INFO;
import static akash.com.mobe.Helper.Config.API_VOTE_LINK;
import static akash.com.mobe.Helper.Config.EMPLOYEE_ACTIVITY;
import static akash.com.mobe.Helper.Config.NOT_APPROVED;
import static akash.com.mobe.Helper.Config.SHARED_PREFERENCE;
import static akash.com.mobe.Helper.Config.SP_DEPARTMENT;
import static akash.com.mobe.Helper.Config.SP_KEY;
import static akash.com.mobe.Helper.EmojiTag.E_ANGRY;
import static akash.com.mobe.Helper.EmojiTag.E_CONF;
import static akash.com.mobe.Helper.EmojiTag.E_DISAP;
import static akash.com.mobe.Helper.EmojiTag.E_HAPPY;
import static akash.com.mobe.Helper.EmojiTag.E_RELAX;
import static akash.com.mobe.Helper.EmojiTag.E_SAD;

public class EmployeeActivity extends AppCompatActivity {

    private MaterialProgressBar progressBar;
    private TextView tv_loading,tv_approve,tv_name,tv_noact;
    private RelativeLayout relativeLayout,mainLayout,emoLayout;
    private CoordinatorLayout coordinatorLayout;
    private ImageView iv_approve;
    private ListView listView;
    private FloatingActionButton fab_setting,fab_share;
    private ImageButton ib_happy,ib_conf,ib_relax,ib_disap,ib_sad,ib_angry,ib_news;
    private Snackbar snacky;

    private String EMP_EMAIL,EMP_FNAME,EMP_LNAME,EMP_DOB,EMP_APPROVE,EMP_DEPARTMENT,MANAGER;

    private List<FeedBack> feedBacks;
    private FeedBackListAdapter adapter;

    private Helper helper = new Helper(EmployeeActivity.this);

    private Context context = this;
    private SharedPreferences preferences;
    private Animation emoOpen,listClicked;

    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        preferences = context.getSharedPreferences(SHARED_PREFERENCE,Context.MODE_PRIVATE);
        EMP_EMAIL = preferences.getString(SP_KEY,"");

        progressBar = (MaterialProgressBar) this.findViewById(R.id.progress);
        tv_loading = (TextView) this.findViewById(R.id.tv_loading);
        tv_approve = (TextView) this.findViewById(R.id.tv_approve);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_noact = (TextView) this.findViewById(R.id.tv_noact);
        relativeLayout = (RelativeLayout) this.findViewById(R.id.layout_hide);
        mainLayout = (RelativeLayout) this.findViewById(R.id.layout_main);
        emoLayout = (RelativeLayout) this.findViewById(R.id.layout_emo);
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.layout_employee);
        iv_approve = (ImageView) this.findViewById(R.id.iv_approve);
        listView = (ListView) this.findViewById(R.id.lv_emp);
        fab_setting = (FloatingActionButton) this.findViewById(R.id.fab_setting);
        fab_share = (FloatingActionButton) this.findViewById(R.id.fab_share);

        ib_angry = (ImageButton) this.findViewById(R.id.emo_angry);
        ib_conf = (ImageButton) this.findViewById(R.id.emo_conf);
        ib_disap = (ImageButton) this.findViewById(R.id.emo_dis);
        ib_happy = (ImageButton) this.findViewById(R.id.emo_happy);
        ib_relax = (ImageButton) this.findViewById(R.id.emo_relax);
        ib_sad = (ImageButton) this.findViewById(R.id.emo_sad);
        ib_news = (ImageButton) this.findViewById(R.id.ib_news);

        feedBacks = new ArrayList<FeedBack>();
        adapter = new FeedBackListAdapter(EmployeeActivity.this,feedBacks);
        listView.setAdapter(adapter);

        listClicked = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        emoOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.open);

        final SwipeDetector swipeDetector = new SwipeDetector();
        coordinatorLayout.setOnTouchListener(swipeDetector);


        snacky = Snackbar
                .make(coordinatorLayout, "Swipe right to left to see Newsfeed.", Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snacky.dismiss();
                    }
                });
        snacky.show();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mainLayout.setVisibility(View.INVISIBLE);

                ib_news.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Newsfeed.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(ACTIVITY_TAG,EMPLOYEE_ACTIVITY);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }
                });

                coordinatorLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(swipeDetector.swipeDetected()) {
                            if(swipeDetector.getAction() == SwipeDetector.Action.RL) {
                                Intent intent = new Intent(context, Newsfeed.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(ACTIVITY_TAG,EMPLOYEE_ACTIVITY);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                                ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                                finish();
                            }else{
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Swipe right to left to see Newsfeed.", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    }
                });

                fab_setting.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("PrivateResource")
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SettingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(ACTIVITY_TAG,EMPLOYEE_ACTIVITY);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                        finish();
                    }
                });

                fab_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        emoLayout.setVisibility(View.VISIBLE);
                        emoLayout.startAnimation(emoOpen);

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        view.startAnimation(listClicked);
                        
                        MANAGER = feedBacks.get(position).getEmail();
                    }
                });

                emoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emoLayout.setVisibility(View.GONE);
                    }
                });

                ib_angry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharePost(EMP_EMAIL,E_ANGRY);
                    }
                });

                ib_conf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharePost(EMP_EMAIL,E_CONF);
                    }
                });

                ib_disap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharePost(EMP_EMAIL,E_DISAP);
                    }
                });

                ib_happy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharePost(EMP_EMAIL,E_HAPPY);
                    }
                });

                ib_relax.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharePost(EMP_EMAIL,E_RELAX);
                    }
                });

                ib_sad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharePost(EMP_EMAIL,E_SAD);
                    }
                });
                
            }
        });

        RunAsync(EMP_EMAIL);
        GetFeedback(EMP_EMAIL);
    }
    
    public void SharePost(final String email,final String emo){

        class React extends AsyncTask<String, Void, String>{

            private HTTPHelper ruc = new HTTPHelper();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);

                System.out.println(" REACT RESPONSE - > "+s);

                if(s != null){
                    switch (s){
                        case API_ERROR: {
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "API Error. Please contact the back office.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            break;
                        }
                        case API_FAIL:{
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Failed to Share ! Pleast try again later.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            emoLayout.setVisibility(View.GONE);
                        }
                        case API_SUCCESS:
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Your mood posted !", Snackbar.LENGTH_LONG);
                            snackbar.show();

                            emoLayout.setVisibility(View.GONE);

                            break;
                        default:
                            SharePost(email,emo);
                            break;
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("email",params[0]);
                data.put("emo",params[1]);

                return ruc.sendPostRequest(API_SHARE_LINK,data);
            }
        }
        React emp = new React();
        emp.execute(email,emo);
        
    }

    public void GetFeedback(final String email){

        class feedbackinfo extends AsyncTask<String, Void, String>{

            private HTTPHelper ruc = new HTTPHelper();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @SuppressLint("PrivateResource")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                switch (s) {
                    case API_ERROR: {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "API Error. Please contact the back office", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    case API_FAIL: {
                        if (!EMP_APPROVE.equals(NOT_APPROVED)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_noact.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                        break;
                    }
                    case API_INSERT_ERROR:{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_loading.setVisibility(View.VISIBLE);
                                tv_loading.setText(context.getResources().getString(R.string.empty));
                            }
                        });
                        break;
                    }
                    default:
                        parseFeedback(s);
                        System.out.println("EMO JSON - > "+s);
                        break;
                }

                if(feedBacks.size() == 0){
                    GetFeedback(email);
                    System.out.print("GOING TO LOAD FEEDBACK AGAIN");
                }

            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("email",params[0]);

                return ruc.sendPostRequest(API_FEEDBACK_LINK,data);
            }
        }

        feedbackinfo feed = new feedbackinfo();
        feed.execute(email);

    }

    public void RunAsync(final String email){

        class UserInfo extends AsyncTask<String, Void, String> {

            private HTTPHelper ruc = new HTTPHelper();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                ShowProgress(true);

            }

            @SuppressLint("PrivateResource")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                ShowProgress(false);

                switch (s){
                    case API_ERROR: {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "API Error. Please contact the back office", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    default:
                        parseJSON(s);
                        break;
                }

                if(tv_name.getText() == context.getResources().getString(R.string.none)){

                    RunAsync(email);

                    System.out.println("SSL HANDSHAKE FAILED. GOING TO RunAsync -> "+s);

                }

            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("email",params[0]);

                return ruc.sendPostRequest(API_INFO_LINK,data);
            }
        }

        UserInfo ru = new UserInfo();
        ru.execute(email);
    }

    private void parseFeedback(String json){

        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray(API_EMP_DATA);

            for(int i = 0 ; i < array.length() ; i++){

                FeedBack feedBack = new FeedBack();

                JSONObject object = array.getJSONObject(i);

                feedBack.setFname(object.getString(API_FNAME));
                feedBack.setLname(object.getString(API_LNAME));
                feedBack.setEmo(object.getString(API_EMO));
                feedBack.setDate(object.getString(API_DATE));
                feedBack.setEmail(object.getString(API_EMAIL));

                feedBacks.add(feedBack);

            }
            adapter.notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void parseJSON(String json){

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray(API_USER_INFO);

            for (int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                EMP_FNAME = object.getString(API_FNAME);
                EMP_LNAME = object.getString(API_LNAME);
                EMP_DOB = object.getString(API_DOB);
                EMP_APPROVE = object.getString(API_APPROVE);
                EMP_DEPARTMENT = object.getString(API_DEPARTMENT);

                final String EMP_NAME = EMP_FNAME + " " + EMP_LNAME;

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(SP_DEPARTMENT, EMP_DEPARTMENT);
                editor.apply();

                if(EMP_APPROVE.equals(NOT_APPROVED)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_name.setText(EMP_NAME);
                            tv_approve.setVisibility(View.VISIBLE);
                            iv_approve.setVisibility(View.VISIBLE);
                            ShowProgress(false);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainLayout.setVisibility(View.VISIBLE);
                            tv_name.setText(EMP_NAME);
                        }
                    });
                }

                System.out.println("JSON RESPONSE FOR EMP - ->"+json);
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
        helper.AlertExit();
    }

}
