package com.sunofbeaches.himalaya.interfaces;

import com.sunofbeaches.himalaya.base.IBasePresenter;

/**
 * Created by TrillGates on 2019/1/24.
 * God bless my code!
 */
public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallback> {

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上接加载更多
     */
    void loadMore();


    /**
     * 获取专辑详情
     *
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId, int page);


}
