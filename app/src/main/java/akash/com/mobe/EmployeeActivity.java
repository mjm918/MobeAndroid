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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import akash.com.mobe.Constructor.UserInfo;
import akash.com.mobe.Helper.HTTPHelper;
import akash.com.mobe.Helper.Helper;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static akash.com.mobe.Helper.Config.ACTIVITY_TAG;
import static akash.com.mobe.Helper.Config.API_APPROVE;
import static akash.com.mobe.Helper.Config.API_DEPARTMENT;
import static akash.com.mobe.Helper.Config.API_DOB;
import static akash.com.mobe.Helper.Config.API_EMAIL;
import static akash.com.mobe.Helper.Config.API_EMO;
import static akash.com.mobe.Helper.Config.API_EMP_DATA;
import static akash.com.mobe.Helper.Config.API_ERROR;
import static akash.com.mobe.Helper.Config.API_FNAME;
import static akash.com.mobe.Helper.Config.API_INFO_LINK;
import static akash.com.mobe.Helper.Config.API_LNAME;
import static akash.com.mobe.Helper.Config.EMPLOYEE_ACTIVITY;
import static akash.com.mobe.Helper.Config.MANAGER_ACTIVITY;
import static akash.com.mobe.Helper.Config.NOT_APPROVED;
import static akash.com.mobe.Helper.Config.SHARED_PREFERENCE;
import static akash.com.mobe.Helper.Config.SP_KEY;

public class EmployeeActivity extends AppCompatActivity {

    private MaterialProgressBar progressBar;
    private TextView tv_loading,tv_approve,tv_name;
    private RelativeLayout layout_hide,layout_main;
    private CoordinatorLayout coordinatorLayout;
    private ImageView iv_approve;
    private ListView listView;
    private FloatingActionButton fab_setting;

    private String EMP_EMAIL,EMP_FNAME,EMP_LNAME,EMP_DOB,EMP_APPROVE,EMP_DEPARTMENT;

    private Helper helper = new Helper(EmployeeActivity.this);

    private Context context = this;
    private SharedPreferences preferences;

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
        layout_hide = (RelativeLayout) this.findViewById(R.id.layout_hide);
        layout_main = (RelativeLayout) this.findViewById(R.id.layout_main);
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.layout_employee);
        iv_approve = (ImageView) this.findViewById(R.id.iv_approve);
        listView = (ListView) this.findViewById(R.id.lv_emp);
        fab_setting = (FloatingActionButton) this.findViewById(R.id.fab_setting);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
            }
        });

        RunAsync(EMP_EMAIL);

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

    private void parseJSON(String json){

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

                final String EMP_NAME = EMP_FNAME + " " + EMP_LNAME;

                if(EMP_APPROVE.equals(NOT_APPROVED)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_approve.setVisibility(View.VISIBLE);
                            iv_approve.setVisibility(View.VISIBLE);
                            ShowProgress(false);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout_main.setVisibility(View.VISIBLE);
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
            layout_hide.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            tv_loading.setVisibility(View.INVISIBLE);
        }else{
            layout_hide.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            tv_loading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        helper.AlertExit();
    }

}
