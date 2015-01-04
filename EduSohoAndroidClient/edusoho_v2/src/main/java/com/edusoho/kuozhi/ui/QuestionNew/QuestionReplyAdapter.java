package com.edusoho.kuozhi.ui.QuestionNew;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by onewoman on 2014/12/30.
 */
public class QuestionReplyAdapter extends ListBaseAdapter<String>{

    public QuestionReplyAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void addItems(ArrayList<String> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View mQuestionReplyLoadView;
        ImageView imageView;
        if(view == null) {
            view = inflater.inflate(mResource, null);
        }
        imageView = (ImageView) view.findViewById(R.id.question_answer_content_image);
        mQuestionReplyLoadView = view.findViewById(R.id.load_layout);

        String imgUrl = mList.get(i);
        ImageLoader.getInstance().displayImage(imgUrl,imageView);
        mQuestionReplyLoadView.setVisibility(View.GONE);
        return view;
    }
}
