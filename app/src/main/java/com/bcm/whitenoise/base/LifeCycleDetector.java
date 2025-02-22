package com.bcm.whitenoise.base;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

public class LifeCycleDetector {

    // region 변수
    // 인스턴스
    private static LifeCycleDetector instance;
    // 컨텍스트
    private Context mContext;

    // 백그라운드 여부
    private Boolean m_bIsBackground;
    // 라이프 사이클 변경 리스너
    private ActivityLifeCycleListener m_listener;
    // endregion

    private LifeCycleDetector(Context context) {
        mContext = context;
        m_listener = new ActivityLifeCycleListener();
    }

    public static LifeCycleDetector getInstance(Context context) {
        if (instance == null) {
            instance = new LifeCycleDetector(context);
        }
        return instance;
    }

    //region 공개함수
    // 백그라운드 진입 여부 반환
    public boolean isBackground() {
        return m_bIsBackground;
    }

    // 백그라운드 진입 여부 설정
    public void setIsBackground(boolean isBackground) {
        m_bIsBackground = isBackground;
    }

    // 라이프 사이클 체커 설정
    public void setObserver() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(m_listener);
    }

    // 라이프 사이클 체커 제거
    public void removeObserver() {
        ProcessLifecycleOwner.get().getLifecycle().removeObserver(m_listener);
    }
    // endregion

    // 이벤트 리스너 클래스
    class ActivityLifeCycleListener implements LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onMoveToForeground(){
            setIsBackground(false);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onMoveToBackground() {
            setIsBackground(true);
        }
    }
}
