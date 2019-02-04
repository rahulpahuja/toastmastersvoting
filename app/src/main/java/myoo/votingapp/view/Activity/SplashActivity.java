package myoo.votingapp.view.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import myoo.votingapp.R;


public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                Class<?> classs;

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                  //  classs = EnterCodeActivity.class;
                    classs = MainActivity.class;
                } else {
                    classs = LoginActivity.class;
                }



                Intent i = new Intent(SplashActivity.this, classs);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
