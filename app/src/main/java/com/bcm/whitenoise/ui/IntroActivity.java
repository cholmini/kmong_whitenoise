package com.bcm.whitenoise.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.bcm.whitenoise.R;
import com.bcm.whitenoise.base.BaseActivity;
import com.bcm.whitenoise.databinding.ActivityIntroBinding;

public class IntroActivity extends BaseActivity {

    // region 변수
    private ActivityIntroBinding binding;
    // endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 화면 설정
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뷰 초기화
        initView();

        // 1초 후 서브메인(타이틀)화면으로 이동
        // 딜레이시간 조정이 필요한 경우 nMilliSecDelay 값을 원하는 정수값으로 변경 ex. 1.5초는 nMilliSecDelay을 1500으로 설정
        int nMilliSecDelay = 1000;
        setDelayForMove(nMilliSecDelay);
    }

    // 뷰 초기화
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView() {
        binding.ivLogo.setImageDrawable(getDrawable(R.drawable.img_logo));
    }

    // region 액션
    // 딜레이 후 화면이동
    private void setDelayForMove(int _nMilliSecDelay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goSubMain();
            }
        }, _nMilliSecDelay);
    }

    // 서브메인(타이틀) 화면으로 이동
    private void goSubMain() {
        Intent intent = new Intent(this, SubMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.none_out);
        finish();
    }
    // endregion
}
