package com.example.administrator.attendanceinputv3.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.administrator.attendanceinputv3.R;
import com.example.administrator.attendanceinputv3.model.PersonModel;
import com.example.administrator.attendanceinputv3.utils.StringUtils;
import com.example.administrator.attendanceinputv3.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 云中双月 on 2018/3/30.
 */

public class HomeRecyAdapter extends RecyclerView.Adapter<HomeRecyAdapter.HomeHolder> {
    private List<PersonModel> modelList = new ArrayList<>();
    private RequestOptions options;
    private Context context;

    public HomeRecyAdapter(List<PersonModel> models, RequestOptions options) {
        modelList = models;
        this.options = options;
    }

    @Override
    public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home,parent,false);
        context = parent.getContext();
        HomeHolder holder = new HomeHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HomeHolder holder, int position) {
        //是否对比出结果了
        if (position == 0) {
            holder.layBg.setBackground(ContextCompat.getDrawable(context, R.mipmap.img_date_new));
        } else {
            holder.layBg.setBackground(ContextCompat.getDrawable(context, R.mipmap.img_date));
        }

        holder.name.setText(modelList.get(position).getName());
        if (StringUtils.isEmpty(modelList.get(position).getIoTimeStr())) {
            holder.time.setText(context.getString(R.string.str_));
        }else {
            holder.time.setText(modelList.get(position).getName());
        }

        if (context != null) {
            Glide.with(context)
                    .load(modelList.get(position).getVerifyFacImgUrl())
                    .apply(options)
                    .into(holder.head);
        }
    }

    public void remove(int position) {
        modelList.remove(position);
        notifyItemRemoved(position);
    }

    public void add(PersonModel model, int position) {
        modelList.add(position, model);
        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return modelList == null ? 0 : modelList.size();
    }

    public void addAll(ArrayList<PersonModel> list) {
        modelList.clear();
        modelList.addAll(list);
        notifyDataSetChanged();
    }

    public static class HomeHolder extends RecyclerView.ViewHolder {
        TextView name, time;
        ImageView head;
        RelativeLayout layBg;

        public HomeHolder(View itemView) {
            super(itemView);
            layBg = itemView.findViewById(R.id.lay_bg);
            name = itemView.findViewById(R.id.tv_username);
            time = itemView.findViewById(R.id.tv_time);
            head = itemView.findViewById(R.id.iv_head);
        }
    }


}
