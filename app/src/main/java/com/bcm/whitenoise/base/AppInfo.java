package com.bcm.whitenoise.base;

import android.content.Context;

import com.bcm.whitenoise.R;

public class AppInfo {

    // 공유 텍스트
    static public String getShareText(Context context) {
        return context.getString(R.string.player_share_text);
    }

    // 공유 URL
    static public String getShareUrl() {
        return "http://www.facebookevent.com/";
    }
}
