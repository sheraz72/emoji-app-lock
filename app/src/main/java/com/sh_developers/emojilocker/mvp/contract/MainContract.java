package com.sh_developers.emojilocker.mvp.contract;

import android.content.Context;

import com.sh_developers.emojilocker.base.BasePresenter;
import com.sh_developers.emojilocker.base.BaseView;
import com.sh_developers.emojilocker.model.CommLockInfo;

import java.util.List;



public interface MainContract {
    interface View extends BaseView<Presenter> {
        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context, boolean isSort);

        void loadLockAppInfo(Context context);

        void onDestroy();
    }
}
