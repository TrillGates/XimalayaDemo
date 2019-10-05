package com.sunofbeaches.himalaya.presenters;

import android.support.annotation.Nullable;

import com.sunofbeaches.himalaya.data.XimalayApi;
import com.sunofbeaches.himalaya.interfaces.IAlbumDetailPresenter;
import com.sunofbeaches.himalaya.interfaces.IAlbumDetailViewCallback;
import com.sunofbeaches.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrillGates on 2019/1/24.
 * God bless my code!
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {


    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();

    private Album mTargetAlbum = null;
    //当前的专辑id
    private int mCurrentAlbumId = -1;
    //当前页
    private int mCurrentPageIndex = 0;

    private AlbumDetailPresenter() {
    }

    private static AlbumDetailPresenter sInstance = null;

    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }


    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
        //去加载更多内容
        mCurrentPageIndex++;
        //传入true，表示结果会追加到列表的后方。
        doLoaded(true);
    }

    private void doLoaded(final boolean isLoaderMore) {
        XimalayApi ximalayApi = XimalayApi.getXimalayApi();
        ximalayApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, "tracks size -- > " + tracks.size());
                    if (isLoaderMore) {
                        //上拉加载，结果放到后面去
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoaderMoreResult(size);
                    } else {
                        //这个是下拉加载，结果放到前面去
                        mTracks.addAll(0, tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isLoaderMore) {
                    mCurrentPageIndex--;
                }
                LogUtil.d(TAG, "errorCode -- >   " + errorCode);
                LogUtil.d(TAG, "errorMsg -- >   " + errorMsg);
                handlerError(errorCode, errorMsg);
            }
        }, mCurrentAlbumId, mCurrentPageIndex);
    }

    /**
     * 处理加载更多的结果
     *
     * @param size
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;
        //根据页码和专辑id获取列表
        doLoaded(false);
    }

    /**
     * 如果是发生错误，那么就通知UI
     *
     * @param errorCode
     * @param errorMsg
     */
    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onNetworkError(errorCode, errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)) {
            mCallbacks.add(detailViewCallback);
            if (mTargetAlbum != null) {
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        mCallbacks.remove(detailViewCallback);
    }


    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }
}
