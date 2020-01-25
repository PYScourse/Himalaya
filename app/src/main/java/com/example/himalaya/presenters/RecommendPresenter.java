package com.example.himalaya.presenters;

import android.support.annotation.Nullable;

import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {
    private static final String TAG = "RecommendPresenter";

    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();

    //构造方法私有化
    private RecommendPresenter(){

    }

    private static RecommendPresenter sInstance = null;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static RecommendPresenter getInstance(){
        //懒汉式单例
        if (sInstance==null) {
            synchronized (RecommendPresenter.class){
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }
    //提供一个公开的静态的获取对象的方法


    /**
     * 获取推荐内容，其实就是猜你喜欢
     * 这个接口：3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //为了使内容返回进来要注册一个接口进来
        //获取推荐内容
        //封装参数
        updateLoading();
        Map<String, String> map = new HashMap<>();
        //这个数据表示一页参数返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND + "");//转化成字符串
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                //这里是数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList != null) {
                        //数据回来以后，我们要去更新UI
                        //upRecommendUI(albumList);
                        //通知
                        handlerRecommendResult(albumList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                //这里是数据获取出错
                LogUtil.d(TAG, "error  -- >  " + i);
                LogUtil.d(TAG, "errorMsg  -- >  " + s);
                handlerError();
            }
        });
    }

    private void handlerError() {
        if (mCallbacks != null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onNetworkError();//把albumList传出去
            }
        }
    }


    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI更新
        if (albumList != null) {
           /* //测试， 清空一下，让界面显示为空
            albumList.clear();*/
            if (albumList.size() == 0) {
                //判断数据为空
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onEmpty();//把albumList传出去
                }
            }else {
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoaded(albumList);//把albumList传出去
                }
            }
        }
    }


    private void updateLoading(){
        for (IRecommendViewCallback callback : mCallbacks) {
            callback.onLoading();//把albumList传出去
        }
    }


    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks!=null && !mCallbacks.contains(callback)) {
            //防止重复加入
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        //清除
        if (mCallbacks != null) {
            mCallbacks.remove(mCallbacks);
        }
    }
}
