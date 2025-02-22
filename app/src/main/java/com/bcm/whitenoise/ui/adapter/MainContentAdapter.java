package com.bcm.whitenoise.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bcm.whitenoise.R;
import com.bcm.whitenoise.base.Utils;
import com.bcm.whitenoise.constant.MusicConst;
import com.bcm.whitenoise.model.MusicCategoryVO;
import com.bcm.whitenoise.model.MusicList;
import com.bcm.whitenoise.model.MusicVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainContentAdapter extends RecyclerView.Adapter<MainContentAdapter.ViewHolder> {

    private Context mContext;

    private ICallbackHandler mCallback;

    public interface ICallbackHandler {
        void onClickItem(int position);
    }

    public MainContentAdapter(Context _context, ICallbackHandler _callback) {
        mContext = _context;
        mCallback = _callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_main_content, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // 인덱스에 해당하는 음악 데이터 설정
        MusicVO vo = MusicList.getInstance().arrFilteredMusicList.get(position);
        String strTitle = vo.getTitle();
        String strType = MusicConst.getTypeName(mContext, vo.getType());
        String strPlaytimeSec = vo.getPlayTimeSec();
        String strComposer = vo.getComposer();
        String strImgFileName = vo.getImgFileName();

        strType += " • " + Utils.getTimeText(mContext, strPlaytimeSec);

        // 음악 데이터 바인딩
        viewHolder.ivCover.setImageDrawable(Utils.getImageByString(mContext, strImgFileName));

        viewHolder.tvTitle.setText(strTitle);
        viewHolder.tvInfo.setText(strType);
        viewHolder.tvComposer.setText(strComposer);

        // 음악 클릭 이벤트
        viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION) {
                    mCallback.onClickItem(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return MusicList.getInstance().arrFilteredMusicList.size();
    }

    // 재생 목록 UI 컴포넌트
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // 재생 항목 컨테이너
        public LinearLayout llItem;
        // 재생 항목 커버이미지
        public ImageView ivCover;
        // 재생 항목 장르 및 재생시간
        public TextView tvInfo;
        // 재생 항목 제목
        public TextView tvTitle;
        // 재생 항목 작곡가
        public TextView tvComposer;

        public ViewHolder(View view) {
            super(view);

            llItem = (LinearLayout) view.findViewById(R.id.llItem);
            ivCover = (ImageView) view.findViewById(R.id.ivCover);
            tvInfo = (TextView) view.findViewById(R.id.tvInfo);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvComposer = (TextView) view.findViewById(R.id.tvComposer);
        }
    }
}