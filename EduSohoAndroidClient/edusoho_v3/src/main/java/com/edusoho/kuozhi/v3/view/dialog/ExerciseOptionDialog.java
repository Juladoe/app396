package com.edusoho.kuozhi.v3.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.util.Const;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melomelon on 2015/10/10.
 */
public class ExerciseOptionDialog extends Dialog {

    private Context mContext;
    private GridView mGridView;
    private int mLessonId;

    public View.OnClickListener mClickListener;

    public ExerciseOptionDialog(Context context, int lessonId) {
        super(context, R.style.FullDialogTheme);
        mContext = context;
        mLessonId = lessonId;
        setContentView(R.layout.exercise_option_layout);

        initWindow();
        initView();
    }

    private ArrayList<GridViewItem> createGridViewItemList() {
        ArrayList<GridViewItem> list = new ArrayList<>();

        GridViewItem item = new GridViewItem();
        item.iconRes = R.drawable.icon_homework_sel;
        item.title = "作业";
        item.type = "homework";
        item.action = "HomeworkSummaryActivity";
        list.add(item);

        item = new GridViewItem();
        item.iconRes = R.drawable.icon_exercise_sel;
        item.title = "练习";
        item.type = "exercise";
        item.action = "HomeworkSummaryActivity";
        list.add(item);

        return list;
    }

    public void initView() {
        mGridView = (GridView) findViewById(R.id.gridview);
        GridViewItemAdapter adapter = new GridViewItemAdapter(getContext(), createGridViewItemList(), R.layout.lesson_gridview_item);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridViewItem item = (GridViewItem) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt("lessonId", mLessonId);
                Intent intent = new Intent();
                intent.putExtra(Const.LESSON_ID, mLessonId);
                intent.putExtra("type", item.type);
                intent.setClassName(getContext().getPackageName(), item.action);
                mContext.startActivity(intent);
            }
        });

    }

    public void initWindow() {
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        lp.width = display.getWidth();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 0;

        window.setAttributes(lp);
    }

    /**
     * Created by howzhi on 14-10-8.
     */
    public class GridViewItemAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private int mResouce;
        private Context mContext;
        private List<GridViewItem> mList;

        public GridViewItemAdapter(
                Context context, List<GridViewItem> list, int resource)
        {
            mList = list;
            mContext = context;
            mResouce = resource;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int index) {
            return mList.get(index);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int index, View view, ViewGroup vg) {
            final ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(mResouce, null);
                holder = new ViewHolder();

                holder.itemView = (TextView) view.findViewById(R.id.lesson_gridview_item);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            GridViewItem item = mList.get(index);
            holder.itemView.setText(item.title);
            holder.itemView.setCompoundDrawablesWithIntrinsicBounds(0, item.iconRes, 0, 0);
            return view;
        }

        private class ViewHolder
        {
            public TextView itemView;
        }
    }

    private class GridViewItem {

        public int iconRes;
        public String title;
        public String type;;
        public String action;
    }

}
