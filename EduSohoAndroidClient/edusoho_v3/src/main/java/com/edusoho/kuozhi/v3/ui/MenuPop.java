package com.edusoho.kuozhi.v3.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.htmlapp.Menu;
import com.edusoho.kuozhi.v3.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.AppUtils;
import jazzyviewpager.Util;
import m.framework.ui.widget.slidingmenu.MenuAdapter;
import m.framework.utils.Utils;

/**
 * Created by remilia on 2016/12/19.
 */
public class MenuPop {
    private PopupWindow mPopup;
    private Context mContext;
    private ListView mListView;
    private List<Item> mNames = new ArrayList<>();
    private MenuAdapter mAdapter = new MenuAdapter();

    public MenuPop(Context context) {
        this.mContext = context;
        init();
        initEvent();
    }

    private void init() {
        mPopup = new PopupWindow(mContext);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        mPopup.setBackgroundDrawable(dw);

        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.pop_menu, null, false);
        mPopup.setContentView(contentView);
        mListView = (ListView) contentView.findViewById(R.id.lv_menu);
        mListView.setAdapter(mAdapter);

        mPopup.setWidth(AppUtil.dp2px(mContext, 68));
        mPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopup.setFocusable(true);
        mPopup.setTouchable(true);
        mPopup.setOutsideTouchable(true);
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnMenuClickListener != null) {
                    mOnMenuClickListener.onClick(view, position, mNames.get(position).name);
                }
            }
        });
    }

    public MenuPop addItem(String name) {
        Item item = new Item();
        item.name = name;
        mNames.add(item);
        return this;
    }

    public MenuPop addItem(String name, int drawableId) {
        Item item = new Item();
        item.name = name;
        item.drawable = drawableId;
        mNames.add(item);
        return this;
    }

    private OnMenuClickListener mOnMenuClickListener;

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.mOnMenuClickListener = onMenuClickListener;
    }

    public interface OnMenuClickListener {
        void onClick(View v, int position, String name);
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        mAdapter.notifyDataSetChanged();
        mPopup.showAtLocation(parent, gravity, x, y);
    }

    public void showAsDropDown(View view, int x, int y) {
        mPopup.showAsDropDown(view, x, y);
    }

    public void dismiss() {
        mPopup.dismiss();
    }

    private class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNames.size();
        }

        @Override
        public Object getItem(int position) {
            return mNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                RelativeLayout layout = new RelativeLayout(mContext);
                view = new TextView(mContext);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        AppUtil.dp2px(mContext, 68), AppUtil.dp2px(mContext, 40)
                );
                params.setMargins(0, AppUtil.dp2px(mContext, 1), 0, 0);
                view.setLayoutParams(params);
                layout.addView(view);
                view.setBackgroundColor(Color.parseColor("#80000000"));
                view.setGravity(Gravity.CENTER);
                view.setTextColor(mContext.getResources().getColor(R.color.disabled2_hint_color));
                view.setTextSize(13);
                convertView = layout;
                convertView.setTag(view);
            } else {
                view = (TextView) convertView.getTag();
            }
            Item item = mNames.get(position);
            view.setText(item.name);
            if (item.drawable != -1) {
                view.setCompoundDrawables(mContext.getResources()
                        .getDrawable(item.drawable), null, null, null);
            }
            return convertView;
        }

        TextView view;
    }


    private class Item {
        String name;
        int drawable = -1;
    }
}
