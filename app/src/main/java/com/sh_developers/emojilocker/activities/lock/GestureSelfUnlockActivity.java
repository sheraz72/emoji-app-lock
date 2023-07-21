package com.sh_developers.emojilocker.activities.lock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.applovin.adview.AppLovinAdView;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.sh_developers.emojilocker.R;
import com.sh_developers.emojilocker.activities.main.MainActivity;
import com.sh_developers.emojilocker.activities.setting.LockSettingActivity;
import com.sh_developers.emojilocker.base.AppConstants;
import com.sh_developers.emojilocker.base.BaseActivity;
import com.sh_developers.emojilocker.db.CommLockInfoManager;
import com.sh_developers.emojilocker.utils.LockPatternUtils;
import com.sh_developers.emojilocker.utils.SpUtil;
import com.sh_developers.emojilocker.utils.SystemBarHelper;
import com.sh_developers.emojilocker.widget.LockPatternView;
import com.sh_developers.emojilocker.widget.LockPatternViewPattern;


import java.util.List;



public class GestureSelfUnlockActivity extends BaseActivity {

    private LockPatternView mLockPatternView;
    private LockPatternUtils mLockPatternUtils;
    private LockPatternViewPattern mPatternViewPattern;
    private int mFailedPatternAttemptsSinceLastTimeout = 0;
    private String actionFrom;
    private String pkgName;
    private CommLockInfoManager mManager;
    private RelativeLayout mTopLayout;


    private TextureView mTextureView;

    private AppLovinAd loadedAd;
    AppLovinInterstitialAdDialog interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        AppLovinSdk.getInstance( getApplicationContext() ).setMediationProvider( "max" );
//        AppLovinSdk.initializeSdk( getApplicationContext(), new AppLovinSdk.SdkInitializationListener() {
//            @Override
//            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
//            {
//                // AppLovin SDK is initialized, start loading ads
//            }
//        } );
        AppLovinSdk.initializeSdk(GestureSelfUnlockActivity.this);
        AppLovinAdView bannerAd = new AppLovinAdView(AppLovinAdSize.BANNER,this);
        ViewGroup rootView = findViewById(android.R.id.content);
        rootView.addView(bannerAd);

        bannerAd.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM) );

        bannerAd.loadNextAd();

        AppLovinSdk.getInstance(this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd ad) {
                loadedAd = ad;
            }

            @Override
            public void failedToReceiveAd(int errorCode) {

            }
        });

        interstitialAd = AppLovinInterstitialAd.create(AppLovinSdk.getInstance(this),this);
                //interstitialAd.showAndRender(loadedAd);

    }

    @NonNull
    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_gesture_self_unlock;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mLockPatternView = findViewById(R.id.unlock_lock_view);
        mTopLayout = findViewById(R.id.top_layout);
        mTextureView = findViewById(R.id.texture_view);
        mTopLayout.setPadding(0, SystemBarHelper.getStatusBarHeight(this), 0, 0);
    }

    @Override
    protected void initData() {
        mManager = new CommLockInfoManager(this);
        pkgName = getIntent().getStringExtra(AppConstants.LOCK_PACKAGE_NAME);
        actionFrom = getIntent().getStringExtra(AppConstants.LOCK_FROM);
        initLockPatternView();


    }

    private void initLockPatternView() {
        mLockPatternUtils = new LockPatternUtils(this);
        mPatternViewPattern = new LockPatternViewPattern(mLockPatternView);
        mPatternViewPattern.setPatternListener(new LockPatternViewPattern.onPatternListener() {
            @Override
            public void onPatternDetected(@NonNull List<LockPatternView.Cell> pattern) {
                if (mLockPatternUtils.checkPattern(pattern)) {
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    if (actionFrom.equals(AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY)) {
                        Intent intent = new Intent(GestureSelfUnlockActivity.this, MainActivity.class);
                        startActivity(intent);
                       interstitialAd.showAndRender(loadedAd);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else if (actionFrom.equals(AppConstants.LOCK_FROM_FINISH)) {
                        mManager.unlockCommApplication(pkgName);
                        finish();
                    } else if (actionFrom.equals(AppConstants.LOCK_FROM_SETTING)) {
                        startActivity(new Intent(GestureSelfUnlockActivity.this, LockSettingActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else if (actionFrom.equals(AppConstants.LOCK_FROM_UNLOCK)) {
                        mManager.setIsUnLockThisApp(pkgName, true);
                        mManager.unlockCommApplication(pkgName);
                        sendBroadcast(new Intent(GestureUnlockActivity.FINISH_UNLOCK_THIS_APP));
                        finish();
                    }
                } else {
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
                        mFailedPatternAttemptsSinceLastTimeout++;
                        int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT - mFailedPatternAttemptsSinceLastTimeout;
                        if (retry >= 0) {
                        }
                    } else {
                    }
                    if (mFailedPatternAttemptsSinceLastTimeout >= 3) {
                        if (SpUtil.getInstance().getBoolean(AppConstants.LOCK_AUTO_RECORD_PIC, false)) {

                        }
                    }
                    if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) { //The number of failures is greater than the maximum number of incorrect attempts before blocking the use

                    } else {
                        mLockPatternView.postDelayed(mClearPatternRunnable, 50);
                    }
                }
            }
        });
        mLockPatternView.setOnPatternListener(mPatternViewPattern);
        mLockPatternView.setTactileFeedbackEnabled(true);
    }

    @Override
    protected void initAction() {

    }



}
