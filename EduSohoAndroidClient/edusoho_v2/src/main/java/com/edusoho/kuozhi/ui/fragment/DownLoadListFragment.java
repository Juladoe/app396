package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
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
public class DownLoadListFragment extends BaseFragment {

    private RecyclerView mDownloadView;
    private LinearLayoutManager mLayoutManager;

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

        mDownloadView = (RecyclerView) view.findViewById(R.id.download_recycler_view);
        mLayoutManager = new LinearLayoutManager(mContext);
        mDownloadView.setLayoutManager(mLayoutManager);

        MyAdapter adapter = new MyAdapter(new String[] {"xxx", "ddd"});
        mDownloadView.setAdapter(adapter);
    }


    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private String[] mDataset;

        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.download_list_item_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //holder.mTextView.setText(mDataset[position]);
        }

        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            //mTextView = (TextView) v.findViewById(R.id.download_info_text);
        }
    }
}
