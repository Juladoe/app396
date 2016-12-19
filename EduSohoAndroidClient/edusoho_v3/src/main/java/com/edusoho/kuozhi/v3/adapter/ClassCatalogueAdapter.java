package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.lesson.ClassCatalogue;

/**
 * Created by DF on 2016/12/15.
 */

public class ClassCatalogueAdapter extends BaseAdapter{
    public Context mContext;
    public ClassCatalogue mClassCatalogue;
    private ClassHolder classHolder;

    public ClassCatalogueAdapter(Context mContext, ClassCatalogue mClassCatalogue) {
        this.mContext = mContext;
        this.mClassCatalogue = mClassCatalogue;
    }

    @Override
    public int getCount() {
        mClassCatalogue.get
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_class_catalog, parent, false);
//        classHolder = new ClassHolder(view);
//        return classHolder;
//
//
//        ImageLoader.getInstance().loadImage("url", new SimpleImageLoadingListener(){
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                super.onLoadingComplete(imageUri, view, loadedImage);
//                classHolder.mIvClass.setImageBitmap(loadedImage);
//            }
//        });
        return null;
    }

    public static class ClassHolder extends RecyclerView.ViewHolder{
        public ImageView mIvClass;
        public TextView mTvTitle;
        public TextView mTvFree;
        public TextView mTvPeople;

        public ClassHolder(View itemView) {
            super(itemView);
            mIvClass = (ImageView) itemView.findViewById(R.id.iv_class);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvFree = (TextView) itemView.findViewById(R.id.tv_free_price);
            mTvPeople = (TextView) itemView.findViewById(R.id.tv_people_join);
        }
    }
}
