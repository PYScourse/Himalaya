package com.example.himalaya.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.InnerHolder> {

    //内部最好持有一个集合用来保护数据
    private  List<Track> mDetailData = new ArrayList<>();
    //创建一个时间格式的类
    private SimpleDateFormat mUpdateDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListener = null;
    private ItemLongClickListener mItemLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
       //找到控件，设置数据
        final View itemView = holder.itemView;

        //顺序id
        TextView orderTv = itemView.findViewById(R.id.order_text);
        //标题
        TextView titleTv = itemView.findViewById(R.id.detail_item_title);
        //播放次数
        TextView playCount = itemView.findViewById(R.id.detail_item_play_count);
        //时常
         TextView durationTv = itemView.findViewById(R.id.detail_item_duration);
         //更新日期
        TextView updateDataTv = itemView.findViewById(R.id.detail_item_update_time);
        //设置数据
        final Track track = mDetailData.get(position);
        orderTv.setText((position + 1) + "");
        titleTv.setText(track.getTrackTitle());
        playCount.setText(track.getPlayCount() + "");

        int durationMil = track.getDuration() * 1000;
        String duration = mDurationFormat.format(durationMil);
        durationTv.setText(duration);
        String updateTimeText = mUpdateDateFormat.format(track.getUpdatedAt());
        updateDataTv.setText(updateTimeText);



        //设置item的点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //TODO:
               // Toast.makeText(view.getContext(),"you click" + position + "item",Toast.LENGTH_SHORT).show();
                //先判空，
                if (mItemClickListener != null) {
                    //参数需要有列表和位置

                    mItemClickListener.onItemClick(mDetailData, position);
                }
            }
        });


        //
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //返回true表示消费掉该事件
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(track);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        mDetailData.clear() ;
        mDetailData.addAll(tracks);
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public InnerHolder(View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public interface ItemClickListener{
        void onItemClick(List<Track> detailData, int position);
    }


    public void setItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }
    public interface ItemLongClickListener{
        void onItemLongClick(Track track);
    }
}
