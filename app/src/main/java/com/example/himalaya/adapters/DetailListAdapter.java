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

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {

    //内部最好持有一个集合用来保护数据
    private  List<Track> mDetailData = new ArrayList<>();
    //创建一个时间格式的类
    private SimpleDateFormat mUpdateDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
       //找到控件，设置数据
        View itemView = holder.itemView;

        //顺序id
        TextView orderTv = itemView.findViewById(R.id.order_text);
        //标题
        TextView titleTv = itemView.findViewById(R.id.detail_item_title);
        //播放次数
        TextView playCoutn = itemView.findViewById(R.id.detail_item_play_count);
        //时常
         TextView durationTv = itemView.findViewById(R.id.detail_item_duration);
         //更新日期
        TextView updataDataTv = itemView.findViewById(R.id.detail_item_update_time);
        //设置数据
        Track track = mDetailData.get(position);
        orderTv.setText(position + "");
        titleTv.setText(track.getTrackTitle());
        playCoutn.setText(track.getPlayCount() + "");

        int durationMil = track.getDuration() * 1000;
        String duration = mDurationFormat.format(durationMil);
        durationTv.setText(duration);
        String updateTimeText = mUpdateDateFormat.format(track.getUpdatedAt());
        updataDataTv.setText(updateTimeText);
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
}
