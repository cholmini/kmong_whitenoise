package com.bcm.whitenoise.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.MainThread;
import androidx.recyclerview.widget.RecyclerView;

import com.bcm.whitenoise.R;
import com.bcm.whitenoise.constant.MusicConst;
import com.bcm.whitenoise.model.MusicCategory;
import com.bcm.whitenoise.model.MusicCategoryVO;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Handler;

public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MusicCategoryVO> arrCategoryList;

    private ICallbackHandler mCallback;

    public interface ICallbackHandler {
        void onClickItem(int position);
    }

    public MainCategoryAdapter(Context _context, ArrayList<MusicCategoryVO> _list, ICallbackHandler _callback) {
        mContext = _context;
        arrCategoryList = _list;
        mCallback = _callback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_main_category, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // 인덱스에 해당하는 장르 데이터 설정
        MusicCategoryVO vo = arrCategoryList.get(position);
        String strTitle = vo.getTitle();
        boolean bIsSelected = vo.isSelected();
        viewHolder.tbCategory.setText(strTitle);

        // 장르 선택 여부에 따른 UI 변경
        if (bIsSelected) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(100.f);
            drawable.setColor(Color.parseColor("#FFFFFF"));
            viewHolder.tbCategory.setBackground(drawable);
            viewHolder.tbCategory.setTextColor(Color.parseColor("#000000"));
        } else {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(100.f);
            drawable.setColor(Color.parseColor("#99000000"));
            drawable.setStroke(4, Color.parseColor("#99d0d0d0"));
            viewHolder.tbCategory.setBackground(drawable);
            viewHolder.tbCategory.setTextColor(Color.parseColor("#FFFFFF"));
        }

        // 장르 클릭 이벤트
        viewHolder.llParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) mCallback.onClickItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrCategoryList.size();
    }

    // 장르 목록 UI 컴포넌트
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 장르 항목 컨테이너
        public LinearLayout llParent;
        // 장르 제목
        public TextView tbCategory;

        public ViewHolder(View view) {
            super(view);

            llParent = (LinearLayout) view.findViewById(R.id.llParent);
            tbCategory = (TextView) view.findViewById(R.id.tbCategory);
        }
    }
}