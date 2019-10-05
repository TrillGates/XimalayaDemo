package com.sunofbeaches.himalaya.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunofbeaches.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.InnerHolder> {

    private List<QueryResult> mData = new ArrayList<>();
    private ItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public SearchRecommendAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_tecommend, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecommendAdapter.InnerHolder holder, int position) {
        TextView text = holder.itemView.findViewById(R.id.search_recommend_item);
        final QueryResult queryResult = mData.get(position);
        text.setText(queryResult.getKeyword());
        //设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(queryResult.getKeyword());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 设置数据
     *
     * @param keyWordList
     */
    public void setData(List<QueryResult> keyWordList) {
        mData.clear();
        mData.addAll(keyWordList);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }
    }


    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(String keyword);
    }
}
