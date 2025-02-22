package com.bcm.whitenoise.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.bcm.whitenoise.R;

public class BaseAlert {

    // 단일 버튼(확인) 팝업 - 메세지만 설정 후 표시 (텍스트 전달)
    public static void show(Context context, String message) {
        show(context, "", message, null);
    }

    // 단일 버튼(확인) 팝업 - 제목과 메세지 설정 후 표시 (텍스트 전달)
    public static void show(Context context, String title, String message) {
        show(context, title, message, null);
    }

    // 단일 버튼(확인) 팝업 - 제목과 메세지 설정 후 표시 & 버튼 클릭 이벤트 반환 (텍스트 전달)
    public static void show(Context context, String title, String message, DialogInterface.OnClickListener eventPositive) {
        String alertMessage = message;
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(context);
        AlertDialog dialog = customBuilder.setCancelable(false)
                .setMessage(alertMessage)
                .setTitle(title)
                .setPositiveButton(R.string.common_confirm, eventPositive)
                .create();
        dialog.show();
    }

    // TWO 버튼(확인/취소) 팝업 - 제목과 메세지 설정 후 표시 & 버튼 클릭 이벤트 반환 (텍스트 전달)
    public static void show(Context context, String title, String message, DialogInterface.OnClickListener eventPositive, DialogInterface.OnClickListener eventNegative) {
        String alertMessage = message;
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(context);
        AlertDialog dialog = customBuilder.setCancelable(false)
                .setMessage(alertMessage)
                .setTitle(title)
                .setNegativeButton(R.string.common_cancel, eventNegative)
                .setPositiveButton(R.string.common_confirm, eventPositive)
                .create();
        dialog.show();
    }
}
