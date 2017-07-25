package akash.com.mobe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import akash.com.mobe.Helper.HTTPHelper;
import akash.com.mobe.Helper.ConvertToHash;
import akash.com.mobe.Helper.Helper;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static akash.com.mobe.Helper.Config.API_ERROR;
import static akash.com.mobe.Helper.Config.API_FAIL;
import static akash.com.mobe.Helper.Config.API_INSERT_ERROR;
import static akash.com.mobe.Helper.Config.API_REG_LINK;
import static akash.com.mobe.Helper.Config.API_SUCCESS;
import static akash.com.mobe.Helper.Config.MOBE_DOMAIN;
import static akash.com.mobe.Helper.Config.SHARED_PREFERENCE;
import static akash.com.mobe.Helper.Config.SP_KEY;
import static akash.com.mobe.Helper.Config.SP_STATUS;

public class RegistrationActivity extends AppCompatActivity {

    private Context context = this;
    private SharedPreferences preferences;
    private Calendar calendar = Calendar.getInstance();

    private MaterialProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private CoordinatorLayout coordinatorLayout;
    private EditText et_fname,et_lname,et_dob,et_email,et_pass;
    private CheckBox cb_manager;
    Button btn_reg,btn_login;

    private String USER_FNAME,USER_LNAME,USER_DOB,USER_EMAIL,USER_PASS,USER_MANAGER;

    private Helper helper = new Helper(RegistrationActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        preferences = context.getSharedPreferences(SHARED_PREFERENCE,Context.MODE_PRIVATE);

        et_fname = (EditText) this.findViewById(R.id.et_fname);
        et_lname = (EditText) this.findViewById(R.id.et_lname);
        et_dob = (EditText) this.findViewById(R.id.et_dob);
        et_email = (EditText) this.findViewById(R.id.et_email);
        et_pass = (EditText) this.findViewById(R.id.et_pass);
        cb_manager = (CheckBox) this.findViewById(R.id.cb_manager);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        btn_reg = (Button) this.findViewById(R.id.btn_reg);
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.layout_reg);
        relativeLayout = (RelativeLayout) this.findViewById(R.id.layout_hide);
        progressBar = (MaterialProgressBar) this.findViewById(R.id.progress);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                };

                btn_login.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("PrivateResource")
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                        finish();
                    }
                });

                et_dob.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            new DatePickerDialog(RegistrationActivity.this, date, calendar
                                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)).show();
                            return true;
                        }
                        return false;
                    }
                });

                btn_reg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        USER_FNAME = et_fname.getText().toString().trim();
                        USER_LNAME = et_lname.getText().toString().trim();
                        USER_DOB = et_dob.getText().toString().trim();
                        USER_EMAIL = et_email.getText().toString().trim();
                        USER_PASS = et_pass.getText().toString().trim();

                        if(cb_manager.isChecked()){
                            USER_MANAGER = "1";
                        }else{
                            USER_MANAGER = "0";
                        }

                        if(!validate(USER_FNAME)){
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Invalid ! First Name is Empty", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }
                        if(!validate(USER_LNAME)){
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Invalid ! Last Name is Empty", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }
                        if(!validate(USER_DOB)){
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Invalid ! Date of Birth is Empty", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }
                        if(!USER_EMAIL.contains(MOBE_DOMAIN)){
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Invalid ! Please Use Your Company Email Address", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }
                        if(!validate(USER_EMAIL)){
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Invalid ! Email Address is Empty", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }
                        if(!validate(USER_PASS)){
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Invalid ! Password is Empty", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }else{
                            try {
                                USER_PASS = ConvertToHash.SHA1(USER_PASS);
                            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        RunAsync(USER_FNAME,USER_LNAME,USER_MANAGER,USER_DOB,USER_EMAIL,USER_PASS);
                    }
                });
            }
        });
    }

    public void RunAsync(String fname, String lname, String status, String dob, String email, String pass){

        class RegisterUser extends AsyncTask<String, Void, String> {

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
                switch (s) {
                    case API_SUCCESS:
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(SP_KEY, USER_EMAIL);
                        editor.putString(SP_STATUS, USER_MANAGER);
                        editor.apply();

                        if (USER_MANAGER.equals("1")) {
                            Intent intent = new Intent(context, ManagerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                            ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                            finish();
                        }
                        if (USER_MANAGER.equals("0")) {
                            Intent intent = new Intent(context, EmployeeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                            ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                            finish();
                        }

                        break;
                    case API_FAIL: {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Email already exist. Try another email", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    case API_INSERT_ERROR: {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Something went wrong. Please try again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    case API_ERROR: {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "API Error. Please contact the back office", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("fname",params[0]);
                data.put("lname",params[1]);
                data.put("status",params[2]);
                data.put("dob",params[3]);
                data.put("email",params[4]);
                data.put("password",params[5]);

                return ruc.sendPostRequest(API_REG_LINK,data);
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute(fname,lname,status,dob,email,pass);
    }

    public Boolean validate(String content){
        return !Objects.equals(content, "");
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

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_dob.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onBackPressed() {
        helper.AlertExit();
    }
}
