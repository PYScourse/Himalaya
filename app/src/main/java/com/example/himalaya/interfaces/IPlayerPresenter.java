package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {
    /**
     *播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 播放上一首
     */
    void playPre();

    /**
     * 播放下一首
     */
    void playNext();

    /**
     * 切换播放模式
     * @param mode
     */
    void switchPlayMode(XmPlayListControl.PlayMode mode);

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 根据节目的位置进行播放
     * @param index
     */
    void playByIndex(int index);

    /**
     * 切换进度
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 判断播放器是否在播放
     */
    boolean isPlaying();

    /**
     * 把播放器列表反转
     */
    void reversePlayList();


    /**
     * 播放专辑的第一个节目
     * @param id
     */
    void playByAlbumId(long id);
}
