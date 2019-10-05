package com.sunofbeaches.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryCallback {

    /**
     * 历史内容加载结果.
     *
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);

}
