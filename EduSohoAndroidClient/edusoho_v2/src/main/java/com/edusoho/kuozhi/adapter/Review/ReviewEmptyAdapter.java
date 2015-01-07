package com.edusoho.kuozhi.adapter.Review;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerEmptyAdapter;

/**
 * Created by howzhi on 14/12/30.
 */
public class ReviewEmptyAdapter extends RecyclerEmptyAdapter {

    public ReviewEmptyAdapter(
            Context context, int resource, String[] emptyTexts)
    {
        super(context, resource, emptyTexts);
    }

    public ReviewEmptyAdapter(
            Context context, int resource, String[] emptyTexts, int emptyIcon)
    {
        super(context, resource, emptyTexts, emptyIcon);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final int index = i;
        if (mRecyclerItemClick != null) {
            View view = viewHolder.itemView.findViewById(R.id.course_review_btn);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecyclerItemClick.onItemClick(mList.get(index), index);
                }
            });
        }
    }
}
