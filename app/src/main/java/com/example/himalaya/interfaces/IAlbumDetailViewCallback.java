package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallback {
    //只需要处理更新列表，UI都已经搭建好了

    /**
     * 专辑详情内容加载出来了
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 把album传给UI使用
     * @param album
     */
    void onAlbumLoad(Album album);


    /**
     * 网络错误
     * @param errorCode
     * @param errorMsg
     */
    void onNetWorkError(int errorCode, String errorMsg);


    /**
     * 加载更多的结果
     * @param size  size > 0 表示加载成功
     */
    void onLoaderMoreFinished(int size);

    /**
     * 下拉加载更多
     * @param size  size > 0 表示加载成功
     */
    void onRefreshFinished(int size);

}
