package com.example.himalaya.interfaces;

import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallback {

    /**
     * 调用添加的时候去通知UI结果
     *
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);


    /**
     * 删除订阅的回调方法
     *
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);


    /**
     * 获取专辑加载的结果回调
     *
     * @param albums
     */
    void onSubscriptionsLoaded(List<Album> albums);

    /**
     * 订阅数量满了
     *
     */
    void onSubFull();
}
