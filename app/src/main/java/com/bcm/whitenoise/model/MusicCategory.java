package com.bcm.whitenoise.model;

import android.content.Context;

import com.bcm.whitenoise.constant.MusicConst;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicCategory {

    //region 변수
    private static Context mContext;
    private static MusicCategory instance;
    public ArrayList<HashMap<MusicConst.CategoryListKey, String>> arrMusicCategory = new ArrayList<>();
    //endregion

    private MusicCategory(){
        makeMusicCategoryList();
    }

    public static MusicCategory getInstance(Context _context) {
        mContext = _context;
        if(instance == null) {
            instance = new MusicCategory();
        }
        return instance;
    }

    //음악정보 배열생성 (여기에서 앱에 표시될 음악을 추가/삭제)
    private void makeMusicCategoryList() {
        addMusicCategoryList(MusicConst.type.All.name(), MusicConst.getTypeName(mContext, MusicConst.type.All),"1");
        addMusicCategoryList(MusicConst.type.Meditation.name(), MusicConst.getTypeName(mContext, MusicConst.type.Meditation),"0");
        addMusicCategoryList(MusicConst.type.Goodnight.name(), MusicConst.getTypeName(mContext, MusicConst.type.Goodnight),"0");
        addMusicCategoryList(MusicConst.type.Sing.name(), MusicConst.getTypeName(mContext, MusicConst.type.Sing),"0");
        addMusicCategoryList(MusicConst.type.Classic.name(), MusicConst.getTypeName(mContext, MusicConst.type.Classic),"0");
        addMusicCategoryList(MusicConst.type.Jazz.name(), MusicConst.getTypeName(mContext, MusicConst.type.Jazz),"0");
        addMusicCategoryList(MusicConst.type.Whitenoise.name(), MusicConst.getTypeName(mContext, MusicConst.type.Whitenoise),"0");
        addMusicCategoryList(MusicConst.type.Nature.name(), MusicConst.getTypeName(mContext, MusicConst.type.Nature),"0");
        addMusicCategoryList(MusicConst.type.Etc.name(), MusicConst.getTypeName(mContext, MusicConst.type.Etc),"0");
    }

    /**
     * 배열에 카테고리정보 추가
     * @param _type 음악 타입(장르)
     * @param _title 표시될 카테고리명
     * @param _isSelected 카테고리 선택여부 "0" or "1"
     */
    private void addMusicCategoryList(String _type, String _title, String _isSelected) {
        HashMap<MusicConst.CategoryListKey, String> map = new HashMap<>();
        map.put(MusicConst.CategoryListKey.Type, _type);
        map.put(MusicConst.CategoryListKey.Title, _title);
        map.put(MusicConst.CategoryListKey.IsSelected, _isSelected);
        arrMusicCategory.add(map);
    }
}
