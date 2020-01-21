package com.example.himalaya.fragments;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {

    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        //View加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, null);
        //返回View,给界面显示
        //RecycleView的使用
        //1.找到对应的控件
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //设置方向
        //设置成垂直方向
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                //dp -> px
                outRect.top = UIUtil.dip2px(view.getContext(),5);//magicindicator.buildins,一个工具类
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);

            }
        });

        //3.设置适配器
        mRecommendListAdapter = new RecommendListAdapter();//设置为成员变量，等会要设置数据
        mRecommendRv.setAdapter(mRecommendListAdapter);
        //去拿数据回来
        //获取到逻辑层的对象       getRecommendData();

        //获取到对象，设置成成员变量
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口的注册
        //让本类来实现它，fragment来实现这个类
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();
        return mRootView;
    }



    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当我们获取推荐内容的时候，这个方法就会被调用(成功了)
        //数据回来以后，就是更新UI了
        //这里需要把数据和UI结合起来，把View设置成成员变量
        //把数据设置给适配器，并更新
        mRecommendListAdapter.setData(result);
    }

    @Override
    public void onLoaderMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册，避免内存泄漏
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }
}
