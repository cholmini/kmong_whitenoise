package com.bcm.whitenoise.model;

public class MusicVO {

    //region 변수
    // 음악 타입
    private String type;
    // 음악 제목
    private String title;
    // 음악 작곡가
    private String composer;
    // 음악 총 재생시간
    private String playTimeSec;
    // 음악 파일명
    private String musicFileName;
    // 음악 커버이미지
    private String imgFileName;
    // 음악 GIF 이미지
    private String bgFileName;
    // endregion

    /**
     * 음악정보
     * @param _type 음악 타입
     * @param _title 음악 제목
     * @param _composer 음악 작곡가
     * @param _playTimeSec 음악 플레이시간(초)
     * @param _musicFileName 음악 파일명
     * @param _imgFileName 음악 커버 이미지 파일명
     * @param _bgFileName 음악 재생화면 GIF 이미지 파일명
     */
    public MusicVO(String _type, String _title, String _composer, String _playTimeSec, String _musicFileName, String _imgFileName, String _bgFileName) {
        type = _type;
        title = _title;
        composer = _composer;
        playTimeSec = _playTimeSec;
        musicFileName = _musicFileName;
        imgFileName = _imgFileName;
        bgFileName = _bgFileName;
    }

    // region 공개 함수

    // 타입 조회
    public String getType() {
        return type;
    }

    // 타입 저장
    public void setType(String type) {
        this.type = type;
    }

    // 제목 조회
    public String getTitle() {
        return title;
    }

    // 제목 저장
    public void setTitle(String title) {
        this.title = title;
    }

    // 작곡가 조회
    public String getComposer() {
        return composer;
    }

    // 작곡가 저장
    public void setComposer(String composer) {
        this.composer = composer;
    }

    // 총 재생시간 조회
    public String getPlayTimeSec() {
        return playTimeSec;
    }

    // 총 재생시간 저장
    public void setPlayTimeSec(String playTimeSec) {
        this.playTimeSec = playTimeSec;
    }

    // 음악 파일명 조회
    public String getMusicFileName() {
        return musicFileName;
    }

    // 음악 파일명 저장
    public void setMusicFileName(String musicFileName) {
        this.musicFileName = musicFileName;
    }

    // 음악 커버 이미지 파일명 조회
    public String getImgFileName() {
        return imgFileName;
    }

    // 음악 커버 이미지 파일명 저장
    public void setImgFileName(String imgFileName) {
        this.imgFileName = imgFileName;
    }

    // 음악 배경 GIF 이미지 파일명 조회
    public String getBgFileName() {
        return bgFileName;
    }

    // 음악 배경 GIF 이미지 파일명 저장
    public void setBgFileName(String bgFileName) {
        this.bgFileName = bgFileName;
    }
    // endregion
}
