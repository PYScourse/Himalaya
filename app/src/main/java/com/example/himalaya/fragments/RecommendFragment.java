package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.DetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, RecommendListAdapter.OnRecommendItemClickListener {

    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, final ViewGroup container) {

        mUiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater, container);
            }
        };


        //去拿数据回来
        //获取到逻辑层的对象       getRecommendData();

        //获取到对象，设置成成员变量
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口的注册
        //让本类来实现它，fragment来实现这个类
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();
        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }
        //mUiLoader有了之后我们就要设置一个监听,实现方法
        mUiLoader.setOnRetryClickListener(this);
        return mUiLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //View加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
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
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                //dp -> px
                outRect.top = UIUtil.dip2px(view.getContext(), 5);//magicindicator.buildins,一个工具类
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);

            }
        });

        //3.设置适配器
        mRecommendListAdapter = new RecommendListAdapter();//设置为成员变量，等会要设置数据
        mRecommendRv.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setOnRecommendItemClickListener(this);//让这个类实现当前这个接口
        return mRootView;

    }


    @Override
    public void onRecommendListLoaded(List<Album> result) {
        LogUtil.d(TAG, "onRecommendListLoaded");
        //当我们获取推荐内容的时候，这个方法就会被调用(成功了)
        //数据回来以后，就是更新UI了
        //这里需要把数据和UI结合起来，把View设置成成员变量
        //把数据设置给适配器，并更新
        mRecommendListAdapter.setData(result);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        LogUtil.d(TAG, "onNetworkError");
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        LogUtil.d(TAG, "onEmpty");
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        LogUtil.d(TAG, "onLoading");
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册，避免内存泄漏
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //表示网络不佳的时候用户点击了重试，重试只有一个内容
        //重新加载推荐内容
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        //根据位置拿到数据
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击了,跳转到详情界面,之后就要绑定回调接口
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
