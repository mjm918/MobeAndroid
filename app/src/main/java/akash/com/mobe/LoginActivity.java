package akash.com.mobe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;

import akash.com.mobe.Helper.HTTPHelper;
import akash.com.mobe.Helper.HashToSHA1;
import akash.com.mobe.Helper.Helper;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static akash.com.mobe.Helper.Config.API_ERROR;
import static akash.com.mobe.Helper.Config.API_FAIL;
import static akash.com.mobe.Helper.Config.API_LOGIN_LINK;
import static akash.com.mobe.Helper.Config.API_SUCCESS;
import static akash.com.mobe.Helper.Config.API_isMANAGER;
import static akash.com.mobe.Helper.Config.SHARED_PREFERENCE;
import static akash.com.mobe.Helper.Config.SP_KEY;
import static akash.com.mobe.Helper.Config.SP_STATUS;

public class LoginActivity extends AppCompatActivity {

    private Helper helper = new Helper(LoginActivity.this);

    private Context context = this;
    private SharedPreferences preferences;
    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout relativeLayout;
    private MaterialProgressBar progressBar;

    private EditText et_id,et_pass;
    Button btn_login,btn_reg;

    private String USER_EMAIL,USER_PASSWORD,USER_MANAGER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = context.getSharedPreferences(SHARED_PREFERENCE,Context.MODE_PRIVATE);

        et_id = (EditText) this.findViewById(R.id.et_id);
        et_pass = (EditText) this.findViewById(R.id.et_pass);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        btn_reg = (Button) this.findViewById(R.id.btn_reg);
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.layout_login);
        relativeLayout = (RelativeLayout) this.findViewById(R.id.layout_hide);
        progressBar = (MaterialProgressBar) this.findViewById(R.id.progress);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        USER_EMAIL = et_id.getText().toString().toUpperCase().trim();
                        USER_PASSWORD = et_pass.getText().toString().trim();

                        if(!validate(USER_EMAIL)){
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Invalid ! Email is Empty", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }
                        if(!validate(USER_PASSWORD)){
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Invalid ! Password is Empty", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }else{
                            try {
                                USER_PASSWORD = HashToSHA1.SHA1(USER_PASSWORD);
                            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        RunAsync(USER_EMAIL,USER_PASSWORD);
                    }
                });
                btn_reg.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("PrivateResource")
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, RegistrationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                        finish();
                    }
                });
            }
        });
    }

    public void RunAsync(final String user, String pass){

        class LoginUser extends AsyncTask<String, Void, String> {

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
                    case API_SUCCESS:
                        USER_MANAGER = "0";
                        SharedPreferences.Editor editorNo = preferences.edit();
                        editorNo.putString(SP_KEY, USER_EMAIL);
                        editorNo.putString(SP_STATUS, USER_MANAGER);
                        editorNo.apply();

                        Intent intent = new Intent(context, EmployeeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                        finish();

                        break;
                    case API_isMANAGER: {
                        USER_MANAGER = "1";
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(SP_KEY, USER_EMAIL);
                        editor.putString(SP_STATUS, USER_MANAGER);
                        editor.apply();

                        Intent intentM = new Intent(context, ManagerActivity.class);
                        intentM.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intentM);
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                        finish();

                        break;
                    }
                    case API_FAIL: {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Login failed. Please Email & Password", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    case API_ERROR: {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "API Error. Please contact back office", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("username",params[0]);
                data.put("password",params[1]);

                return ruc.sendPostRequest(API_LOGIN_LINK,data);
            }
        }

        LoginUser ru = new LoginUser();
        ru.execute(user,pass);
    }

    public void ShowProgress(boolean command){
        if(!command){
            relativeLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }else{
            relativeLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public Boolean validate(String content){
        return !Objects.equals(content, "");
    }

    @Override
    public void onBackPressed() {
        helper.AlertExit();
    }
}
