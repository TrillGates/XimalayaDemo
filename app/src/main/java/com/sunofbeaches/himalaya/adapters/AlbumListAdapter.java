package com.sunofbeaches.himalaya.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sunofbeaches.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrillGates on 2018/12/23.
 * God bless my code!
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private static final String TAG = "AlbumListAdapter";
    private List<Album> mData = new ArrayList<>();
    private OnAlbumItemClickListener mItemClickListener = null;
    private OnAlbumItemLongClickListener mLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //这里是载View
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //这里是设置数据
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    int clickPosition = (int) v.getTag();
                    mItemClickListener.onItemClick(clickPosition, mData.get(clickPosition));
                }
                Log.d(TAG, "holder.itemView click -- > " + v.getTag());
            }
        });
        holder.setData(mData.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongClickListener != null) {
                    int clickPosition = (int) v.getTag();
                    mLongClickListener.onItemLongClick(mData.get(clickPosition));
                }
                //true表示消费掉该事件
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新一下UI。
        notifyDataSetChanged();
    }


    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到各个控件，设置数据
            //专辑封面
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDesTv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);

            albumTitleTv.setText(album.getAlbumTitle());
            albumDesTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(album.getPlayCount() + "");
            albumContentCountTv.setText(album.getIncludeTrackCount() + "");

            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Picasso.with(itemView.getContext()).load(coverUrlLarge).into(albumCoverIv);
            } else {
                albumCoverIv.setImageResource(R.mipmap.ximalay_logo);
            }
        }
    }


    public void setAlbumItemClickListener(OnAlbumItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnAlbumItemClickListener {
        void onItemClick(int position, Album album);
    }


    public void setOnAlbumItemLongClickListener(OnAlbumItemLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    /**
     * item长按的接口
     */
    public interface OnAlbumItemLongClickListener {
        void onItemLongClick(Album album);
    }

}
