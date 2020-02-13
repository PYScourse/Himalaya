package com.example.himalaya.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private  List<Album> mData = new ArrayList<>();
    private static final String TAG = "AlbumListAdapter";
    private OnAlbumItemClickListener mItemClickListener = null;
    private OnAlbumItemLongClickListener mLongClickListener =null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //先创建内部类，再实现方法
        //这块是创建每个item
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        //把itemView封装进去
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //在这里设置数据
        holder.itemView.setTag(position);//把当前的位置设置给item,所有的View都有setTag的方法的
        //在这里面设置点击事件，通过item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    int clickPosition = (int) v.getTag();
                    mItemClickListener.onItemClick(clickPosition,mData.get(clickPosition));
                }
                //设置log,观察点击的对不对
                Log.d(TAG, "  holder.itemView   click ---> " + v.getTag());
            }
        });
        //在这里加上一个值
        holder.setData(mData.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //长按要取消判空
                if (mLongClickListener != null) {
                    int clickPosition = (int) v.getTag();
                    mLongClickListener.onItemLongClick(mData.get(clickPosition));
                }
                //true表示消费掉该事件
                return true;

            }
        });
    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        //设置数据
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新UI
        notifyDataSetChanged();

    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        //再实现构造方法
        public InnerHolder(View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到各个控件，设置数据
            //专辑的封面
            ImageView albumCoverTv = itemView.findViewById(R.id.album_cover);
            //描述
            TextView albumDesTv = itemView.findViewById(R.id.album_description_tv);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //播放的数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);
            albumTitleTv.setText(album.getAlbumTitle());
            albumDesTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(album.getPlayCount() +"");
            albumContentCountTv.setText(album.getIncludeTrackCount() + "");
            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Picasso.with(itemView.getContext()).load(coverUrlLarge).into(albumCoverTv);
            }else {
                albumCoverTv.setImageResource(R.mipmap.logo);
            }
        }
    }
    public void setAlbumItemClickListener(OnAlbumItemClickListener listener){
        this.mItemClickListener = listener;
    }
    /**
     * item长按的接口
     */
    public interface OnAlbumItemClickListener {
        void onItemClick(int position, Album album);//把位置传出去
        //

    }


    public void setOnAlbumItemLongClickListener(OnAlbumItemLongClickListener listener){
        this.mLongClickListener = listener;
    }
    /**
     * item 长按的接口
     */
    public interface OnAlbumItemLongClickListener{
        void onItemLongClick(Album album);
    }
}
