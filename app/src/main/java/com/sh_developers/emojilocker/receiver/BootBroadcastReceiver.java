package com.sh_developers.emojilocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.sh_developers.emojilocker.base.AppConstants;
import com.sh_developers.emojilocker.services.BackgroundManager;
import com.sh_developers.emojilocker.services.LoadAppListService;
import com.sh_developers.emojilocker.services.LockService;
import com.sh_developers.emojilocker.utils.LogUtil;
import com.sh_developers.emojilocker.utils.SpUtil;


public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        LogUtil.i("Boot service....");
        //TODO: pie compatable done
       // context.startService(new Intent(context, LoadAppListService.class));
        BackgroundManager.getInstance().init(context).startService(LoadAppListService.class);
        if (SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE, false)) {
            BackgroundManager.getInstance().init(context).startService(LockService.class);
            BackgroundManager.getInstance().init(context).startAlarmManager();
        }
    }
}
