package com.bcm.whitenoise.base;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.bcm.whitenoise.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class Utils {

    // 재생시간 00:00형식으로 반환
    public static String getTimeText(Context _context, String _playtimeSec) {
        int nPlaytimeSec = Integer.parseInt(_playtimeSec);
        int nPlaytimeMin = nPlaytimeSec / 60;
        nPlaytimeSec = nPlaytimeSec % 60;
        if (nPlaytimeMin == 0) {
            return nPlaytimeSec + _context.getString(R.string.common_sec);
        } else {
            return nPlaytimeMin + _context.getString(R.string.common_min) + " " + nPlaytimeSec + _context.getString(R.string.common_sec);
        }
    }

    // 이미지 파일명으로 Drawable 반환
    public static Drawable getImageByString(Context _context, String _imageName) {
        return _context.getResources().getDrawable(_context.getResources().getIdentifier(_imageName, "drawable", _context.getPackageName()));
    }

    // 음악 파일명으로 Uri 반환
    public static Uri getUriByString(Context _context, String _musicFileName) {
        return new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE).path(Integer.toString(_context.getResources().getIdentifier(_musicFileName, "raw", _context.getPackageName()))).build();
    }

    // gif 파일명으로 id 반환
    public static int getRawIdByString(Context _context, String _fileName) {
        return _context.getResources().getIdentifier(_fileName, "raw", _context.getPackageName());
    }

    // 공유하기
    public static void shareWithSns(Context context) {
        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, AppInfo.getShareText(context) + "\n" + AppInfo.getShareUrl());
            share.putExtra(Intent.EXTRA_TITLE, AppInfo.getShareText(context));
            Intent intent = Intent.createChooser(share, AppInfo.getShareText(context));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Dp 값을 PX값으로 반환
    public static int ConvertDPtoPX(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
