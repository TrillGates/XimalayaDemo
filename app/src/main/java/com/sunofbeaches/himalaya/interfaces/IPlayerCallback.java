package com.sunofbeaches.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {

    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     * 播放停止
     */
    void onPlayStop();

    /**
     * 播放错误
     */
    void onPlayError();

    /**
     * 下一首播放
     */
    void nextPlay(Track track);

    /**
     * 上一首播放
     */
    void onPrePlay(Track track);

    /**
     * 播放列表数据加载完成
     *
     * @param list 播放器列表数据
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放模式改变了
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条的改变
     *
     * @param currentProgress
     * @param total
     */
    void onProgressChange(int currentProgress, int total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告结束
     */
    void onAdFinished();


    /**
     * 更新当前节目
     *
     * @param track 节目
     */
    void onTrackUpdate(Track track, int playIndex);

    /**
     * 通知UI更新播放列表的顺序文字和图标.
     * @param isReverse
     */
    void updateListOrder(boolean isReverse);
}
