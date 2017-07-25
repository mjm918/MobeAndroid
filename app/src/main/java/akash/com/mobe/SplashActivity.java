package akash.com.mobe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Objects;

import akash.com.mobe.Helper.Helper;

import static akash.com.mobe.Helper.Config.SHARED_PREFERENCE;
import static akash.com.mobe.Helper.Config.SP_KEY;
import static akash.com.mobe.Helper.Config.SP_STATUS;

public class SplashActivity extends AppCompatActivity {

    private Context context = this;
    private SharedPreferences preferences;
    private String USER_EMAIL,USER_STATUS;

    private Helper helper = new Helper(SplashActivity.this);

    int SPLASH_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferences = context.getSharedPreferences(SHARED_PREFERENCE,Context.MODE_PRIVATE);
        USER_EMAIL = preferences.getString(SP_KEY,"");
        USER_STATUS = preferences.getString(SP_STATUS,"");

        if(!helper.IsNetworkAvailable()){
            helper.Alert();
        }else{
            new Handler().postDelayed(new Runnable() {
                @SuppressLint("PrivateResource")
                @Override
                public void run() {

                    if(!validate(USER_EMAIL)){
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                        ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                        finish();
                    }else{
                        switch (USER_STATUS){
                            case "1":
                                Intent intent = new Intent(context, ManagerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                                finish();
                                break;
                            case "0":
                                Intent intentM = new Intent(context, EmployeeActivity.class);
                                intentM.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intentM);
                                ((Activity) context).finish();
                                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                                finish();
                                break;
                        }
                    }
                }
            }, SPLASH_TIMEOUT);
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
