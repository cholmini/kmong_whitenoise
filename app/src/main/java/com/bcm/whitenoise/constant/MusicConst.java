package com.bcm.whitenoise.constant;

import android.content.Context;

import com.bcm.whitenoise.R;

public class MusicConst {
    // 음악타입, 제목, 작곡가, 플레이시간(초), 음악파일명, 사진이름
    public enum ListKey {
        Type,
        Title,
        Composer,
        PlaytimeSec,
        MusicFileName,
        ImgFileName
    }

    // 명상, 노래, 굿나잇
    public enum type {
        All,
        Meditation,
        Sing,
        Goodnight,
        Classic,
        Jazz,
        Whitenoise,
        Etc
    }

    //음악 타입에 따른 장르명 반환
    static public String getTypeName(Context _context, type _type) {
        if (_type == type.All) {
            return _context.getString(R.string.music_type_all);
        } else if (_type == type.Meditation) {
            return _context.getString(R.string.music_type_meditation);
        } else if (_type == type.Sing) {
            return _context.getString(R.string.music_type_sing);
        } else if (_type == type.Goodnight) {
            return _context.getString(R.string.music_type_goodnight);
        } else if (_type == type.Classic) {
            return _context.getString(R.string.music_type_classic);
        } else if (_type == type.Jazz) {
            return _context.getString(R.string.music_type_jazz);
        } else if (_type == type.Whitenoise) {
            return _context.getString(R.string.music_type_whitenoise);
        } else if (_type == type.Etc) {
            return _context.getString(R.string.music_type_etc);
        } else {
            return "";
        }
    }

    // 음악 타입에 따른 장르명 반환
    static public String getTypeName(Context _context, String _type) {
        if (_type.equals(type.All.name())) {
            return _context.getString(R.string.music_type_all);
        } else if (_type.equals(type.Meditation.name())) {
            return _context.getString(R.string.music_type_meditation);
        } else if (_type.equals(type.Sing.name())) {
            return _context.getString(R.string.music_type_sing);
        } else if (_type.equals(type.Goodnight.name())) {
            return _context.getString(R.string.music_type_goodnight);
        } else if (_type.equals(type.Classic.name())) {
            return _context.getString(R.string.music_type_classic);
        } else if (_type.equals(type.Jazz.name())) {
            return _context.getString(R.string.music_type_jazz);
        } else if (_type.equals(type.Whitenoise.name())) {
            return _context.getString(R.string.music_type_whitenoise);
        } else if (_type.equals(type.Etc.name())) {
            return _context.getString(R.string.music_type_etc);
        } else {
            return "";
        }
    }

    // 타입(장르), 표시될 카테고리명, 선택여부
    public enum CategoryListKey {
        Type,
        Title,
        IsSelected
    }
}
