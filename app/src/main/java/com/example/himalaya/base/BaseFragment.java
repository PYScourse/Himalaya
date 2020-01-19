package com.example.himalaya.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public  abstract class BaseFragment extends Fragment {
    private View mRootView;
    //在这里完成内容的加载


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = onSubViewLoaded(inflater,container );//绑定到父类里面去
        return mRootView;
    }


    protected abstract View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container);
    //创建一个抽象方法
}
