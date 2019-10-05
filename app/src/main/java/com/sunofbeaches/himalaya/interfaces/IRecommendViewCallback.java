package com.sunofbeaches.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by TrillGates on 2018/12/27.
 * God bless my code!
 */
public interface IRecommendViewCallback {


    /**
     * 获取推荐内容的结果
     *
     * @param result
     */
    void onRecommendListLoaded(List<Album> result);

    /**
     * 网络错误
     */
    void onNetworkError();


    /**
     * 数据为空
     */
    void onEmpty();

    /**
     * 正在加载
     */
    void onLoading();

}
