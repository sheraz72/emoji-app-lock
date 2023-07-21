package com.sh_developers.emojilocker.activities.fingerprint;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.sh_developers.emojilocker.R;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintActivity extends Activity {


    FingerprintHandler fingerprintHandler;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_unlock);
        setFinishOnTouchOutside(false);

        final String packageName = getIntent().getStringExtra("app");

        fingerprintHandler=new FingerprintHandler();

        fingerprintHandler.startListening(new FingerprintHandler.Callback() {

            @Override
            public void onAuthenticated() {


                finishAndRemoveTask();
            }

            @Override
            public void onFailed() {

            }

            @Override
            public void onError() {

            }

        });
    }
}
