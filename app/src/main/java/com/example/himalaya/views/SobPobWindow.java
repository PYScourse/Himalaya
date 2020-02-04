package com.example.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.PlayListAdapter;
import com.example.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class SobPobWindow extends PopupWindow {

    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTracksList;
    private PlayListAdapter mPlayListAdapter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mPlayContainer;
    private PlayListActionListener mPlayModeClickListener = null;
    private View mOrderBtnContainer;
    private ImageView mOrderIcon;
    private TextView mOrderText;

    public SobPobWindow(){
        //设置它的宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //这里要注意，设置setOutsideTouchable要先设置setBackgroundDrawable
        //否则点击外部无法关闭pop
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);

        //载View
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);
        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEven();
    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        //先找到控件
        mTracksList = mPopView.findViewById(R.id.play_list_rv);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTracksList.setLayoutManager(layoutManager);
        //设置适配器
        //TODO:
        mPlayListAdapter = new PlayListAdapter();
        mTracksList.setAdapter(mPlayListAdapter);
        //播放模式相关
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlayContainer = mPopView.findViewById(R.id.play_list_play_mode_container);
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderIcon = mPopView.findViewById(R.id.play_list_order_iv);
        mOrderText = mPopView.findViewById(R.id.play_list_order_tv);
    }

    private void initEven() {
        //点击以后窗口消失
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPobWindow.this.dismiss();
            }
        });

        mPlayContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mPlayModeClickListener 切换播放模式
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onPlayModeClick();
                }
            }
        });
        mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:切换播放列表为顺序或者逆序
                mPlayModeClickListener.onOrderClick();
            }
        });
    }

    /**
     * 适配器设置数据
     * @param data
     */
    public void setListData(List<Track> data){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(data);
        }
    }


    public void setCurrentPlayPosition(int position){
        if (mPlayListAdapter != null) {
           mPlayListAdapter.setCurrentPlayPosition(position);
           mTracksList.scrollToPosition(position);
        }
    }


    //设置方法
    public void setPlayListItemClickListener(PlayListItemClickListener listener){
        //
        mPlayListAdapter.setOnItemClickListener(listener);
    }

    /**
     * 更新播放列表的播放模式
     * @param currentMode
     */
    public void updatePlayMode(XmPlayListControl.PlayMode currentMode) {
        updatePlayModeBtnImg(currentMode);
    }
    //暴露方法去更新UI

    /**
     * 更新切换列表顺序和逆序的按钮和文字
     * @param isReverse
     */
    public void updateOrderIcon(boolean isReverse){
        mOrderIcon.setImageResource(isReverse?R.drawable.selector_play_mode_list_order:R.drawable.selector_play_mode_list_revers);
        mOrderText.setText(isReverse?"顺序":"逆序");
    }


    /**
     * 根据当前状态更新播放模式图标
     *  PLAY_MODEL_LIST
     *  PLAY_MODEL_LIST_LOOP
     *  PLAY_MODEL_RANDOM
     *  PLAY_MODEL_SINGLE_LOOP
     */
    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode playMode) {
        int resId = R.drawable.selector_play_mode_list_order;
        int textId = R.string.play_mode_order_text;
        switch (playMode){
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list_order;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_player_mode_list_order_ramdom;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_player_mode_list_order_looper;
                textId = R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_player_mode_list_order_single_loop;
                textId = R.string.play_mode_single_play_text;
                break;
        }
        mPlayModeIv.setImageResource(resId);
        mPlayModeTv.setText(textId);
    }

    //定义接口给外面用
    public interface PlayListItemClickListener{
        void onItemClick(int position);
    }

    public void setPlayListActionListener(PlayListActionListener playModeListener){
        mPlayModeClickListener = playModeListener;
    }

    public interface PlayListActionListener {
        //播放模式被点击
        void onPlayModeClick();
        //播放顺序或者逆序被点击
        void onOrderClick();

    }
}
