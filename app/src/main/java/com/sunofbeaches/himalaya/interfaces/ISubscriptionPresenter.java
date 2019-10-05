package com.sunofbeaches.himalaya.interfaces;

import com.sunofbeaches.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * 订阅我们一般有上限，比如说不能超过100个
 */
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback> {

    /**
     * 添加订阅
     *
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     *
     * @param album
     */
    void deleteSubscription(Album album);


    /**
     * 获取订阅列表
     */
    void getSubscriptionList();


    /**
     * 判断当前专辑是否已经收藏/订阅
     *
     * @param album
     */
    boolean isSub(Album album);

}
