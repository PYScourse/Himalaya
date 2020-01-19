package com.example.himalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.himalaya.MainActivity;
import com.example.himalaya.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

//继承内部类实现这个方法
public class IndicatorAdapter extends CommonNavigatorAdapter {

    private final String[] mTitles;
    private OnIndicatorTapClickListener mOnTapClickListener;

    public IndicatorAdapter(Context context) {
        //初始化的时候就要拿到数据
        mTitles = context.getResources().getStringArray(R.array.indicator_title);

    }

    @Override
    public int getCount() {
        //返回它的个数
        if (mTitles != null) {
            return mTitles.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        //创建view
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
        //设置一般情况下的颜色为灰色
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        //设置选中情况下为黑色
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        //单位sp
        colorTransitionPagerTitleView.setTextSize(18);
        //设置要显示的内容
        colorTransitionPagerTitleView.setText(mTitles [index]);
        //设置title的点击事件，如果点击了title，那么就选中下面viewPager到对应的index里面去
        //当我们点击title的时候，下面的viewPager会对应着index进行切换内容
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //切换viewPager的内容，如果index不一样的话
                if (mOnTapClickListener != null) {
                    mOnTapClickListener.onTabClick(index);
                }
            }
        });
        //把这个创建好的view返回回去
        return colorTransitionPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        indicator.setColors(Color.parseColor("#ffffff"));
        return indicator;
    }

    public void setOnIndicatorTapClickListener(OnIndicatorTapClickListener listener){
        this.mOnTapClickListener = listener;
    }

    public interface OnIndicatorTapClickListener{
        void onTabClick(int index);//索引的位置

    }
}
