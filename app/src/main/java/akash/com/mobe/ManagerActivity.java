package akash.com.mobe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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

import akash.com.mobe.Constructor.UserInfo;
import akash.com.mobe.Helper.EmpListAdapter;
import akash.com.mobe.Helper.HTTPHelper;
import akash.com.mobe.Helper.Helper;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static akash.com.mobe.Helper.Config.ACTIVITY_TAG;
import static akash.com.mobe.Helper.Config.API_APPROVE;
import static akash.com.mobe.Helper.Config.API_DEPARTMENT;
import static akash.com.mobe.Helper.Config.API_DOB;
import static akash.com.mobe.Helper.Config.API_EMAIL;
import static akash.com.mobe.Helper.Config.API_EMPDATA_LINK;
import static akash.com.mobe.Helper.Config.API_EMP_DATA;
import static akash.com.mobe.Helper.Config.API_ERROR;
import static akash.com.mobe.Helper.Config.API_FAIL;
import static akash.com.mobe.Helper.Config.API_FNAME;
import static akash.com.mobe.Helper.Config.API_INFO_LINK;
import static akash.com.mobe.Helper.Config.API_LNAME;
import static akash.com.mobe.Helper.Config.API_USER_INFO;
import static akash.com.mobe.Helper.Config.MANAGER_ACTIVITY;
import static akash.com.mobe.Helper.Config.NOT_APPROVED;
import static akash.com.mobe.Helper.Config.SHARED_PREFERENCE;
import static akash.com.mobe.Helper.Config.SP_DEPARTMENT;
import static akash.com.mobe.Helper.Config.SP_KEY;

public class ManagerActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout relativeLayout,mainLayout,emoLayout;
    private MaterialProgressBar progressBar;
    private ImageView iv_approve;
    private TextView tv_approve,tv_loading,tv_name,tv_noemp;
    private FloatingActionButton fab_setting;
    private ListView listView;

    private List<UserInfo> userInfos;
    private EmpListAdapter empListAdapter;

    private Helper helper = new Helper(ManagerActivity.this);
    private Context context = this;
    private Animation listClicked,emoOpen;
    private SharedPreferences preferences;
    private String USER_EMAIL,USER_FNAME,USER_LNAME,USER_DOB,USER_APPROVE,USER_DEPARTMENT;
    private String EMP_EMAIL,EMP_FNAME,EMP_LNAME,EMP_DOB,EMP_APPROVE,EMP_DEPARTMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        preferences = context.getSharedPreferences(SHARED_PREFERENCE,Context.MODE_PRIVATE);
        USER_EMAIL = preferences.getString(SP_KEY,"");

        relativeLayout = (RelativeLayout) this.findViewById(R.id.layout_hide);
        emoLayout = (RelativeLayout) this.findViewById(R.id.layout_emo);
        mainLayout = (RelativeLayout) this.findViewById(R.id.layout_main);
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.layout_man);
        progressBar = (MaterialProgressBar) this.findViewById(R.id.progress);

        tv_approve = (TextView) this.findViewById(R.id.tv_approve);
        tv_loading = (TextView) this.findViewById(R.id.tv_loading);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_noemp = (TextView) this.findViewById(R.id.tv_noemp);
        fab_setting = (FloatingActionButton) this.findViewById(R.id.fab_setting);
        iv_approve = (ImageView) this.findViewById(R.id.iv_approve);
        listView = (ListView) this.findViewById(R.id.lv_emp);

        listClicked = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        emoOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.open);

        userInfos = new ArrayList<UserInfo>();

        empListAdapter = new EmpListAdapter(ManagerActivity.this,userInfos);
        listView.setAdapter(empListAdapter);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mainLayout.setVisibility(View.INVISIBLE);

                fab_setting.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("PrivateResource")
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SettingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(ACTIVITY_TAG,MANAGER_ACTIVITY);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                        finish();
                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        view.startAnimation(listClicked);

                        emoLayout.setVisibility(View.VISIBLE);
                        emoLayout.startAnimation(emoOpen);

                        System.out.println("POSITION - > "+userInfos.get(position).getName()+" - Position ->"+position);
                    }
                });

                emoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emoLayout.setVisibility(View.GONE);
                    }
                });
            }
        });

        RunAsync(USER_EMAIL);
        EmployeeData(USER_EMAIL);
    }

    public void EmployeeData(final String email){

        class Data extends AsyncTask<String, Void, String>{

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
                    case API_FAIL:{
                        iv_approve.setVisibility(View.VISIBLE);
                        tv_noemp.setVisibility(View.VISIBLE);
                    }
                    default:
                        parseEmpData(s);
                        break;
                }
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("email",params[0]);

                return ruc.sendPostRequest(API_EMPDATA_LINK,data);
            }
        }
        Data emp = new Data();
        emp.execute(email);
    }

    public void RunAsync(final String email){

        class UserInfo extends AsyncTask<String, Void, String> {

            private HTTPHelper ruc = new HTTPHelper();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @SuppressLint("PrivateResource")
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

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

                    System.out.println("SSL HANDSHAKE FAILED. GOING TO RunAsync");

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

    private void parseEmpData(String json){

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray(API_EMP_DATA);

            for (int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                EMP_FNAME = object.getString(API_FNAME);
                EMP_LNAME = object.getString(API_LNAME);
                EMP_DOB = object.getString(API_DOB);
                EMP_APPROVE = object.getString(API_APPROVE);
                EMP_DEPARTMENT = object.getString(API_DEPARTMENT);
                EMP_EMAIL = object.getString(API_EMAIL);

                String EMP_NAME = EMP_FNAME + " " + EMP_LNAME;

                UserInfo info = new UserInfo();
                info.setName(EMP_NAME);
                info.setApprove(EMP_APPROVE);
                info.setDepartment(EMP_DEPARTMENT);
                info.setEmail(EMP_EMAIL);
                info.setDob(EMP_DOB);

                userInfos.add(info);

                System.out.println("JSON RESPONSE FOR EMP - ->"+json);
            }

            empListAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseJSON(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray(API_USER_INFO);

            for (int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                USER_FNAME = object.getString(API_FNAME);
                USER_LNAME = object.getString(API_LNAME);
                USER_DOB = object.getString(API_DOB);
                USER_APPROVE = object.getString(API_APPROVE);
                USER_DEPARTMENT = object.getString(API_DEPARTMENT);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(SP_DEPARTMENT, USER_DEPARTMENT);
                editor.apply();

                if(USER_APPROVE.equals(NOT_APPROVED)){
                    tv_approve.setVisibility(View.VISIBLE);
                    iv_approve.setVisibility(View.VISIBLE);
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainLayout.setVisibility(View.VISIBLE);
                            tv_name.setText(USER_FNAME+" "+USER_LNAME);
                        }
                    });
                }

                System.out.println("JSON RESPONSE ->"+json);
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
