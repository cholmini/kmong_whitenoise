package com.bcm.whitenoise.ui;

import static androidx.media3.common.Player.STATE_ENDED;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bcm.whitenoise.R;
import com.bcm.whitenoise.base.BaseActivity;
import com.bcm.whitenoise.base.BaseAlert;
import com.bcm.whitenoise.base.LifeCycleDetector;
import com.bcm.whitenoise.base.MusicTimer;
import com.bcm.whitenoise.base.Player;
import com.bcm.whitenoise.base.Utils;
import com.bcm.whitenoise.base.VerticalMarqueeTextView;
import com.bcm.whitenoise.base.settings.BaseSettings;
import com.bcm.whitenoise.databinding.ActivityDetailBinding;
import com.bcm.whitenoise.model.MusicList;
import com.bcm.whitenoise.model.MusicVO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.time.Duration;

public class DetailActivity extends BaseActivity {

    // region 변수
    // UI 컨트롤 집합
    private ActivityDetailBinding binding;
    private VerticalMarqueeTextView VMTV;

    // 재생중인 음악 정보
    private MusicVO musicVO;
    // 음악 반복 여부
    private boolean m_bIsRepeat;

    // 시간 포맷
    private SimpleDateFormat m_sdfPlayTime = new SimpleDateFormat("mm:ss");
    // 타이머 스레드 (재생시간표시)
    private Thread m_thrdTimer;

    // 애니메이션 페이드아웃
    @SuppressLint("Recycle")
    ObjectAnimator fadeOutControlView;
    // 애니메이션 페이드인
    @SuppressLint("Recycle")
    ObjectAnimator fadeInControlView;

    // 애니메이션 슬라이드업
    @SuppressLint("Recycle")
    ObjectAnimator slideUpTimerView;
    // 애니메이션 슬라이드다운
    @SuppressLint("Recycle")
    ObjectAnimator slideDownTimerView;

    // 타이머 설정 뷰
    private DetailTimerView vTimer;
    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 화면 설정
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // marquee 초기화
        initMarquee();
        // 데이터 초기화
        initData();
        // 화면 초기화
        initView(false);
        // 화면 컨트롤 초기화
        initControls();
        // 재생시간 표시를 위한 타이머 실행
        initTimer();
        // 플레이어에 리스너 연결
        setPlayerListener();
        // 재생 타이머 연결
        setMusicTimerCallback();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // context가 없는 경우는 앱이 메모리에서 날아간 상태임
        if (mContext != null) {
            // 현재 재생중인 음악으로 설정
            initData();
            // 현재 재생중인 음악 정보로 표시
            initView(false);
            if (!MusicTimer.getInstance(mContext).isRunning()) {
                // 타이머 영역 업데이트
                updateAlarm(false, 0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VMTV.stopMarquee();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.none_out, R.anim.bottom_out);

        // 재생시간 타이머 정지
        m_thrdTimer.interrupt();
        m_thrdTimer = null;
        // 플레이어에 설정했던 리스너 해제
        removePlayerListener();
    }

    // Marquee 초기화
    private void initMarquee() {
        VMTV = (VerticalMarqueeTextView) binding.tvTitle;
        VMTV.setMovementMethod(new ScrollingMovementMethod());
        VMTV.setDuration(1);
        VMTV.setPixelYOffSet(3);
    }

    // 데이터 초기화
    private void initData() {
        musicVO = Player.getInstance(mContext).getMusicVO();
    }

    // 화면 초기화
    private void initView(boolean isPrevNext) {
        // 배경
//        binding.ivGif.setImageDrawable(Utils.getImageByString(mContext, musicVO.getImgFileName()));
        Glide.with(this)
                .asGif()
                .load(Utils.getRawIdByString(mContext, musicVO.getBgFileName()))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.ivGif);

        // 노래 제목
//        VMTV.pauseMarquee();
        binding.tvTitle.setText(musicVO.getTitle());
        VMTV.initView();
//        if (VMTV.getLineCount() > 8) {
//            VMTV.resumeMarquee();
//        }

        // 아티스트
        binding.tvComposer.setText(musicVO.getComposer());
        binding.tvComposer.setSelected(true);

        // 음악 총 재생시간
        binding.tvTotalTime.setText(m_sdfPlayTime.format(Integer.parseInt(musicVO.getPlayTimeSec()) * 1000));

        // 음악 재생 Seekbar max값 설정
        binding.sbGauge.setMax(Integer.parseInt(musicVO.getPlayTimeSec()) * 1000);

        // 음악 재생 여부에 따른 화면 업데이트
        updateViewPlay(Player.getInstance(mContext).isPlaying() || isPrevNext);
    }

