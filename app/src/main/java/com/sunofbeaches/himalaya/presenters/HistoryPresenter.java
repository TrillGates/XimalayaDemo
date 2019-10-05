package com.sunofbeaches.himalaya.presenters;

import com.sunofbeaches.himalaya.base.BaseApplication;
import com.sunofbeaches.himalaya.data.HistoryDao;
import com.sunofbeaches.himalaya.data.IHistoryDao;
import com.sunofbeaches.himalaya.data.IHistoryDaoCallback;
import com.sunofbeaches.himalaya.interfaces.IHistoryCallback;
import com.sunofbeaches.himalaya.interfaces.IHistoryPresenter;
import com.sunofbeaches.himalaya.utils.Constants;
import com.sunofbeaches.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * 历史数量最多是100条记录，如果超过了100条记录的话，那么就删除最前面添加的，再把当前的
 * 历史添加进去.
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private static final String TAG = "HistoryPresenter";
    private List<IHistoryCallback> mCallbacks = new ArrayList<>();

    private final IHistoryDao mHistoryDao;
    private List<Track> mCurrentHistories = null;
    private Track mCurrentAddTrack = null;

    private HistoryPresenter() {
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistories();
    }

    private static HistoryPresenter sHistoryPresenter = null;

    public static HistoryPresenter getHistoryPresenter() {
        if(sHistoryPresenter == null) {
            synchronized(HistoryPresenter.class) {
                if(sHistoryPresenter == null) {
                    sHistoryPresenter = new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }

    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if(mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean isDoDelAsOutOfSize = false;

    @Override
    public void addHistory(final Track track) {
        //需要去判断是否>=100条记录
        if(mCurrentHistories != null && mCurrentHistories.size() >= Constants.MAX_HISTORY_COUNT) {
            isDoDelAsOutOfSize = true;
            this.mCurrentAddTrack = track;
            //先不能添加，先删除最前的一条记录，再添加
            delHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));
        } else {
            doAddHistory(track);
        }
    }

    private void doAddHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if(mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if(mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if(mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        //ui注册过来的
        if(!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {
        //删除UI的回调接口
        mCallbacks.remove(iHistoryCallback);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        //nothing to do.
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        //nothing to do.
        if(isDoDelAsOutOfSize && mCurrentAddTrack != null) {
            isDoDelAsOutOfSize = false;
            //添加当前的数据进到数据库里
            addHistory(mCurrentAddTrack);
        } else {
            listHistories();
        }
    }

    @Override
    public void onHistoriesLoaded(final List<Track> tracks) {
        this.mCurrentHistories = tracks;
        LogUtil.d(TAG,"histories size -- > " + tracks.size());

        //通知UI更新数据
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for(IHistoryCallback callback : mCallbacks) {
                    callback.onHistoriesLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClean(boolean isSuccess) {
        //nothing to do.
        listHistories();
    }
}
