package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.util.ArrayList;

/**
 * Created by hby on 14-11-7.
 */
public class ErrorAdapter<T> extends ListBaseAdapter<T> {
    private Context mContext;
    private String mErrorText;
    private View.OnClickListener mOnClickListener;

    public ErrorAdapter(Context context, T[] array, int layoutId, View.OnClickListener onClickListener) {
        super(context, layoutId);
        this.mContext = context;
        for (T text : array) {
            mList.add(text);
        }
        this.mOnClickListener = onClickListener;
    }

    @Override
    public void addItems(ArrayList list) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(mResource, null);
        }

        TextView tvError = (TextView) convertView.findViewById(R.id.list_error_text);
        Button btnError = (Button) convertView.findViewById(R.id.list_error_btn);
        tvError.setText((String) mList.get(position));
        if (mOnClickListener != null) {
            btnError.setOnClickListener(mOnClickListener);
        }

        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        layoutParams.height = parent.getHeight();
        convertView.setLayoutParams(layoutParams);

        return convertView;
    }

    public void setErrorClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
