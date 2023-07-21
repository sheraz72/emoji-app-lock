package com.sh_developers.emojilocker.mvp.contract;

import android.content.Context;

import com.sh_developers.emojilocker.base.BasePresenter;
import com.sh_developers.emojilocker.base.BaseView;
import com.sh_developers.emojilocker.model.CommLockInfo;
import com.sh_developers.emojilocker.mvp.p.LockMainPresenter;

import java.util.List;



public interface LockMainContract {
    interface View extends BaseView<Presenter> {

        void loadAppInfoSuccess(List<CommLockInfo> list);
    }

    interface Presenter extends BasePresenter {
        void loadAppInfo(Context context);

        void searchAppInfo(String search, LockMainPresenter.ISearchResultListener listener);

        void onDestroy();
    }
}
