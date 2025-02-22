package com.bcm.whitenoise.model;

public class MusicCategoryVO {
    //region 변수
    // 타입
    private String type;
    // 타입 타이틀
    private String title;
    // 타입 선택 여부
    private boolean isSelected;
    //endregion

    public MusicCategoryVO(String _type, String _title, boolean _isSelected) {
        type = _type;
        title = _title;
        isSelected = _isSelected;
    }

    //region 공개함수
    // 타입 조회
    public String getType() {
        return type;
    }

    // 타입 저장
    public void setType(String type) {
        this.type = type;
    }

    // 타입 타이틀 조회
    public String getTitle() {
        return title;
    }

    // 타입 타이틀 저장
    public void setTitle(String title) {
        this.title = title;
    }

    // 타입 선택 여부 조회
    public boolean isSelected() {
        return isSelected;
    }

    // 타입 선택 여부 저장
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    //endregion
}
