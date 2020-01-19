package com.example.himalaya.utils;

import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.fragments.HistoryFragment;
import com.example.himalaya.fragments.RecommendFragment;
import com.example.himalaya.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentCreator {
    //先创建一个集合列表
    public final static int INDEX_RECOMMEND = 0; //创建常量
    public final static int INDEX_SUBSCRIPTION = 1; //创建常量
    public final static int INDEX_HISTORY = 2; //创建常量
    public final static int PAGE_COUNT = 3;

    private static Map<Integer, BaseFragment> sCache = new HashMap<>();

    //形成一个键值对，可以更好的使用了
    //然后我们提供一个方法
    public static BaseFragment getFragment(int index) {
        BaseFragment baseFragment = sCache.get(index);
        //先从缓存拿
        if (baseFragment != null) {
            return baseFragment;
        }
        switch (index) {
            //否则就创建
            case INDEX_RECOMMEND:
                baseFragment = new RecommendFragment();
                break;
            case INDEX_SUBSCRIPTION:
                baseFragment = new SubscriptionFragment();
                break;
            case INDEX_HISTORY:
                baseFragment = new HistoryFragment();
                break;
        }
        sCache.put(index, baseFragment);
        return baseFragment;
    }
}