    // 화면 컨트롤 초기화
    private void initControls() {
        // 닫기
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 공유하기
        binding.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickShare();
            }
        });

        // 알람설정
        binding.ivAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAlarm();
            }
        });

        // 뒤로가기
        binding.llPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPrev();
            }
        });

        // 일시정지
        binding.llPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPause();
            }
        });

        // 다음
        binding.llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickNext();
            }
        });

        // 한곡반복
        binding.llRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRepeat();
            }
        });

        // 게이지 터치 비활성
        binding.sbGauge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    Player.getInstance(mContext).changePlayPoint(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // 커버클릭 이벤트
        binding.clCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeOutControlView.start();
            }
        });

        // 컨트롤러 숨김/표시 애니메이션 설정
        fadeOutControlView = ObjectAnimator.ofFloat(binding.llControls, View.ALPHA, 1f, 0f);
        fadeOutControlView.setDuration(500);
        fadeOutControlView.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.llControls.setVisibility(View.GONE);
            }
        });

        fadeInControlView = ObjectAnimator.ofFloat(binding.llControls, View.ALPHA, 0f, 1f);
        fadeInControlView.setDuration(500);
        fadeInControlView.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                binding.llControls.setVisibility(View.VISIBLE);
            }
        });

        // 배경 클릭 이벤트
        binding.ivGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeInControlView.start();
            }
        });

        // 타이머 숨김/표시 애니메이션 설정
        vTimer = (DetailTimerView) binding.vTimer;
        vTimer.setCallback(new DetailTimerView.ICallbackHandler() {
            @Override
            public void close() {
                slideDownTimerView.start();
            }

            @Override
            public void setTimer() {
                MusicTimer.getInstance(mContext).startTimer(BaseSettings.getInstance(mContext).timerHour, BaseSettings.getInstance(mContext).timerMin);
                slideDownTimerView.start();
                Toast.makeText(mContext, R.string.settimer_set_timer, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void delTimer() {
                MusicTimer.getInstance(mContext).stopTimer();
                updateAlarm(false, 0);
                slideDownTimerView.start();
                Toast.makeText(mContext, R.string.settimer_cancel_timer, Toast.LENGTH_SHORT).show();
            }
        });
        slideUpTimerView = ObjectAnimator.ofFloat(vTimer, View.TRANSLATION_Y, 10000f, 0f);
        slideUpTimerView.setDuration(500);
        slideUpTimerView.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                vTimer.setVisibility(View.VISIBLE);
            }
        });

        slideDownTimerView = ObjectAnimator.ofFloat(vTimer, View.TRANSLATION_Y, 0f, 10000f);
        slideDownTimerView.setDuration(500);
        slideDownTimerView.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                vTimer.setVisibility(View.GONE);
            }
        });
    }

    // 1초마다 재생시간 체크 후 화면에 표시
    private void initTimer() {
        m_thrdTimer = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (mContext != null && !LifeCycleDetector.getInstance(mContext).isBackground()) {
                        binding.tvNowTime.post(new Runnable() {
                            public void run() {
                                // 현재 재생 시간
                                long nowMilliseconds = Player.getInstance(mContext).getNowTime();
                                // 총 재생 시간
                                long totalMilliseconds = Player.getInstance(mContext).getTotalTime();
                                binding.tvNowTime.setText(m_sdfPlayTime.format(nowMilliseconds));

                                Log.d(TAG, "Thread runnding");

                                // 재생률
//                            int nPercent = (int)((double) nowMilliseconds /totalMilliseconds*100.0) * 10;
                                int nPercent = (int) nowMilliseconds;
                                binding.sbGauge.setProgress(nPercent);
                            }
                        });
                    }

                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        m_thrdTimer.start();
    }

    //region 콜백 & 리스너
    private void setMusicTimerCallback() {
        MusicTimer.getInstance(mContext).setCallback(new MusicTimer.IMusicTimerCallback() {
            @Override
            public void onFinishTimer() {
                // 앱이 백그라운드 상태가 아니고 Context가 존재할 때 UI 업데이트
                if (mContext != null && !LifeCycleDetector.getInstance(mContext).isBackground()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 음악 정지 & 화면 업데이트
                            clickPause();
                            // 볼륨 초기화
                            Player.getInstance(mContext).setVolume(1.f);
                            // 팝업 표시
//                            BaseAlert.show(mContext, getString(R.string.alert_finish_play));
                            Toast.makeText(mContext, R.string.settimer_finish_timer, Toast.LENGTH_SHORT).show();
                            // 타이머 영역 업데이트
                            updateAlarm(false, 0);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 음악 정지
                            playMusic(false);
                        }
                    });
                }
            }

            @Override
            public void onTick(int totalSec, int nowSec) {
                // 앱이 백그라운드 상태가 아니고 Context가 존재할 때 UI 업데이트
                if (mContext != null && !LifeCycleDetector.getInstance(mContext).isBackground()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 타이머 영역 업데이트
                            updateAlarm(true, totalSec - nowSec);
                        }
                    });
                }

                // 타이머가 6초 남았을때부터 사운드 크기 줄어듬
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        float nRemainSec = (float) (totalSec - nowSec);
                        if (nRemainSec < 7) {
                            Player.getInstance(mContext).setVolume(nRemainSec/10.f);
                        }
                    }
                });
            }
        });
    }

    // 플레이어에 리스너 연결
    androidx.media3.common.Player.Listener m_listener = new androidx.media3.common.Player.Listener() {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            androidx.media3.common.Player.Listener.super.onPlaybackStateChanged(playbackState);

            if (playbackState == STATE_ENDED) {
                // 재생 종료 후 다음 곡 재생
                if (mContext != null && !LifeCycleDetector.getInstance(mContext).isBackground()) {
                    clickNext();
                } else {
                    playNextMusic();
                }
            }
        }
    };

    // 플레이어에 리스너 연결
    private void setPlayerListener() {
        Player.getInstance(mContext).addListener(m_listener);
    }

    // 플레이어에 리스너 해제
    private void removePlayerListener() {
        Player.getInstance(mContext).removeListener(m_listener);
    }
    // endregion

    // region UI
    // 타이머 영역 업데이트
    private void updateAlarm(boolean isRunning, int remainSec) {
        if (isRunning) {
            String time = "";
            if (remainSec <= 59) {
                time = remainSec + getString(R.string.common_sec);
            } else {
                int nHour = remainSec / 3600;
                int nMin = remainSec % 3600 / 60;
                if (remainSec % 3600 % 60 > 0) {
                    if (nMin == 59) {
                        nHour++;
                        nMin = 0;
                    } else nMin++;
                }

                if (nHour != 0) {
                    time = nHour + ":" + (nMin < 10 ? "0" + nMin : nMin);
                } else {
                    time = nMin + getString(R.string.common_min);
                }
            }
            binding.tvTimer.setText(time);
            binding.tvTimer.setVisibility(View.VISIBLE);
//            binding.ivAlarm.setImageDrawable(getDrawable(R.drawable.ic_circle_alarm_on));
            Glide.with(this)
                    .asGif()
                    .load(R.raw.gif_timer)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(binding.ivAlarm);
        } else {
            binding.tvTimer.setVisibility(View.INVISIBLE);
            binding.ivAlarm.setImageDrawable(getDrawable(R.drawable.ic_circle_alarm_off));
        }
    }

    // 플레이 아이콘 업데이트
    private void updateViewPlay(boolean isPlay) {
        if (isPlay) {
            binding.ivPause.setImageDrawable(getDrawable(R.drawable.ic_pause));
        } else {
            binding.ivPause.setImageDrawable(getDrawable(R.drawable.ic_play));
        }
    }
    // endregion

    // region 액션
    // 공휴하기 클릭
    private void clickShare() {
        Utils.shareWithSns(mContext);
    }

    // 타이머 클릭
    @SuppressLint("UseCompatLoadingForDrawables")
    private void clickAlarm() {
        // 타이머 표시
        vTimer.updateView();
        slideUpTimerView.start();
    }

    // 이전 클릭
    private void clickPrev() {
        // 이전 음악 재생
        playPrevMusic();
        // 재생중인 음악 초기화
        initData();
        // 재생중인 음악 관련 정보 표시
        initView(true);
    }

    // 일시정지 클릭
    private void clickPause() {
        boolean bIsPlaying = Player.getInstance(mContext).isPlaying();
        // 플레이 아이콘 업데이트
        updateViewPlay(!bIsPlaying);
        // 음악 재생/정지
        playMusic(!bIsPlaying);
    }

    // 다음 클릭
    private void clickNext() {
        // 다음 음악 재생
        playNextMusic();
        // 재생중인 음악 초기화
        initData();
        // 재생중인 음악 관련 정보 표시
        initView(true);
    }

    // 한곡반복 클릭
    @SuppressLint("UseCompatLoadingForDrawables")
    private void clickRepeat() {
        m_bIsRepeat = !m_bIsRepeat;
        Player.getInstance(mContext).repeatMusic(m_bIsRepeat);

        if (m_bIsRepeat) {
            binding.ivRepeat.setImageDrawable(getDrawable(R.drawable.ic_repeat_on));
            Toast.makeText(getBaseContext(), getText(R.string.player_repeat_on),
                    Toast.LENGTH_SHORT).show();
        } else {
            binding.ivRepeat.setImageDrawable(getDrawable(R.drawable.ic_repeat_off));
            Toast.makeText(getBaseContext(), getText(R.string.player_repeat_off),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // endregion

    // region 플레이어 컨트롤

    // 음악 재생/정지
    private void playMusic(boolean isPlay) {
        if (isPlay) {
            // 타이머 재개
            MusicTimer.getInstance(mContext).resumeTimer();
            // 음악 재개
            Player.getInstance(mContext).resumeMusic();
        } else {
            // 타이머 정지
            MusicTimer.getInstance(mContext).pauseTimer();
            // 음악 정지
            Player.getInstance(mContext).pauseMusic();
        }
    }

    // 이전곡 재생
    private void playPrevMusic() {
        int nNowIdx = MusicList.getInstance().arrFilteredMusicList.indexOf(Player.getInstance(mContext).getMusicVO());
        nNowIdx--;
        if (nNowIdx < 0)
            nNowIdx = MusicList.getInstance().arrFilteredMusicList.size() - 1;

        MusicVO vo = MusicList.getInstance().arrFilteredMusicList.get(nNowIdx);
        Player.getInstance(mContext).initMusic(vo);
    }

    // 다음곡 재생
    private void playNextMusic() {
        int nNowIdx = MusicList.getInstance().arrFilteredMusicList.indexOf(Player.getInstance(mContext).getMusicVO());
        nNowIdx++;
        if (nNowIdx > MusicList.getInstance().arrFilteredMusicList.size() - 1)
            nNowIdx = 0;

        MusicVO vo = MusicList.getInstance().arrFilteredMusicList.get(nNowIdx);
        Player.getInstance(mContext).initMusic(vo);
    }

    // endregion
}
