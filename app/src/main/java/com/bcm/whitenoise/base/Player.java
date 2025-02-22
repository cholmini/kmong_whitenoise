package com.bcm.whitenoise.base;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.bcm.whitenoise.model.MusicVO;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Player {

    // region 변수
    // 컨텍스트
    private static Context mContext;
    // 플레이어 인스턴스
    private static Player instance;

    // 음악 컨트롤러
    private static MediaController mediaController;
    // 재생중인 음악 정보
    private static MusicVO musicVO;
    // endregion

    private Player() {
    }

    public static Player getInstance(Context _context) {
        mContext = _context;
        if(instance == null) {
            instance = new Player();
        }
        return instance;
    }

    // region 공개함수
    // 리스너 설정
    public void addListener(androidx.media3.common.Player.Listener listener) {
        if (mediaController != null)
            mediaController.addListener(listener);
    }

    // 리스너 제거
    public void removeListener(androidx.media3.common.Player.Listener listener) {
        if (mediaController != null)
            mediaController.removeListener(listener);
    }

    // 선택된 음악정보 반환
    public MusicVO getMusicVO() {
        return musicVO;
    }

    // 마지막 음악 설정
    public void prepareMusic(MusicVO vo) {
        musicVO = vo;

        if (mediaController == null) {
            SessionToken sessionToken =
                    new SessionToken(mContext, new ComponentName(mContext, PlaybackService.class));
            ListenableFuture<MediaController> controllerFuture =
                    new MediaController.Builder(mContext, sessionToken).buildAsync();
            controllerFuture.addListener(() -> {

                MediaItem mediaItem =
                        new MediaItem.Builder()
                                .setMediaId("media-1")
                                .setUri(Utils.getUriByString(mContext,vo.getMusicFileName()))
                                .setMediaMetadata(
                                        new MediaMetadata.Builder()
                                                .setArtist(musicVO.getComposer())
                                                .setTitle(musicVO.getTitle())
                                                .build())
                                .build();

                mediaController = null;
                try {
                    mediaController = controllerFuture.get();
                    mediaController.setMediaItem(mediaItem);
                    mediaController.prepare();
                    mediaController.pause();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }, MoreExecutors.directExecutor());
        } else {
            MediaItem mediaItem =
                    new MediaItem.Builder()
                            .setMediaId("media-1")
                            .setUri(Utils.getUriByString(mContext,vo.getMusicFileName()))
                            .setMediaMetadata(
                                    new MediaMetadata.Builder()
                                            .setArtist(musicVO.getComposer())
                                            .setTitle(musicVO.getTitle())
                                            .build())
                            .build();

            try {
                mediaController.setMediaItem(mediaItem);
                mediaController.prepare();
                mediaController.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 선택된 음악 설정
    public void initMusic(MusicVO vo) {
        musicVO = vo;

        if (mediaController == null) {
            SessionToken sessionToken =
                    new SessionToken(mContext, new ComponentName(mContext, PlaybackService.class));
            ListenableFuture<MediaController> controllerFuture =
                    new MediaController.Builder(mContext, sessionToken).buildAsync();
            controllerFuture.addListener(() -> {

                MediaItem mediaItem =
                        new MediaItem.Builder()
                                .setMediaId("media-1")
                                .setUri(Utils.getUriByString(mContext,vo.getMusicFileName()))
                                .setMediaMetadata(
                                        new MediaMetadata.Builder()
                                                .setArtist(musicVO.getComposer())
                                                .setTitle(musicVO.getTitle())
                                                .build())
                                .build();

                mediaController = null;
                try {
                    mediaController = controllerFuture.get();
                    mediaController.setMediaItem(mediaItem);
                    mediaController.prepare();
                    mediaController.play();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }, MoreExecutors.directExecutor());
        } else {
            MediaItem mediaItem =
                    new MediaItem.Builder()
                            .setMediaId("media-1")
                            .setUri(Utils.getUriByString(mContext,vo.getMusicFileName()))
                            .setMediaMetadata(
                                    new MediaMetadata.Builder()
                                            .setArtist(musicVO.getComposer())
                                            .setTitle(musicVO.getTitle())
                                            .build())
                            .build();

            try {
                mediaController.setMediaItem(mediaItem);
                mediaController.prepare();
                mediaController.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 재생여부 반환
    public boolean isPlaying() {
        if (mediaController != null) {
            return mediaController.isPlaying();
        }

        return false;
    }

    // 재개
    public void resumeMusic() {
        if (mediaController != null) {
            mediaController.play();
        }
    }

    // 일시정지
    public void pauseMusic() {
        if (mediaController != null) {
            mediaController.pause();
        }
    }

    // 정지
    public void stopMusic() {
        if (mediaController != null) {
            mediaController.pause();
        }
    }

    // 완전제거
    public void clearMusic() {
        if (mediaController != null) {
            mediaController.stop();
            mediaController.clearMediaItems();
            mediaController.release();
            mediaController = null;
        }
    }

    // 반복설정
    public void repeatMusic(boolean isRepeat) {
        if (mediaController != null) {
            mediaController.setRepeatMode(isRepeat ? androidx.media3.common.Player.REPEAT_MODE_ONE : androidx.media3.common.Player.REPEAT_MODE_OFF);
        }
    }

    // 재생 위치 변경
    public void changePlayPoint(int changed) {
        if (mediaController != null) {
            mediaController.getCurrentPosition();
            mediaController.setMediaItem(Objects.requireNonNull(mediaController.getCurrentMediaItem()), changed);
        }
    }

    // 현재 재생시간 반환
    public long getNowTime() {

        if (mediaController != null) {
            return mediaController.getCurrentPosition();
        }
        return 0;
    }

    // 총 재생시간 반환
    public long getTotalTime() {
        if (mediaController != null) {
            return mediaController.getDuration();
        }
        return 0;
    }

    // 볼륨 크기 조정
    public void setVolume(float volume) {
        if (mediaController != null) {
            mediaController.setVolume(volume);
        }
    }
    //endregion
}
