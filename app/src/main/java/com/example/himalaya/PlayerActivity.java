package com.example.himalaya;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.himalaya.adapters.PlayerTrackPagerAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.SobPobWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgressBar = false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreBtn;
    private TextView mTrackTitleTv;
    private String mTrackTitleText;
    private ViewPager mTrackPageView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserSlidePager = false;
    private ImageView mPlayerModeSwitchBtn;

    private XmPlayListControl.PlayMode mCurrentMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;



    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();

    static {
        //处理播放模式的切换
        //1.默认的是：PLAY_MODEL_LIST
        //2.列表循环：PLAY_MODEL_LIST_LOOP
        //3.随机播放：PLAY_MODEL_RANDOM
        //4.单曲循环：PLAY_MODEL_SINGLE_LOOP
        sPlayModeRule.put(PLAY_MODEL_LIST,PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP,PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM,PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP,PLAY_MODEL_LIST);
    }

    private View mPlayListBtn;
    private SobPobWindow mSobPobWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimator;
    public final int BG_ANIMATION_DURATION = 800;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //一进来就播放//测试
        initView();

        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

        //找到方法
        //在界面初始化以后，才获取数据
        mPlayerPresenter.getPlayList();
        //设置监听的方法
        initEvent();

        initBgAnimation();
    }

    private void initBgAnimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f, 0.7f);
        mEnterBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                LogUtil.d(TAG,"value --- > " );
                //处理一下背景，有点透明,播放列表消失以后恢复正常
                updateBgAlpha(value);
            }
        });

        mOutBgAnimator = ValueAnimator.ofFloat(0.7f, 1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                LogUtil.d(TAG,"value --- > " );
                //处理一下背景，有点透明,播放列表消失以后恢复正常
                updateBgAlpha(value);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }


    /**
     * 给控件设置相关的控件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:
                if (mPlayerPresenter.isPlay()) {
                    //如果现在的状态是正在播放的，那么就暂停
                    mPlayerPresenter.pause();
                } else {
                    mPlayerPresenter.play();
                    //如果现在的状态是非播放的，那么我们久让播放器播放节目
                }
            }
        });
        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手离开拖动进度条的时候更新进度
                mIsUserTouchProgressBar = false;
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });


        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放前一个节目
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });

        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一个节目
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });


        //设置监听事件
        mTrackPageView.addOnPageChangeListener(this);
        //设置事件
        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                        break;
                }
                return false;
            }
        });


        mPlayerModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:展示播放列表
                mSobPobWindow.showAtLocation(v, Gravity.BOTTOM,0, 0);

                //修改背景的透明度，有一个渐变的过程

                mEnterBgAnimator.start();
            }
        });
        mSobPobWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //窗体消失以后，恢复透明度
                mOutBgAnimator.start();
            }
        });

        mSobPobWindow.setPlayListItemClickListener(new SobPobWindow.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
               //说明播放列表里的item被点击了
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });
        mSobPobWindow.setPlayListActionListener(new SobPobWindow.PlayListActionListener() {
            @Override
            public void onPlayModeClick() {
                //切换播放模式
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                //点击了切换顺序和逆序
                Toast.makeText(PlayerActivity.this, "切换列表顺序",Toast.LENGTH_SHORT).show();
                mSobPobWindow.updateOrderIcon(!textOrder);
                textOrder = !textOrder;
            }
        });
    }

    private boolean textOrder = false;

    private void switchPlayMode() {
        //根据的mode获取下一个mode
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
        //修改播放模式
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    public void updateBgAlpha(float alpha){
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    /**
     * 根据当前状态更新播放模式图标
     *  PLAY_MODEL_LIST
     *  PLAY_MODEL_LIST_LOOP
     *  PLAY_MODEL_RANDOM
     *  PLAY_MODEL_SINGLE_LOOP
     */
    private void updatePlayModeBtnImg() {
        int resId = R.drawable.selector_play_mode_list_order;
        switch (mCurrentMode){
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list_order;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_player_mode_list_order_ramdom;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_player_mode_list_order_looper;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_player_mode_list_order_single_loop;
                break;
        }
        mPlayerModeSwitchBtn.setImageResource(resId);
    }

    /**
     * 找到各个控件
     */
    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        mPlayNextBtn = this.findViewById(R.id.play_next);
        mPlayPreBtn = this.findViewById(R.id.play_pre);
        mTrackTitleTv = this.findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitleTv.setText(mTrackTitleText);
        }
        mTrackPageView = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTrackPageView.setAdapter(mTrackPagerAdapter);

        //找到切换模式的按钮
        mPlayerModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);

        //播放列表
        mPlayListBtn = this.findViewById(R.id.player_list);

        //
        mSobPobWindow = new SobPobWindow();

    }

    @Override
    public void onPlayStart() {
        //开始播放，修改UI层暂停的按钮
        //异步需要判空
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
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
        //把数据设置到适配器里面
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
        //数据回来以后，也要给节目列表一份
        if (mSobPobWindow != null) {
            mSobPobWindow.setListData(list);
        }

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式，并且修改UI
        mCurrentMode = playMode;
        //更新pop里的播放模式
        mSobPobWindow.updatePlayMode(mCurrentMode);
        updatePlayModeBtnImg();
    }

    @Override
    public void onProgressChange(int currentDuration, int total) {
        //更新播放进度， 更新播放进度条
        mDurationBar.setMax(total);
        String totalDuration;
        String currentPosition;
        if (total > 1000 * 60 * 60) {
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentDuration);
        } else {
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentDuration);
        }
        //更新时间
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
        //更新进度和的当前时间
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //更新进度
        //计算进度
        if (!mIsUserTouchProgressBar) {
            mDurationBar.setProgress(currentDuration);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitleTv != null) {
            //设置当前界面的标题
            mTrackTitleTv.setText(mTrackTitleText);
        }
        //当前节目改变的时候，我们就获取当前播放中播放位置
        //当前的节目改变以后，要修改页面的图片
        if (mTrackPageView != null) {
            mTrackPageView.setCurrentItem(playIndex, true);
        }
        //修改播放列表里的播放位置
        if (mSobPobWindow != null) {
            mSobPobWindow.setCurrentPlayPosition(playIndex);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //当页面选中时候，就去切换播放的内容
        LogUtil.d(TAG, "position -- >" + position);
        if (mPlayerPresenter != null && mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
