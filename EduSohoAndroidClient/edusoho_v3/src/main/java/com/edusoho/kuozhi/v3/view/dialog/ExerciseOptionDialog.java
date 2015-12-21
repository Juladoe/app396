package com.edusoho.kuozhi.v3.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.LessonPluginCallback;
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

        Bundle bundle = new Bundle();
        bundle.putInt("lessonId", mLessonId);
        Intent intent = new Intent();
        intent.setPackage(mContext.getPackageName());
        intent.putExtra(Const.LESSON_ID, mLessonId);
        intent.setAction(Const.LESSON_PLUGIN);

        List<ResolveInfo> resolveInfos = mContext.getPackageManager().queryIntentActivities(
                intent, PackageManager.GET_ACTIVITIES);
        for (ResolveInfo resolveInfo : resolveInfos) {
            GridViewItem item = new GridViewItem();
            item.iconRes = mContext.getResources().getDrawable(resolveInfo.activityInfo.icon);
            item.title = resolveInfo.loadLabel(mContext.getPackageManager()).toString();
            item.bundle = intent.getExtras();
            item.action = resolveInfo.activityInfo.name;
            try {
                Class lessonPluginCallbackCls = Class.forName(resolveInfo.activityInfo.name + "$Callback");
                item.callback = (LessonPluginCallback) lessonPluginCallbackCls.getConstructor(Context.class).newInstance(mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
            list.add(item);
        }

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
                if (item.callback.click(parent, view, position)) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtras(item.bundle);
                intent.setClassName(getContext().getPackageName(), item.action);
                mContext.startActivity(intent);
            }
        });

    }

    public void initWindow() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.DialogWindowAnimation);
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
            GridViewItem item = mList.get(index);
            if (view == null) {
                view = inflater.inflate(mResouce, null);
                holder = new ViewHolder();
                holder.itemView = (TextView) view.findViewById(R.id.lesson_gridview_item);
                holder.itemLoad = view.findViewById(R.id.lesson_gridview_load);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.itemView.setText(item.title);
            holder.itemView.setCompoundDrawablesWithIntrinsicBounds(null, item.iconRes, null, null);
            switch (item.status) {
                case GridViewItem.LOAD:
                    setViewStatus(holder, view, false);
                    holder.itemLoad.setVisibility(View.VISIBLE);
                    break;
                case GridViewItem.UNENABLE:
                    setViewStatus(holder, view, false);
                    holder.itemLoad.setVisibility(View.GONE);
                    break;
                case GridViewItem.ENABLE:
                    setViewStatus(holder, view, true);
                    holder.itemLoad.setVisibility(View.GONE);

            }
            item.callback.initPlugin(this, index);
            return view;
        }

        private void setViewStatus(ViewHolder holder, View view, boolean status) {
            view.setEnabled(status);
            holder.itemView.setEnabled(status);
        }

        private class ViewHolder
        {
            public TextView itemView;
            public View itemLoad;
        }
    }

    public class GridViewItem {

        public static final int LOAD = 0001;
        public static final int ENABLE = 0002;
        public static final int UNENABLE = 0003;

        public Drawable iconRes;
        public String title;
        public String action;
        public Bundle bundle;
        public int status;
        public LessonPluginCallback callback;
    }

}
