package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by DF on 2016/12/15.
 */

public class ClassCatalogueAdapter extends RecyclerView.Adapter{
    public Context mContext;
    private ClassHolder classHolder;

    public ClassCatalogueAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_class_catalog, parent, false);
        classHolder = new ClassHolder(view);
        return classHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageLoader.getInstance().loadImage("url", new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                classHolder.ivClass.setImageBitmap(loadedImage);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ClassHolder extends RecyclerView.ViewHolder{
        public ImageView ivClass;
        public TextView tvTitle;
        public TextView tvFree;
        public TextView tvPeople;

        public ClassHolder(View itemView) {
            super(itemView);
            ivClass = (ImageView) itemView.findViewById(R.id.iv_class);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvFree = (TextView) itemView.findViewById(R.id.tv_free_price);
            tvPeople = (TextView) itemView.findViewById(R.id.tv_people_join);
        }
    }
}
