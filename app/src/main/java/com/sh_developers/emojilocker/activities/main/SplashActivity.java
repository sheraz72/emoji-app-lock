package com.sh_developers.emojilocker.activities.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sh_developers.emojilocker.R;
import com.sh_developers.emojilocker.activities.lock.GestureSelfUnlockActivity;
import com.sh_developers.emojilocker.activities.pwd.CreatePwdActivity;
import com.sh_developers.emojilocker.ads.Ads;
import com.sh_developers.emojilocker.base.AppConstants;
import com.sh_developers.emojilocker.base.BaseActivity;
import com.sh_developers.emojilocker.services.BackgroundManager;
import com.sh_developers.emojilocker.services.LoadAppListService;
import com.sh_developers.emojilocker.services.LockService;
import com.sh_developers.emojilocker.utils.AppUtils;
import com.sh_developers.emojilocker.utils.LockUtil;
import com.sh_developers.emojilocker.utils.SpUtil;
import com.sh_developers.emojilocker.utils.ToastUtil;
import com.sh_developers.emojilocker.widget.DialogPermission;



public class SplashActivity extends BaseActivity {

    private static final int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;
    private static final int RESULT_ACTION_ACCESSIBILITY_SETTINGS = 3;

    private ImageView mImgSplash;
    @Nullable
    private ObjectAnimator animator;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getAds();
    }

    private void getAds() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("ads");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // String app_id = dataSnapshot.child("app_id").getValue().toString();
                String banner = dataSnapshot.child("banner").getValue(String.class);
                String interstitial = dataSnapshot.child("interstitial").getValue(String.class);
                String app_id = dataSnapshot.child("id").getValue(String.class);
                String native_id = dataSnapshot.child("native").getValue(String.class);
                String inter2 = dataSnapshot.child("inter2").getValue(String.class);
                String inter3 = dataSnapshot.child("inter3").getValue(String.class);

//                String native_id = dataSnapshot.child("native_id").getValue().toString();
//                String rewarded_id = dataSnapshot.child("rewarded_id").getValue().toString();
//                String ad_type = dataSnapshot.child("ad_type").getValue().toString();
//                boolean admob_ad = Boolean.parseBoolean(dataSnapshot.child("admob_ad").getValue().toString());
//                Log.d("admob_ad", String.valueOf(admob_ad));
//                Log.d("ad_type", ad_type);
//                Log.d("app_id", app_id);

                Ads.banner = banner;
                Ads.interstitial = interstitial;
                Ads.inter2 = inter2;
                Ads.inter3 = inter3;
                Ads.appId = app_id;
                Ads.nativeAds = native_id;

                // App id changed
                try {
                    ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                    Bundle bundle = ai.metaData;
                    ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", Ads.appId);
                    String apiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                    Log.d("AppID", "The saved id is" + Ads.appId);
                    Log.d("AppID", "The saved id is" + apiKey);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("MainActivity", "Failed to load meta-data, NameNotFound: " + e.getMessage());
                } catch (NullPointerException e) {
                    Log.e("MainActivity", "Failed to load meta-data, NullPointer: " + e.getMessage());
                }

//                Log.d("banner_id", banner);
//                Log.d("interstitial_id", interstitial);
//                Log.d("native_id", native_id);
//                Log.d("rewarded_id", rewarded_id);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        AppUtils.hideStatusBar(getWindow(), true);
        mImgSplash = findViewById(R.id.img_splash);
    }

    @Override
    protected void initData() {
        //startService(new Intent(this, LoadAppListService.class));
        BackgroundManager.getInstance().init(this).startService(LoadAppListService.class);

        //start lock services if  everything is already  setup
        if (SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE, false)) {
            BackgroundManager.getInstance().init(this).startService(LockService.class);
        }

        animator = ObjectAnimator.ofFloat(mImgSplash, "alpha", 0.5f, 1);
        animator.setDuration(1500);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                boolean isFirstLock = SpUtil.getInstance().getBoolean(AppConstants.LOCK_IS_FIRST_LOCK, true);
                if (isFirstLock) {
                    showDialog();
                } else {
                    Intent intent = new Intent(SplashActivity.this, GestureSelfUnlockActivity.class);
                    intent.putExtra(AppConstants.LOCK_PACKAGE_NAME, AppConstants.APP_PACKAGE_NAME);
                    intent.putExtra(AppConstants.LOCK_FROM, AppConstants.LOCK_FROM_LOCK_MAIN_ACITVITY);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }

    private void showDialog() {
        // If you do not have access to view usage rights and the phone exists to view usage this interface
        if (!LockUtil.isStatAccessPermissionSet(SplashActivity.this) && LockUtil.isNoOption(SplashActivity.this)) {
            DialogPermission dialog = new DialogPermission(SplashActivity.this);
            dialog.show();
            dialog.setOnClickListener(new DialogPermission.onClickListener() {
                @Override
                public void onClick() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = null;
                        intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS);
                    }
                }
            });
        } else {
            gotoCreatePwdActivity();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS) {
            if (LockUtil.isStatAccessPermissionSet(SplashActivity.this)) {
                gotoCreatePwdActivity();
            } else {
                ToastUtil.showToast("Permission denied");
                finish();
            }
        }
        if (requestCode == RESULT_ACTION_ACCESSIBILITY_SETTINGS) {
            gotoCreatePwdActivity();
        }
    }

    public boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE = "io.github.subhamtyagi.privacyapplock/com.lzx.lock.service.LockAccessibilityService";
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            //setting not found so your phone is not supported
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessabilityService = mStringColonSplitter.next();
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void gotoCreatePwdActivity() {
        Intent intent2 = new Intent(SplashActivity.this, CreatePwdActivity.class);
        startActivity(intent2);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animator = null;
    }



//    private void setFbTestKeys() {
//        banner_id = "IMG_16_9_APP_INSTALL#" + banner_id;
//        interstitial_id = "IMG_16_9_APP_INSTALL#" + interstitial_id;
//        native_id = "IMG_16_9_APP_INSTALL#" + native_id;
////        facebook_native = "IMG_16_9_APP_INSTALL#" + facebook_native;
//    }



}
