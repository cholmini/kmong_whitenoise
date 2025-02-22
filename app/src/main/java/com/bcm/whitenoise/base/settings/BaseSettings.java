package com.bcm.whitenoise.base.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BaseSettings {

    //region 변수
    // 인스턴스
    public static BaseSettings instance = null;
    private Context mContext;

    // Android 내부저장소
    private static Editor editor;
    private static SharedPreferences preferences;

    // 타이머 설정 시간
    public int timerHour;
    public int timerMin;

    // 마지막 음악 파일명
    public String lastPlayFileName;
    // 마지막 음악의 재생 시간
    public String lastPlayPlayTime;

    // 앱 구동 횟수
    public int appLaunchCount;
    // 음악 플레이 횟수
    public int musicPlayCount;
    //endregion

    // 설정 값은 항상 동기화 되어 있어야 하기 때문에 모든 세팅스는 어디에서 사용되고 변경되든지 동일한 객체를 사용하도록 한다.
    public static synchronized BaseSettings getInstance(Context context) {
        if (null == instance
                || null == preferences
                || null == editor) {
            instance = new BaseSettings(context);
        }

        return instance;
    }

    // 생성자
    public BaseSettings(Context context) {
        if (context == null)
            return;

        mContext = context.getApplicationContext();
        preferences = mContext.getSharedPreferences("WhiteNoiseV1", Activity.MODE_PRIVATE);
        editor = preferences.edit();
        loadSettings();
    }

    // 로컬에 저장된 값 불러오기
    private void loadSettings() {
        timerHour = getIntItem(SettingsKey.TIMER_HOUR, 0);
        timerMin = getIntItem(SettingsKey.TIMER_MIN, 5);

        lastPlayFileName = getStringItem(SettingsKey.LAST_PLAY_FILE_NAME, "");
        lastPlayPlayTime = getStringItem(SettingsKey.LAST_PLAY_PLAY_TIME, "0");

        appLaunchCount = getIntItem(SettingsKey.APP_LAUNCH_COUNT, 0);
        musicPlayCount = getIntItem(SettingsKey.MUSIC_PLAY_COUNT, 0);
    }

    //region public 함수
    // 아이템을 삭제한다.
    public void removeItem(String key) {
        if (editor == null)
            return;

        editor.remove(key);
        editor.commit();
    }

    // String 형식의 값 저장
    public void putStringItem(String key, String value) {
        if (editor == null){
            return;
        }

        editor.putString(key, value);
        editor.commit();
    }

    // String 형식의 저장된 값 조회
    public String getStringItem(String key, String defaultValue) {
        if (preferences == null)
            return defaultValue;

        return preferences.getString(key, defaultValue);
    }

    // Boolean 형식의 값 저장
    public void putBooleanItem(String key, boolean value) {
        if (editor == null)
            return;

        editor.putBoolean(key, value);
        editor.commit();
    }

    // Boolean 형식의 값 조회
    public boolean getBooleanItem(String key, Boolean defaultValue) {

        //preferences가 Null인 경우가 발생하므로 null인경우는 defaultValue를 return한다.
        //다른 Activity로 들어가면 InterfaceSettings가 재할당될 것임.
        if (preferences == null)
            return defaultValue;

        return preferences.getBoolean(key, defaultValue);
    }

    // Int 형식의 값 저장
    public void putIntItem(String key, int value) {
        if (editor == null)
            return;

        editor.putInt(key, value);
        editor.commit();
    }

    // Int 형식의 값 조회
    public int getIntItem(String key, int defaultValue) {
        if (preferences == null)
            return defaultValue;

        return preferences.getInt(key, defaultValue);
    }
    //endregion
}
