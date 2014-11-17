package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;


/**
 * Created by howzhi on 14/11/10.
 */
public class DownLoadListFragment extends BaseFragment{

    private RecyclerView mDownloadRView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public String getTitle() {
        return "download";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.download_list_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mDownloadRView =(RecyclerView) view.findViewById(R.id.download_recycler_view);

        mLayoutManager = new LinearLayoutManager(mContext);

        DownloadListAdapter adapter = new DownloadListAdapter(new String[] {"xxx", "ddd" }, R.layout.download_list_item_layout);
        mDownloadRView.setLayoutManager(mLayoutManager);
        mDownloadRView.setAdapter(adapter);
    }

    private class DownloadListAdapter extends RecyclerView.Adapter<ViewHolder>
    {
        private int mResource;

        public DownloadListAdapter(String[] data, int resource)
        {
            this.data = data;
            this.mResource = resource;
        }

        private String[] data;

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.mTextView.setText(data[i]);
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(mContext).inflate(mResource, null);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextView;
        private CardView mCardView;

        public ViewHolder(View view)
        {
            super(view);
            //mTextView = (TextView) view.findViewById(R.id.info_text);
        }
    }
}
