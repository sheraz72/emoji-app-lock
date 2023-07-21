package com.sh_developers.emojilocker.mvp.contract;

import com.sh_developers.emojilocker.base.BasePresenter;
import com.sh_developers.emojilocker.base.BaseView;
import com.sh_developers.emojilocker.model.LockStage;
import com.sh_developers.emojilocker.widget.LockPatternView;

import java.util.List;



public interface GestureCreateContract {

    interface View extends BaseView<MainContract.Presenter> {
        void updateUiStage(LockStage stage);

        void updateChosenPattern(List<LockPatternView.Cell> mChosenPattern);

        void updateLockTip(String text, boolean isToast);

        void setHeaderMessage(int headerMessage);

        void lockPatternViewConfiguration(boolean patternEnabled, LockPatternView.DisplayMode displayMode);

        void Introduction();

        void HelpScreen();

        void ChoiceTooShort();

        void moveToStatusTwo();

        void clearPattern();

        void ConfirmWrong();

        void ChoiceConfirmed();
    }

    interface Presenter extends BasePresenter {

        void updateStage(LockStage stage);

        void onPatternDetected(List<LockPatternView.Cell> pattern, List<LockPatternView.Cell> mChosenPattern, LockStage mUiStage);

        void onDestroy();
    }
}