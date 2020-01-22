package com.example.himalaya.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;

public abstract class UILoader extends FrameLayout {

    private View mLoadingView;
    private View mSuccessView;
    private View mNetworkErrorView;
    private View mEmptyView;
    private OnRetryClickListener mOnRetryClickListener = null;

    //定义一个枚举类，用于加载状态
    public enum UIStatus{
        LOADING, SUCCESS, NETWORK_ERROR, EMPTY, NONE
    }
    //定义一个变量表示当前状态
    public UIStatus mCurrentStatus = UIStatus.NONE;
    //使用FrameLayout，因为布局干净没经过怎么处理
    public UILoader(@NonNull Context context) {
        this(context, null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //保证唯一的入口
        init();
    }

    //需要传入一个枚举类型的UIStatus
    public void updateStatus(UIStatus status){
        mCurrentStatus = status;//当前状态值
        //接下来要更新UI,更新UI一定要在主线程上
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }
    //初始化UI
    //相关的View加载进来，创建几个界面
    private void init() {
        //创建界面，首先要去创建一个方法
        switchUIByCurrentStatus();
    }

    private void switchUIByCurrentStatus() {
        //然后把几个界面加载进来
        //设置成员变量进行判断
        //加载中
        if (mLoadingView == null) {
            mLoadingView = getLoadingView();
            addView(mLoadingView);
        }
        //根据状态设置是否可见
        mLoadingView.setVisibility(mCurrentStatus == UIStatus.LOADING? VISIBLE :GONE);
        //成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);//创建一个抽象类有子类实现
            addView(mSuccessView);
        }
        mSuccessView.setVisibility(mCurrentStatus == UIStatus.SUCCESS? VISIBLE :GONE);
        //网络错误
        if (mNetworkErrorView == null) {
            mNetworkErrorView = getNetworkErrorView();
            addView(mNetworkErrorView);
        }
        mNetworkErrorView.setVisibility(mCurrentStatus == UIStatus.NETWORK_ERROR? VISIBLE :GONE);
        //数据为空的界面
        if (mEmptyView == null) {
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }
        mEmptyView.setVisibility(mCurrentStatus == UIStatus.EMPTY? VISIBLE :GONE);
    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
    }

    private View getNetworkErrorView() {
        //这个时候需要找到这个View,然后提取这个代码
        View networkErrorView =  LayoutInflater.from(getContext()).inflate(R.layout.fragment_error_view, this, false);
        //通过这个View来找到这个控件
        //然后设置一个点击事件
        networkErrorView.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
             // 重新去获取数据
                //只有mOnRetryClickListener不为空的时候才去做一些事情
                if (mOnRetryClickListener != null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });
        return networkErrorView;
    }

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view, this, false);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener){
        //设置一个成员变量,默认是空
        this.mOnRetryClickListener = listener;
    }

    //写一个接口用来处理点击调用的事件
    public interface OnRetryClickListener{
        void onRetryClick();
    }
}
