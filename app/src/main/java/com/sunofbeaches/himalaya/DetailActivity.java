package com.sunofbeaches.himalaya;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sunofbeaches.himalaya.adapters.TrackListAdapter;
import com.sunofbeaches.himalaya.base.BaseActivity;
import com.sunofbeaches.himalaya.base.BaseApplication;
import com.sunofbeaches.himalaya.interfaces.IAlbumDetailViewCallback;
import com.sunofbeaches.himalaya.interfaces.IPlayerCallback;
import com.sunofbeaches.himalaya.interfaces.ISubscriptionCallback;
import com.sunofbeaches.himalaya.interfaces.ISubscriptionPresenter;
import com.sunofbeaches.himalaya.presenters.AlbumDetailPresenter;
import com.sunofbeaches.himalaya.presenters.PlayerPresenter;
import com.sunofbeaches.himalaya.presenters.SubscriptionPresenter;
import com.sunofbeaches.himalaya.utils.Constants;
import com.sunofbeaches.himalaya.utils.ImageBlur;
import com.sunofbeaches.himalaya.utils.LogUtil;
import com.sunofbeaches.himalaya.views.RoundRectImageView;
import com.sunofbeaches.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by TrillGates on 2019/1/17.
 * God bless my code!
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, TrackListAdapter.ItemClickListener, IPlayerCallback, ISubscriptionCallback {

    private static final String TAG = "DetailActivity";
    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private int mCurrentPage = 1;
    private RecyclerView mDetailList;
    private TrackListAdapter mDetailListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId = -1;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTips;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks = null;
    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mCurrentTrackTitle;
    private TextView mSubBtn;
    private ISubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        initPresenter();
        //设置订阅按钮的状态
        updateSubState();
        updatePlaySate(mPlayerPresenter.isPlaying());
        initListener();
    }

    private void updateSubState() {
        if (mSubscriptionPresenter != null) {
            boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(isSub ? R.string.cancel_sub_tips_text : R.string.sub_tips_text);
        }
    }

    private void initPresenter() {
        //这个是专辑详情的presenter.
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
        //播放器的Presenter.
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        //订阅相关的presenter.
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.getSubscriptionList();
        mSubscriptionPresenter.registerViewCallback(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.unRegisterViewCallback(this);
        }
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
    }

    private void initListener() {
        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    //判断播放器是否有播放列表.
                    boolean has = mPlayerPresenter.hasPlayList();
                    if (has) {
                        //控制播放器的状态
                        handlePlayControl();
                    } else {
                        handleNoPlayList();
                    }
                }
            }
        });

        mSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                    boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    //如果没有订阅，就去订阅，如果已经订阅了，那么就取消订阅
                    if (isSub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                    } else {
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                    }
                }
            }
        });
    }

    /**
     * 当播放器里面没有播放的内容，我们要进行处理一下。
     */
    private void handleNoPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks, DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        if (mPlayerPresenter.isPlaying()) {
            //正播放，那么就暂停
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.play();
        }
    }

    private void initView() {
        mDetailListContainer = this.findViewById(R.id.detail_list_container);
        //
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }

        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.viv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbumAuthor = this.findViewById(R.id.tv_album_author);
        //播放控制的图标
        mPlayControlBtn = this.findViewById(R.id.detail_play_control);
        mPlayControlTips = this.findViewById(R.id.play_control_tv);
        mPlayControlTips.setSelected(true);
        //
        mSubBtn = this.findViewById(R.id.detail_sub_btn);
    }


    private boolean mIsLoaderMore = false;

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mDetailList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        //RecyclerView的使用步骤
        //第一步:设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDetailList.setLayoutManager(layoutManager);
        //第二步:设置适配器
        mDetailListAdapter = new TrackListAdapter();
        mDetailList.setAdapter(mDetailListAdapter);
        //设置item的上下间距
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });

        mDetailListAdapter.setItemClickListener(this);
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOverScrollBottomShow(false);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "刷新成功...", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //去加载更多的内容
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                    mIsLoaderMore = true;
                }
            }
        });
        return detailListView;
    }


    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (mIsLoaderMore && mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore = false;
        }

        this.mCurrentTracks = tracks;
        //判断数据结果，根据结果控制UI显示
        if (tracks == null || tracks.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //更新/设置UI数据
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        //请求发生错误，显示网络异常状态
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        this.mCurrentAlbum = album;
        long id = album.getId();

        LogUtil.d(TAG, "album -- > " + id);
        mCurrentId = id;
        //获取专辑的详情内容
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) id, mCurrentPage);
        }
        //拿数据，显示Loading状态
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        if (mAlbumTitle != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }

        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }

        //做毛玻璃效果
        if (mLargeCover != null && null != mLargeCover) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();
                    if (drawable != null) {
                        //到这里才说明是有图片的
                        ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError() {
                    LogUtil.d(TAG, "onError");
                }
            });

        }

        if (mSmallCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mSmallCover);
        }
    }

    @Override
    public void onLoaderMoreFinished(int size) {
        if (size > 0) {
            Toast.makeText(this, "成功加载" + size + "条节目", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有更多节目", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void onRetryClick() {
        //这里面表示用户网络不佳的时候 去点击了重新加载
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) mCurrentId, mCurrentPage);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData, position);
        //跳转到播放器界面
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    /**
     * 根据播放状态修改图标和文字
     *
     * @param playing
     */
    private void updatePlaySate(boolean playing) {
        if (mPlayControlBtn != null && mPlayControlTips != null) {
            mPlayControlBtn.setImageResource(playing ? R.drawable.selector_play_control_pause : R.drawable.selector_play_control_play);
            if (!playing) {
                mPlayControlTips.setText(R.string.click_play_tips_text);
            } else {
                if (!TextUtils.isEmpty(mCurrentTrackTitle)) {
                    mPlayControlTips.setText(mCurrentTrackTitle);
                }
            }
        }
    }

    @Override
    public void onPlayStart() {
        //修改图标为暂停的，文字修改为正在播放.
        updatePlaySate(true);
    }

    @Override
    public void onPlayPause() {
        //设置成播放的图标，文字修改成已暂停
        updatePlaySate(false);
    }

    @Override
    public void onPlayStop() {
        //设置成播放的图标，文字修改成已暂停
        updatePlaySate(false);
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null) {
            mCurrentTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mCurrentTrackTitle) && mPlayControlTips != null) {
                mPlayControlTips.setText(mCurrentTrackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功了，那就修改UI成取消订阅
            mSubBtn.setText(R.string.cancel_sub_tips_text);
        }
        //给个toast
        String tipsText = isSuccess ? "订阅成功" : "订阅失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功了，那就修改UI成取消订阅
            mSubBtn.setText(R.string.sub_tips_text);
        }
        //给个toast
        String tipsText = isSuccess ? "删除成功" : "删除失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        //在这个界面 不需要处理
    }

    @Override
    public void onSubFull() {
        //处理一个即可，toast
        Toast.makeText(this, "订阅数量不得超过" + Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }
}
