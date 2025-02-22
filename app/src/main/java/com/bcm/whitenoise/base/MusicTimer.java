package com.bcm.whitenoise.base;

import android.content.Context;
import android.util.Log;

public class MusicTimer {

    //region 변수
    // 타이머 인스턴스
    private static MusicTimer instance;
    // 컨텍스트
    private Context mContext;

    // 타이머 스레드
    private Thread m_thrdTimer;
    // 타이머 재생/정지 여부
    private boolean m_bIsRunningTimer, m_bIsPauseTimer;
    // 총 재생 시간
    private int m_nTotalSec;
    // 현재 재생 시간
    private int m_nNowSec;

    // 콜백
    private IMusicTimerCallback mCallback;
    // endregion

    // 콜백 인터페이스
    public interface IMusicTimerCallback {
        void onFinishTimer();

        void onTick(int totalSec, int nowSec);
    }

    private MusicTimer(Context context) {
        mContext = context;
        // 타이머 스레드 1초마다 실행
        m_thrdTimer = new Thread() {
            @Override
            public void run() {
                while (true) {
                    runTick();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        m_thrdTimer.start();
    }

    public static MusicTimer getInstance(Context context) {
        if (instance == null) {
            instance = new MusicTimer(context);
        }
        return instance;
    }

    // region 공개함수
    // 콜백 설정
    public void setCallback(IMusicTimerCallback callback) {
        mCallback = callback;
    }

    // 타이머 시작
    public void startTimer(int hour, int min) {
        m_nTotalSec = hour * 3600 + min * 60;
        m_nNowSec = 0;
        m_bIsRunningTimer = true;
    }

    // 1초마다 콜백 반환
    private void runTick() {
        if (m_bIsRunningTimer && !m_bIsPauseTimer) {
            m_nNowSec++;
            if (m_nNowSec >= m_nTotalSec) {
                m_bIsRunningTimer = false;
                if (mCallback != null) mCallback.onFinishTimer();
            } else {
                if (mCallback != null) mCallback.onTick(m_nTotalSec, m_nNowSec);
            }
        }
    }

    // 타이머 일시정지
    public void pauseTimer() {
        if (m_bIsRunningTimer)
            m_bIsPauseTimer = true;
    }

    // 타이머 재개
    public void resumeTimer() {
        if (m_bIsRunningTimer)
            m_bIsPauseTimer = false;
    }

    // 타이머 정지
    public void stopTimer() {
        m_nNowSec = 0;
        m_bIsRunningTimer = false;
    }

    // 타이머 진행 여부 반환
    public boolean isRunning() {
        return m_bIsRunningTimer;
    }

    // 타이머 클리어
    public void clearTimer() {
        m_nNowSec = 0;
        m_bIsRunningTimer = false;
        m_bIsPauseTimer = false;
        m_thrdTimer.interrupt();
        m_thrdTimer = null;
    }
    // endregion
}
