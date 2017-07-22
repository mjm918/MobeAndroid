package akash.com.mobe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import akash.com.mobe.Helper.Helper;

public class SplashActivity extends AppCompatActivity {

    private Context context = this;

    private Helper helper = new Helper(SplashActivity.this);

    int SPLASH_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(!helper.IsNetworkAvailable()){
            helper.Alert();
        }else{
            new Handler().postDelayed(new Runnable() {
                @SuppressLint("PrivateResource")
                @Override
                public void run() {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                    finish();
                }
            }, SPLASH_TIMEOUT);
        }
    }

    @Override
    public void onBackPressed() {
        helper.AlertExit();
    }
}
