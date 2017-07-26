package akash.com.mobe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import akash.com.mobe.Helper.ConvertToHash;
import akash.com.mobe.Helper.HTTPHelper;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static akash.com.mobe.Helper.Config.ACTIVITY_TAG;
import static akash.com.mobe.Helper.Config.API_DEPT_LINK;
import static akash.com.mobe.Helper.Config.API_ERROR;
import static akash.com.mobe.Helper.Config.API_INSERT_ERROR;
import static akash.com.mobe.Helper.Config.API_PASS_LINK;
import static akash.com.mobe.Helper.Config.API_SUCCESS;
import static akash.com.mobe.Helper.Config.API_SVDEPT_LINK;
import static akash.com.mobe.Helper.Config.EMPLOYEE_ACTIVITY;
import static akash.com.mobe.Helper.Config.MANAGER_ACTIVITY;
import static akash.com.mobe.Helper.Config.NO_FLAG;
import static akash.com.mobe.Helper.Config.SHARED_PREFERENCE;
import static akash.com.mobe.Helper.Config.SP_DEPARTMENT;
import static akash.com.mobe.Helper.Config.SP_KEY;
import static akash.com.mobe.Helper.Config.YES_FLAG;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Context context = this;
    private String PREVIOUS_ACTIVITY,DEPT_NAME,USER_EMAIL,USER_DEPT,USER_PASSWORD;
    private List<String> deptList;

    private CoordinatorLayout coordinatorLayout;
    private RelativeLayout relativeLayout;
    private Spinner spinner;
    private EditText et_pass;
    private Button btn_dept,btn_pass,btn_logout;
    private TextView tv_hello;
    private MaterialProgressBar progressBar;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Bundle bundle = getIntent().getExtras();
        PREVIOUS_ACTIVITY = bundle.getString(ACTIVITY_TAG);

        preferences = context.getSharedPreferences(SHARED_PREFERENCE,Context.MODE_PRIVATE);
        USER_EMAIL = preferences.getString(SP_KEY,"");
        USER_DEPT = preferences.getString(SP_DEPARTMENT,"");

        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.layout_setting);
        relativeLayout = (RelativeLayout) this.findViewById(R.id.layout_hide);
        progressBar = (MaterialProgressBar) this.findViewById(R.id.progress);
        spinner = (Spinner) this.findViewById(R.id.spinner);
        btn_dept = (Button) this.findViewById(R.id.btn_dept);
        btn_pass = (Button) this.findViewById(R.id.btn_pass);
        btn_logout = (Button) this.findViewById(R.id.btn_logout);
        et_pass = (EditText) this.findViewById(R.id.et_pass);
        tv_hello = (TextView) this.findViewById(R.id.tv_hello);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(!USER_DEPT.equals(NO_FLAG)){
                    tv_hello.setVisibility(View.GONE);
                    spinner.setVisibility(View.GONE);
                    btn_dept.setVisibility(View.GONE);
                }

                spinner.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAccent));
                btn_dept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle(context.getString(R.string.confirm_exit_msg));
                        alertDialogBuilder.setMessage(R.string.wardept);
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String department = String.valueOf(spinner.getSelectedItem());
                                SaveDepartment(USER_EMAIL,department);
                            }
                        });
                        alertDialogBuilder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                });
                btn_pass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        USER_PASSWORD = et_pass.getText().toString().trim();

                        if(!validate(USER_PASSWORD)){
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Invalid ! Password is Empty", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            return;
                        }else{
                            try {
                                USER_PASSWORD = ConvertToHash.SHA1(USER_PASSWORD);
                            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            showChangePassDialog(USER_PASSWORD);
                        }
                    }
                });
                btn_logout.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("PrivateResource")
                    @Override
                    public void onClick(View v) {

                        SharedPreferences user = context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);

                        if(user.edit().clear().commit()){
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                            ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                            finish();
                        }
                    }
                });
            }
        });

        spinner.setOnItemSelectedListener(this);

        deptList = new ArrayList<String>();

        if(USER_DEPT.equals(NO_FLAG)){
            RunAsync(YES_FLAG);
        }
    }

    public void ChangePassword(final String email,final String password){

        class PasswordChange extends AsyncTask<String, Void, String> {

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
                    case API_SUCCESS:{
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Password successfully changed", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        et_pass.setText("");
                        break;
                    }
                    case API_INSERT_ERROR:{
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Sorry ! Your request can't be proceed right now. Try again later", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("email",params[0]);
                data.put("pass",params[1]);

                return ruc.sendPostRequest(API_PASS_LINK,data);
            }
        }
        PasswordChange ps = new PasswordChange();
        ps.execute(email,password);
    }

    public void SaveDepartment(final String email,final String department){

        class DepartmentSave extends AsyncTask<String, Void, String> {

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
                    case API_SUCCESS:{
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Department successfully changed", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    case API_INSERT_ERROR:{
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Sorry ! Your request can't be proceed right now. Try again later", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("email",params[0]);
                data.put("department",params[1]);

                return ruc.sendPostRequest(API_SVDEPT_LINK,data);
            }
        }
        DepartmentSave sv = new DepartmentSave();
        sv.execute(email,department);
    }

    public void RunAsync(final String flag){

        class Department extends AsyncTask<String, Void, String> {

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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SettingActivity.this, android.R.layout.simple_spinner_item, deptList);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(dataAdapter);
                    }
                });
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("request",params[0]);

                return ruc.sendPostRequest(API_DEPT_LINK,data);
            }
        }
        Department ru = new Department();
        ru.execute(flag);
    }

    private void parseJSON(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray("dept");

            for (int i = 0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                DEPT_NAME = object.getString("name");
                deptList.add(DEPT_NAME);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("PrivateResource")
    @Override
    public void onBackPressed() {
        switch (PREVIOUS_ACTIVITY){
            case MANAGER_ACTIVITY:
                Intent intent = new Intent(context, ManagerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                finish();
                break;
            case EMPLOYEE_ACTIVITY:
                Intent intentM = new Intent(context, EmployeeActivity.class);
                intentM.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intentM);
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                finish();
                break;
        }
    }

    public void showChangePassDialog(final String password) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText et_email = (EditText) dialogView.findViewById(R.id.et_email);

        dialogBuilder.setTitle(context.getResources().getString(R.string.confirm_exit_msg));
        dialogBuilder.setMessage("Please enter your email address in order to prove that your identity is valid.");
        dialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String email = et_email.getText().toString().trim();

                if(Objects.equals(USER_EMAIL.toLowerCase(), email)){
                    ChangePassword(USER_EMAIL,password);
                }else{
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Invalid ! Email doesn't match", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                System.out.println(USER_EMAIL + " - " + email);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        spinner.setPrompt(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public Boolean validate(String content){
        return !Objects.equals(content, "");
    }
}
