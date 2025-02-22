package com.bcm.whitenoise.ui;

import static androidx.media3.common.Player.STATE_ENDED;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bcm.whitenoise.R;
import com.bcm.whitenoise.base.BaseActivity;
import com.bcm.whitenoise.base.BaseAlert;
import com.bcm.whitenoise.base.ForceCloseDetector;
import com.bcm.whitenoise.base.LifeCycleDetector;
import com.bcm.whitenoise.base.MusicTimer;
import com.bcm.whitenoise.base.Player;
import com.bcm.whitenoise.base.Utils;
import com.bcm.whitenoise.base.settings.BaseSettings;
import com.bcm.whitenoise.base.settings.SettingsKey;
import com.bcm.whitenoise.constant.AdMobConst;
import com.bcm.whitenoise.constant.MusicConst;
import com.bcm.whitenoise.databinding.ActivityMainBinding;
import com.bcm.whitenoise.model.MusicCategory;
import com.bcm.whitenoise.model.MusicCategoryVO;
import com.bcm.whitenoise.model.MusicList;
import com.bcm.whitenoise.model.MusicVO;
import com.bcm.whitenoise.ui.adapter.MainCategoryAdapter;
import com.bcm.whitenoise.ui.adapter.MainContentAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class MainActivity extends BaseActivity {

    // region 변수
    // UI 컨트롤 집합
    private ActivityMainBinding binding;

    // 장르 목록 adapter
    private MainCategoryAdapter adapterMainCategory;
    // 재생 목록 adapter
    private MainContentAdapter adapterMainContent;

    // 장르 목록
    private ArrayList<MusicCategoryVO> arrMusicCategory = new ArrayList<>();

    // 타이머 스레드 (재생상태표시)
    private Thread m_thrdTimer;

    // 상단 카테고리 표시 기점
    private int m_nScrollYForCategoryShow;
    private int m_nScrollYForCategoryHide;
    // 애니메이션 페이드아웃
    @SuppressLint("Recycle")
    ObjectAnimator fadeOutCategoryHeader;
    // 애니메이션 페이드인
    @SuppressLint("Recycle")
    ObjectAnimator fadeInCategoryHeader;

    // 선택된 음악 인덱스
    private int m_nSelectedMusicIdx = -1;
    // endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 화면 설정
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 띠배너 크기 조정
        setAdMobSize();
        // 데이터 초기화
        initData();
        // 뷰 초기화
        initView();
        // 앱 강제종료 감지 서비스 실행
        startForceCloseDetector();
        // 백버튼 종료시 마지막 재생정보 표시
        initLastSelectedMusic();
        // 전면광고 노출 체크
        checkShowAdMobPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // context가 없는 경우는 앱이 메모리에서 날아간 상태임
        if (mContext != null) {
            // 재생 타이머 콜백 설정
            setMusicTimerCallback();
            // 플레이어에 리스너 연결
            setPlayerListener();
            // 현재 재생 상태에 따른 화면 업데이트
            updateView(false);
            // 1초마다 재생시간 체크 후 화면에 표시
            initTimer();
            // 애드몹 표시
            loadAdMob();
        }
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if ((now - lBackPressedTime) < 2000) {
            BaseSettings.getInstance(mContext).putStringItem(SettingsKey.LAST_PLAY_FILE_NAME, Player.getInstance(mContext).getMusicVO().getMusicFileName());
            BaseSettings.getInstance(mContext).putStringItem(SettingsKey.LAST_PLAY_PLAY_TIME, Player.getInstance(mContext).getNowTime() + "");
            finish();
        } else {
            lBackPressedTime = now;
            Toast.makeText(getBaseContext(), getText(R.string.main_back_press),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 재생시간 타이머 정지
        m_thrdTimer.interrupt();
        m_thrdTimer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 플레이어에 리스너 제거
        removePlayerListener();
        // 플레이어 초기화
        Player.getInstance(mContext).clearMusic();
        // 라이프 사이클 체커 제거
        LifeCycleDetector.getInstance(this).removeObserver();
        // 타이머 클리어
        MusicTimer.getInstance(mContext).clearTimer();
    }

    // 데이터 초기화
    private void initData() {
        // 장르 목록 생성
        for (int i = 0; i < MusicCategory.getInstance(mContext).arrMusicCategory.size(); i++) {
            HashMap<MusicConst.CategoryListKey, String> object = MusicCategory.getInstance(mContext).arrMusicCategory.get(i);
            MusicCategoryVO vo = new MusicCategoryVO(object.get(MusicConst.CategoryListKey.Type), object.get(MusicConst.CategoryListKey.Title), object.get(MusicConst.CategoryListKey.IsSelected).equals("1"));
            arrMusicCategory.add(vo);
        }

        m_nScrollYForCategoryShow = Utils.ConvertDPtoPX(mContext, 120);
        m_nScrollYForCategoryHide = Utils.ConvertDPtoPX(mContext, 82);
    }

    // 뷰 초기화
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView() {
        // 장르 목록에 데이터 연결
        adapterMainCategory = new MainCategoryAdapter(mContext, arrMusicCategory, new MainCategoryAdapter.ICallbackHandler() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClickItem(int position) {
                // 장르 클릭 이벤트

                // 선택된 장르 플래그 설정
                for (int i = 0; i < arrMusicCategory.size(); i++) {
                    MusicCategoryVO vo = arrMusicCategory.get(i);
                    if (i == position) vo.setSelected(true);
                    else vo.setSelected(false);
                }

                // 장르 목록 화면 갱신
                adapterMainCategory.notifyDataSetChanged();

                // 재생 목록 갱신
                clickCategory(position);
            }
        });
        binding.rvCategory.setAdapter(adapterMainCategory);
        binding.rvCategoryHeader.setAdapter(adapterMainCategory);

        // 재생 목록 데이터 설정
        adapterMainContent = new MainContentAdapter(mContext, new MainContentAdapter.ICallbackHandler() {
            @Override
            public void onClickItem(int position) {
                // 재생 목록 클릭 이벤트

                // 보상형 광고 노출 여부 체크
                if (!checkShowAdMobReward()) {
                    // 선택된 음악 표시 및 플레이
                    clickMusic(position);
                } else {
                    m_nSelectedMusicIdx = position;
                    // 일시정지 표시
                    updateViewPlay(false);
                    // 음악 정지
                    playMusic(false);
                }
            }
        });
        binding.rvContent.setAdapter(adapterMainContent);

        // 헤더 표시 애니메이션 초기화
        initCategoryHeader();

        // 스크롤뷰 이벤트 추가
        binding.scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (binding.rvCategoryHeader.getVisibility() != View.VISIBLE && scrollY > m_nScrollYForCategoryShow) {
                    fadeInCategoryHeader.start();
                } else if (binding.rvCategoryHeader.getVisibility() == View.VISIBLE  && scrollY < m_nScrollYForCategoryHide){
//                    fadeOutCategoryHeader.start();
                    binding.rvCategoryHeader.setVisibility(View.INVISIBLE);
                }
            }
        });

        // 선택된 음악 표시 영역 클릭 액션 초기화
        initSelectedMusicViewAction();
        // 선택된 음악 표시 영연 화면 초기화
        initSelectedMusicView();
    }

    // 선택된 음악 표시 영역 클릭 액션 연결
    private void initSelectedMusicViewAction() {

        // 재생중인 음악 일시정지
        binding.llPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPause();
            }
        });
        // 재생중인 음악 종료
        binding.llSelectedClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCloseSelectedView();
            }
        });

        // 상세화면으로 진입
        binding.llSelectedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDetail();
            }
        });

        // 게이지 터치 비활성
        binding.sbGauge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // 로딩 색상 변경
        binding.indicatorLoading.setIndicatorColor(getColor(R.color.white));
    }

    // 선택된 음악 표시 영연 화면 초기화
    private void initSelectedMusicView() {
        // 선택된 음악 표시 영역 숨김
        binding.llSelectedMusic.setVisibility(View.GONE);
        // 선택된 음악 표시 영역의 플레이 아이콘 pause로 초기화
        updateViewPlay(true);
    }

    // 1초마다 재생시간 체크 후 화면에 표시
    private void initTimer() {
        m_thrdTimer = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (mContext != null && !LifeCycleDetector.getInstance(mContext).isBackground()
                            && binding.llSelectedMusic.getVisibility() == View.VISIBLE) {
                        binding.sbGauge.post(new Runnable() {
                            public void run() {
                                // 현재 재생 시간
                                long nowMilliseconds = Player.getInstance(mContext).getNowTime();
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

    private void initCategoryHeader() {
        // 컨트롤러 숨김/표시 애니메이션 설정
        fadeOutCategoryHeader = ObjectAnimator.ofFloat(binding.rvCategoryHeader, View.ALPHA, 1f, 0f);
        fadeOutCategoryHeader.setDuration(500);
        fadeOutCategoryHeader.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.rvCategoryHeader.setVisibility(View.INVISIBLE);
            }
        });

        fadeInCategoryHeader = ObjectAnimator.ofFloat(binding.rvCategoryHeader, View.ALPHA, 0f, 1f);
        fadeInCategoryHeader.setDuration(500);
        fadeInCategoryHeader.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                binding.rvCategoryHeader.setVisibility(View.VISIBLE);
            }
        });
    }

    // 마지막 재생음악 정보 조회
    private void initLastSelectedMusic() {
        String strFileName = BaseSettings.getInstance(mContext).getStringItem(SettingsKey.LAST_PLAY_FILE_NAME, "");
        String strPlaytime = BaseSettings.getInstance(mContext).getStringItem(SettingsKey.LAST_PLAY_PLAY_TIME,"0");

        if (!strFileName.isEmpty()) {
            MusicVO vo = (MusicVO) MusicList.getInstance().arrMusicList.stream().filter(item -> item.getMusicFileName().equals(strFileName)).toArray()[0];
            // 음악 재생
            Player.getInstance(mContext).prepareMusic(vo);
            // 아래 코드가 안먹힘
//            Player.getInstance(mContext).changePlayPoint(Integer.parseInt(strPlaytime));
            // 선택된 음악 표시영역의 재생 정보 업데이트
            updateSelectedMusicView(vo);
            // 선택된 음악 표시영역의 재생 아이콘 변경
            updateViewPlay(false);
            // 선택된 음악 표시영역의 재생바 상태 업데이트
            updateViewGauge();
            // 볼륨 초기화
            Player.getInstance(mContext).setVolume(1.f);
        }


        BaseSettings.getInstance(mContext).putStringItem(SettingsKey.LAST_PLAY_FILE_NAME, "");
        BaseSettings.getInstance(mContext).putStringItem(SettingsKey.LAST_PLAY_PLAY_TIME, "");
    }

    // region 콜백 & 리스너
    // 앱 강제종료 감지 서비스 실행
    private void startForceCloseDetector() {
        startService(new Intent(this, ForceCloseDetector.class));
    }

    // 재생 타이머 콜백 설정
    private void setMusicTimerCallback() {
        MusicTimer.getInstance(mContext).setCallback(new MusicTimer.IMusicTimerCallback() {
            @Override
            public void onFinishTimer() {
                // 앱이 백그라운드 상태가 아니고 Context가 존재할 때 UI 업데이트
                if (mContext != null && !LifeCycleDetector.getInstance(mContext).isBackground()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 음악 정지
                            clickPause();
                            // 볼륨 초기화
                            Player.getInstance(mContext).setVolume(1.f);
                            // 팝업띄우기
//                            BaseAlert.show(mContext, getString(R.string.alert_finish_play));
                            Toast.makeText(mContext, R.string.settimer_finish_timer, Toast.LENGTH_SHORT).show();
                            // 현재 재생 상태에 따른 화면 업데이트
                            updateView(false);
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

    // 플레이어 리스너
    androidx.media3.common.Player.Listener m_listener = new androidx.media3.common.Player.Listener() {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            androidx.media3.common.Player.Listener.super.onPlaybackStateChanged(playbackState);

            if (playbackState == STATE_ENDED) {
                if (MusicTimer.getInstance(mContext).isRunning()) {
                    // 재생 종료 후 다음 곡 재생
                    playNextMusic();
                    // 현재 재생 상태에 따른 화면 업데이트 && 앱이 백그라운드 상태가 아니고 Context가 존재할 때 UI 업데이트
                    if (mContext != null && !LifeCycleDetector.getInstance(mContext).isBackground())
                        updateView(true);
                } else {
                    // 현재 재생 상태에 따른 화면 업데이트 && 앱이 백그라운드 상태가 아니고 Context가 존재할 때 UI 업데이트
                    if (mContext != null && !LifeCycleDetector.getInstance(mContext).isBackground())
                        updateView(false);
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
    //endregion

    // region UI
    // 현재 재생 상태에 따른 화면 업데이트
    private void updateView(boolean isPlayNext) {
        if (binding.llSelectedMusic.getVisibility() == View.VISIBLE) {
            MusicVO vo = Player.getInstance(mContext).getMusicVO();
            // 선택된 음악 표시영역의 재생 정보 업데이트
            updateSelectedMusicView(vo);
            // 선택된 음악 표시영역의 재생 아이콘 변경
            updateViewPlay(Player.getInstance(mContext).isPlaying() || isPlayNext);
            // 선택된 음악 표시영역의 재생바 상태 업데이트
            updateViewGauge();
        }
    }

    // 선택된 음악 표시영역의 재생 정보 업데이트
    private void updateSelectedMusicView(MusicVO vo) {
        String strTitle = vo.getTitle();
        String strType = MusicConst.getTypeName(mContext, vo.getType());
        String strImgFileName = vo.getImgFileName();

        binding.ivSelectedCover.setImageDrawable(Utils.getImageByString(mContext, strImgFileName));
        binding.tvSelectedInfo.setText(strType);
        binding.tvSelectedTitle.setText(strTitle);
        binding.llSelectedMusic.setVisibility(View.VISIBLE);
    }

    // 선택된 음악 표시영역의 재생 아이콘 변경
    private void updateViewPlay(boolean isPlay) {
        if (isPlay)
            binding.ivSelectedPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
        else
            binding.ivSelectedPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
    }

    // 선택된 음악 표시영역의 재생바 상태 업데이트
    private void updateViewGauge() {
        if (binding.llSelectedMusic.getVisibility() == View.VISIBLE) {
            MusicVO vo = Player.getInstance(mContext).getMusicVO();
            // 음악 재생 Seekbar max값 설정
            binding.sbGauge.setMax(Integer.parseInt(vo.getPlayTimeSec()) * 1000);

            // 현재 재생시간 반영
            long nowMilliseconds = Player.getInstance(mContext).getNowTime();
            int nPercent = (int) nowMilliseconds;
            binding.sbGauge.setProgress(nPercent);
        }
    }

    // 로딩 표시 / 숨기기
    private void showLoading(boolean isShow) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isShow)
                    binding.llLoading.setVisibility(View.VISIBLE);
                else
                    binding.llLoading.setVisibility(View.GONE);
            }
        });
    }

    // endregion

    //region 액션
    // 장르 클릭
    private void clickCategory(int _position) {
        MusicCategoryVO vo = arrMusicCategory.get(_position);
        // 선택된 장르의 음악으로 목록 필터링
        MusicList.getInstance().filterMusicList(vo.getType());
        // 목록 갱신
        adapterMainContent.notifyDataSetChanged();
    }

    // 음악 클릭 - 선택된 음악 표시 및 플레이
    private void clickMusic(int position) {
        MusicVO vo = MusicList.getInstance().arrFilteredMusicList.get(position);
        // 음악 재생
        Player.getInstance(mContext).initMusic(vo);
        // 선택된 음악 표시영역의 재생 정보 업데이트
        updateSelectedMusicView(vo);
        // 선택된 음악 표시영역의 재생 아이콘 변경
        updateViewPlay(true);
        // 선택된 음악 표시영역의 재생바 상태 업데이트
        updateViewGauge();
        // 볼륨 초기화
        Player.getInstance(mContext).setVolume(1.f);
    }

    // 음악 일시정지 클릭
    private void clickPause() {
        boolean bIsPlaying = Player.getInstance(mContext).isPlaying();
        // 선택된 음악 표시영역의 재생 아이콘 변경
        updateViewPlay(!bIsPlaying);
        // 음악 재생/정지
        playMusic(!bIsPlaying);
    }

    // 선택된 음악 닫기 클릭
    private void clickCloseSelectedView() {
        // 선택된 음악 표시화면 숨김
        binding.llSelectedMusic.setVisibility(View.GONE);
        // 음악 종료
        Player.getInstance(mContext).stopMusic();
    }

    // 음악 재생 상세 화면으로 이동
    private void goDetail() {
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.bottom_in, R.anim.none_out);
    }
    //endregion

    //region 플레이어 컨트롤

    // 음악 재생
    private void playMusic(boolean isPlay) {
        if (isPlay) {
            // 타이머 재개
            MusicTimer.getInstance(mContext).resumeTimer();
            // 음악 재개
            Player.getInstance(mContext).resumeMusic();
        } else {
            // 타이머 멈춤
            MusicTimer.getInstance(mContext).pauseTimer();
            // 음악 멈춤
            Player.getInstance(mContext).pauseMusic();
        }
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
    //endregion

    //region 애드몹
    AdView adView;
    // 띠배너 크기 설정 및 추가
    private void setAdMobSize() {
        if (!AdMobConst.USE_AD_EXPOSE_BANNER) {
            binding.ivAds.setVisibility(View.GONE);
            return;
        } // 2025/02/19 : 띠배너 노출 옵션 코드 추가
        adView = new AdView(this);
        adView.setAdUnitId(AdMobConst.ADID.BANNER.label());
        adView.setAdSize(getAdSize());

        binding.ivAds.removeAllViews();
        binding.ivAds.addView(adView);
    }
    // 띠배너 로드
    private void loadAdMob() {
        if (!AdMobConst.USE_AD_EXPOSE_BANNER) return; // 2025/02/19 : 띠배너 노출 옵션 코드 추가
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    // 전면 광고 노출여부 체크
    private void checkShowAdMobPage() {
        if (BaseSettings.getInstance(mContext).appLaunchCount == AdMobConst.AD_EXPOSE_COUNT_PAGE && AdMobConst.USE_AD_EXPOSE_PAGE) {
            // 로딩
            showLoading(true);
            // 전면 광고 노출
            loadAdMobPage();

            // 구동 횟수 초기화
            BaseSettings.getInstance(mContext).appLaunchCount = 0;
            BaseSettings.getInstance(mContext).putIntItem(SettingsKey.APP_LAUNCH_COUNT, 0);
        }
    }

    // 전면광고 정보
    private InterstitialAd mInterstitialAd;

    // 전면광고 로드
    private void loadAdMobPage() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, AdMobConst.ADID.PAGE.label(), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        showLoading(false);
                        showAdMobPage();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                        showLoading(false);
                    }
                });

    }

    // 전면광고 표시
    private void showAdMobPage() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    // 보상형 광고 노출여부 체크
    private boolean checkShowAdMobReward() {

        if (AdMobConst.USE_AD_EXPOSE_REWARD) {
            // 횟수 초기화
            BaseSettings.getInstance(mContext).musicPlayCount += 1;
            BaseSettings.getInstance(mContext).putIntItem(SettingsKey.MUSIC_PLAY_COUNT, BaseSettings.getInstance(mContext).musicPlayCount);
        }

        if (BaseSettings.getInstance(mContext).musicPlayCount == AdMobConst.AD_EXPOSE_COUNT_REWARD && AdMobConst.USE_AD_EXPOSE_REWARD) {
            // 로딩
            showLoading(true);
            // 보상형 광고 로드
            loadAdMobReward();

            // 횟수 초기화
            BaseSettings.getInstance(mContext).musicPlayCount = 0;
            BaseSettings.getInstance(mContext).putIntItem(SettingsKey.MUSIC_PLAY_COUNT, 0);

            return true;
        } else {
            return false;
        }
    }

    // 보상형 광고 정보
    private RewardedAd rewardedAd;

    // 보상형 광고 로드
    private void loadAdMobReward() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, AdMobConst.ADID.REWARD.label(),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                        // 로딩
                        showLoading(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 음악 재생
                                clickMusic(m_nSelectedMusicIdx);
                            }
                        }, 500);
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        showLoading(false);
                        showAdMobReward();
                    }
                });

    }

    // 보상형 광고 표시
    private void showAdMobReward() {
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                rewardedAd = null;
                // 로딩
                showLoading(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 음악 재생
                        clickMusic(m_nSelectedMusicIdx);
                    }
                }, 500);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                rewardedAd = null;
                // 로딩
                showLoading(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 음악 재생
                        clickMusic(m_nSelectedMusicIdx);
                    }
                }, 500);
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
        if (rewardedAd != null) {
            Activity activityContext = MainActivity.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
//                    int rewardAmount = rewardItem.getAmount();
//                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }

    //endregion
}
