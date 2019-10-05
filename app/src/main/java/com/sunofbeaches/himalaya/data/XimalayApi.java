package com.sunofbeaches.himalaya.data;

import com.sunofbeaches.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class XimalayApi {

    private XimalayApi() {
    }

    private static XimalayApi sXimalayApi;

    public static XimalayApi getXimalayApi() {
        if (sXimalayApi == null) {
            synchronized (XimalayApi.class) {
                if (sXimalayApi == null) {
                    sXimalayApi = new XimalayApi();
                }
            }
        }
        return sXimalayApi;
    }

    /**
     * 获取推荐内容
     *
     * @param callback 请求结果的回调接口
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        //这个参数表示一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND + "");
        CommonRequest.getGuessLikeAlbum(map, callback);
    }

    /**
     * 根据专辑的id获取到专辑内容.
     *
     * @param callback  获取专辑详情的回调接口
     * @param albumId   专辑的id
     * @param pageIndex 第几页
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callback, long albumId, int pageIndex) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.PAGE, pageIndex + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, callback);
    }

    /**
     * 根据关键字，进行搜索。
     *
     * @param keyword
     */
    public void searchByKeyword(String keyword, int page, IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getSearchedAlbums(map, callback);
    }


    /**
     * 获取推荐的热词
     *
     * @param callback
     */
    public void getHotWords(IDataCallBack<HotWordList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, String.valueOf(Constants.COUNT_HOT_WORD));
        CommonRequest.getHotWords(map, callback);
    }

    /**
     * 根据关键字获取联想词
     *
     * @param keyword  关键字
     * @param callback 回调
     */
    public void getSuggestWord(String keyword, IDataCallBack<SuggestWords> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map, callback);
    }
}
