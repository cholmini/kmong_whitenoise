package com.bcm.whitenoise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;

import com.bcm.whitenoise.R;
import com.bcm.whitenoise.base.BaseActivity;
import com.bcm.whitenoise.databinding.ActivitySubmainBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class SubMainActivity extends BaseActivity {

    // region 변수
    private ActivitySubmainBinding binding;
    // endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "SubMain 진입");

        // 화면 설정
        binding = ActivitySubmainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 뷰 초기화
        initView();
    }

    // 뷰 초기화
    private void initView() {

        // Gif 이미지 설정
        Glide.with(this)
                .asGif()
                .load(R.raw.gif_submain)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.ivGif);

        // 메인 버튼 애니메이션 설정
        setAnimatingMainBtnText();

        // 화면 클릭 이벤트 연결
        binding.llParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메인으로 이동
                goMain();
            }
        });
    }

    // region UI 애니메이션
    // 메인버튼 애니메이션
    private void setAnimatingMainBtnText() {
        binding.tvMainBtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blink_animation));
    }
    // endregion

    // region 액션
    // 메인으로 이동
    private void goMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.none_out);
        finish();
    }
    // endregion
}
