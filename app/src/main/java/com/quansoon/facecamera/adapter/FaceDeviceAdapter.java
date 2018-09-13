package com.quansoon.facecamera.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quansoon.facecamera.R;
import com.quansoon.facecamera.model.FaceDeviceBean;

import java.util.List;

/**
 * @author: Caoy
 * @created on: 2018/9/3 15:54
 * @description:
 */
public class FaceDeviceAdapter extends ArrayAdapter<FaceDeviceBean> {


    @NonNull
    private final List<FaceDeviceBean> deviceBeanList;

    public FaceDeviceAdapter(@NonNull Context context, @NonNull List<FaceDeviceBean> deviceBeanList) {
        super(context,R.layout.item_face_device,deviceBeanList);
        this.deviceBeanList = deviceBeanList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_face_device, parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mTvItemFaceDevice.setText(deviceBeanList.get(position).getDeviceAddress());

        return convertView;
    }

    static class ViewHolder {
        View view;
        TextView mTvItemFaceDevice;

        ViewHolder(View view) {
            this.view = view;
            this.mTvItemFaceDevice = (TextView) view.findViewById(R.id.tv_item_face_device);
        }
    }
}
