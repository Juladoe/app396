package com.edusoho.kuozhi.adapter.appPlugin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.model.appPlugin.AppPlugin;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by howzhi on 14/12/2.
*/
public class AppPluginAdapter
        extends RecyclerViewListBaseAdapter<AppPlugin, AppPluginAdapter.ViewHolder> {

    private ActionBarBaseActivity mActivity;
    private DisplayImageOptions mOptions;
    public AppPluginAdapter(ActionBarBaseActivity activity, int resource)
    {
        super(activity, resource);
        this.mActivity = activity;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .showImageOnFail(R.drawable.icon).showImageForEmptyUri(R.drawable.icon).build();
    }

    @Override
    public void addItem(AppPlugin item) {
        mList.add(item);
        notifyItemInserted(mList.size() - 1);
    }

    @Override
    public void addItems(List<AppPlugin> list) {
        mList.addAll(list);
        notifyItemRangeInserted(mList.size() - 1 - list.size(), mList.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        AppPlugin appPlugin = mList.get(i);

        viewHolder.mNameView.setText(appPlugin.name);
        viewHolder.mVersionView.setText(appPlugin.version);
        viewHolder.mDescriptionView.setText(appPlugin.description);
        viewHolder.mAuthorView.setText(appPlugin.author);
        ImageLoader.getInstance().displayImage(appPlugin.icon, viewHolder.mIconView, mOptions);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mNameView;
        public TextView mVersionView;
        public TextView mAuthorView;
        public TextView mDescriptionView;
        public ImageView mIconView;
        public ViewHolder(View view){
            super(view);

            mNameView = (TextView) view.findViewById(R.id.app_plugin_name);
            mAuthorView = (TextView) view.findViewById(R.id.app_plugin_author);
            mVersionView = (TextView) view.findViewById(R.id.app_plugin_version);
            mDescriptionView = (TextView) view.findViewById(R.id.app_plugin_description);
            mIconView = (ImageView) view.findViewById(R.id.app_plugin_icon);
        }
    }
}
