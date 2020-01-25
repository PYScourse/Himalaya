package com.example.himalaya.presenters;

import android.nfc.Tag;
import android.support.annotation.Nullable;

import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {
    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();
    private Album mTargetAlbum = null;

    //懒汉式单例模式
    private AlbumDetailPresenter(){
    }
    private static AlbumDetailPresenter sInstance = null;

    public static AlbumDetailPresenter getInstance(){
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class){
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

    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        //去根据页码和专辑id获取列表
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page +"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT +"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    //不能再子线程更新控件
                    //tracks是能够播放的内容
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, "tracks  size -->" + tracks.size());
                    //通过集合来通知UI更新
                    //添加一个参数
                    handlerAlbumDetailResult(tracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode -- >" + errorCode);
                LogUtil.d(TAG, "errorMsg -- >" + errorMsg);
                handlerError(errorCode, errorMsg);
            }
        });
    }

    /**
     * 这样数据就可以回去UI上了
     * 如果是发生错误，就通知UI
     * @param errorCode
     * @param errorMsg
     */
    private void handlerError(int errorCode, String errorMsg) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onNetWorkError(errorCode,errorMsg);
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
                detailViewCallback.onAlbumLoad(mTargetAlbum);
            }
        }
    }

    @Override
    public void unregisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        mCallbacks.remove(detailViewCallback);
    }

    //加载的目标的一个专辑
    public void setTargetAlbum(Album targetAlbum){
      this.mTargetAlbum = targetAlbum;
    }
}
