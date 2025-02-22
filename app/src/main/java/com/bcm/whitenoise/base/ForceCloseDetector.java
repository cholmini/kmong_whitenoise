package com.bcm.whitenoise.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

// 앱 강제종료 체커
public class ForceCloseDetector extends Service {

    // 서비스 종료 이벤트
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Player.getInstance(this).clearMusic();
        LifeCycleDetector.getInstance(this).removeObserver();
        // 타이머 클리어
        MusicTimer.getInstance(this).clearTimer();
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
