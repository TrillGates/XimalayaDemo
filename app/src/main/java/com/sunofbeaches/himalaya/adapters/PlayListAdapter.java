package com.sunofbeaches.himalaya.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunofbeaches.himalaya.R;
import com.sunofbeaches.himalaya.base.BaseApplication;
import com.sunofbeaches.himalaya.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {

    private List<Track> mData = new ArrayList<>();
    private int playingIndex = 0;
    private SobPopWindow.PlayListItemClickListener mItemClickListener = null;

    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position);
                }
            }
        });
        //设置数据
        Track track = mData.get(position);

        TextView trackTitleTv = holder.itemView.findViewById(R.id.track_title_tv);

        //设置字体颜色
        trackTitleTv.setTextColor(
                BaseApplication.getAppContext().getResources().getColor(playingIndex == position ?
                        R.color.second_color : R.color.play_list_text_color));


        trackTitleTv.setText(track.getTrackTitle());
        //找播放状态的图标
        View playingIconView = holder.itemView.findViewById(R.id.play_icon_iv);
        playingIconView.setVisibility(playingIndex == position ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> data) {
        //设置数据,更新列表
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int position) {
        playingIndex = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(SobPopWindow.PlayListItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }
    }
}
