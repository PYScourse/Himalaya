package com.example.himalaya;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.himalaya.adapters.IndicatorAdapter;
import com.example.himalaya.adapters.MainContentAdapter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    private static final String TAG ="MainActivity" ;
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;
    //提取局部变量:Ctrl+Alt+V 提取全局变量:Ctrl+Alt+F

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();//创建方法
        initEven();

    }

    private void initEven() {
        //设置监听
        mIndicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                Log.d(TAG, "click index is -->" + index);
                if (mContentPager != null) {
                    mContentPager.setCurrentItem(index);
                }
            }
        });
    }

    private void initView() {
        mMagicIndicator = this.findViewById(R.id.main_indicator);//找到控件
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));//设置背景颜色
        //创建indicator适配器
        //把this传进去，添加一个参数
        mIndicatorAdapter = new IndicatorAdapter(this);

        //封装一层
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(mIndicatorAdapter);
        //设置要显示的内容

        //ViewPager
        mContentPager = this.findViewById(R.id.content_pager);
        //设置适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();//具有兼容性的
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);

        mContentPager.setAdapter(mainContentAdapter);

        //适配器与其绑定起来
        //把ViewPager和indicator绑定到一起
        //？为什么要绑定一起呢
        //把ViewPager交进去，给它设置一个监听，滑动的监听，这样就可以向下关联
        //下面滑动和上面滑动为一起的

        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);
    }
}
