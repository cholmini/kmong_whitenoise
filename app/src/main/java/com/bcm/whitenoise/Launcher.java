package com.bcm.whitenoise;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.bcm.whitenoise.base.BaseActivity;
import com.bcm.whitenoise.base.LifeCycleDetector;
import com.bcm.whitenoise.base.MusicTimer;
import com.bcm.whitenoise.base.settings.BaseSettings;
import com.bcm.whitenoise.base.settings.SettingsKey;
import com.bcm.whitenoise.constant.AdMobConst;
import com.bcm.whitenoise.model.MusicCategory;
import com.bcm.whitenoise.model.MusicList;
import com.bcm.whitenoise.ui.IntroActivity;
import com.google.android.gms.ads.MobileAds;

public class Launcher extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 앱 구동 횟수 추가
        addLauncherCount();
        // 앱에 필요한 데이터 초기화
        initData();
        // 인트로 화면으로 이동
        goIntro();
    }

    // 앱에 필요한 데이터 초기화
    private void initData() {
        // 로컬 저장소
        BaseSettings.getInstance(mContext);
        // 음악 목록
        MusicList.getInstance();
        // 음악 장르
        MusicCategory.getInstance(mContext);
        // 음악 타이머
        MusicTimer.getInstance(mContext);
        // 라이프 사이클 체커
        LifeCycleDetector.getInstance(mContext).setObserver();
        // 구글 애드몹 초기화
        MobileAds.initialize(this);
    }

    // 인트로 화면으로 이동
    private void goIntro() {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    // 앱 구동 횟수 추가
    private void addLauncherCount() {
        if (AdMobConst.USE_AD_EXPOSE_PAGE) {
            BaseSettings.getInstance(mContext).appLaunchCount += 1;
            BaseSettings.getInstance(mContext).putIntItem(SettingsKey.APP_LAUNCH_COUNT, BaseSettings.getInstance(mContext).appLaunchCount);
        }
    }
}
