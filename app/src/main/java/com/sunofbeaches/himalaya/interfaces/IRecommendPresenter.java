package com.sunofbeaches.himalaya.interfaces;

import com.sunofbeaches.himalaya.base.IBasePresenter;

/**
 * Created by TrillGates on 2018/12/27.
 * God bless my code!
 */
public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback> {

    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上接加载更多
     */
    void loadMore();


}
