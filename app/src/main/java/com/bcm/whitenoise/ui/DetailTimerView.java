package com.bcm.whitenoise.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bcm.whitenoise.R;
import com.bcm.whitenoise.base.MusicTimer;
import com.bcm.whitenoise.base.settings.BaseSettings;
import com.bcm.whitenoise.base.settings.SettingsKey;

public class DetailTimerView extends LinearLayout {

    // region 변수
    private Context mContext;

    // 상단 영역
    // 닫기 버튼
    private ImageView ivClose;

    // 타이머 영역
    // 현재 타이머 타이틀
    private TextView tvNowTimerTitle;
    // 현재 타이머 시간
    private TextView tvNowTimerText;

    // 타이머 설명
    private TextView tvTimerDesc;
    // 타임 피커
    private NumberPicker pickerHour;
    private NumberPicker pickerMin;

    // 하단 영역
    // 새 타이머 설정 버튼
    private LinearLayout llConfirm;
    // 새 타이머 취소 버튼
    private LinearLayout llCancel;

    // 클릭 콜백
    private ICallbackHandler mCallback;
    // endregion

    // region 인터페이스
    // 클릭 콜백 인터페이스
    public interface ICallbackHandler {
        void close();
        void setTimer();
        void delTimer();
    };
    // endregion

    // region 생성자
    public DetailTimerView(Context context) {
        super(context);
        mContext = context;
    }

    public DetailTimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        initControls();
    }
    // endregion

    // UI 컨트롤 초기화
    public void initControls() {
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.view_timer, this, false);

        ivClose = (ImageView) v.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickClose();
            }
        });

        tvNowTimerTitle = (TextView) v.findViewById(R.id.tvNowTimerTitle);
        tvNowTimerText = (TextView) v.findViewById(R.id.tvNowTimerText);

        tvTimerDesc = (TextView) v.findViewById(R.id.tvTimerDesc);
        pickerHour = (NumberPicker) v.findViewById(R.id.pickerHour);
        pickerMin = (NumberPicker) v.findViewById(R.id.pickerMin);
        initPicker();

        llConfirm = (LinearLayout) v.findViewById(R.id.llConfirm);
        llConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickConfirm();
            }
        });
        llCancel = (LinearLayout) v.findViewById(R.id.llCancel);
        llCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCancel();
            }
        });

        addView(v);
    }

    // 타임피커 설정
    private void initPicker() {
        pickerHour.setMaxValue(8);
        pickerHour.setMinValue(0);
        pickerHour.setValue(BaseSettings.getInstance(mContext).timerHour);
        pickerHour.setWrapSelectorWheel(false);

        pickerMin.setDisplayedValues(new String[]{"0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"});
        pickerMin.setMaxValue(11);
        pickerMin.setMinValue(0);
        pickerMin.setValue(BaseSettings.getInstance(mContext).timerMin/5);
        pickerMin.setWrapSelectorWheel(false);
    }

    //region 액션
    // 닫기 클릭
    private void clickClose() {
        if (mCallback != null) mCallback.close();
    }

    // 타이머 설정 클릭
    private void clickConfirm() {
        int nHour = pickerHour.getValue();
        int nMin = pickerMin.getValue() * 5;

        BaseSettings.getInstance(mContext).timerHour = nHour;
        BaseSettings.getInstance(mContext).putIntItem(SettingsKey.TIMER_HOUR, nHour);
        BaseSettings.getInstance(mContext).timerMin = nMin;
        BaseSettings.getInstance(mContext).putIntItem(SettingsKey.TIMER_MIN, nMin);

        if (mCallback != null) mCallback.setTimer();
    }

    // 타이머 종료 클릭
    private void clickCancel() {
        if (mCallback != null) mCallback.delTimer();
    }
    //endregion

    // region 공개 함수

    // 버튼 클릭 이벤트 콜백 설정
    public void setCallback(ICallbackHandler callback) {
        mCallback = callback;
    }

    // 재생여부에 따른 화면 업데이트
    public void updateView() {
        if (MusicTimer.getInstance(mContext).isRunning()) {
            tvNowTimerTitle.setVisibility(View.VISIBLE);
            tvNowTimerText.setVisibility(View.VISIBLE);
            int nHour = BaseSettings.getInstance(mContext).timerHour;
            int nMin = BaseSettings.getInstance(mContext).timerMin;
            String text = nHour > 0 ? nHour + mContext.getString(R.string.common_min) + " " : "";
            text += nMin + mContext.getString(R.string.common_min);
            tvNowTimerText.setText(text);

            llCancel.setVisibility(View.VISIBLE);
        } else {
            tvNowTimerTitle.setVisibility(View.INVISIBLE);
            tvNowTimerText.setVisibility(View.INVISIBLE);

            llCancel.setVisibility(View.INVISIBLE);
        }
    }
    //endregion
}
