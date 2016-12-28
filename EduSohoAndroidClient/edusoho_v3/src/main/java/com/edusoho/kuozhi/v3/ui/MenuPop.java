package com.edusoho.kuozhi.v3.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remilia on 2016/12/19.
 */
public class MenuPop {
    private PopupWindow mPopup;
    private Context mContext;
    private ListView mListView;
    private List<Item> mNames = new ArrayList<>();
    private MenuAdapter mAdapter = new MenuAdapter();
    private View mBindView;
    private IMenuShowListener mIMenuShowListener;

    public MenuPop(Context context, View bindView) {
        this.mContext = context;
        mBindView = bindView;
        init();
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

    public MenuPop addItem(String name) {
        Item item = new Item();
        item.name = name;
        mNames.add(item);
        if (mPopup.isShowing() && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }

    public MenuPop addItem(String name, int textColor, Drawable drawable) {
        Item item = new Item();
        item.name = name;
        item.color = textColor;
        mNames.add(item);
        if (mPopup.isShowing() && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }

    public MenuPop addItem(String name, int textColor) {
        Item item = new Item();
        item.name = name;
        item.color = textColor;
        mNames.add(item);
        if (mPopup.isShowing() && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }

    public MenuPop addItem(String name, Drawable drawable) {
        Item item = new Item();
        item.name = name;
        item.drawable = drawable;
        mNames.add(item);
        if (mPopup.isShowing() && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }

    public MenuPop removeItem(int position) {
        if (position != -1 && position < (mNames.size() - 1)) {
            mNames.remove(position);
        }
        if (mPopup.isShowing() && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }

    public void setMenuShowListener(IMenuShowListener iMenuShowListener) {
        this.mIMenuShowListener = iMenuShowListener;
    }

    public void removeAll() {
        mNames.clear();
    }

    public Item getItem(int position) {
        return mNames.get(position);
    }

    public void setVisibility(boolean show) {
        if (mBindView != null) {
            if (show) {
                mBindView.setVisibility(View.VISIBLE);
            } else {
                mBindView.setVisibility(View.GONE);
            }
        }
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
        mAdapter.notifyDataSetChanged();
        mPopup.showAsDropDown(view, x, y);
        if (mIMenuShowListener != null) {
            mIMenuShowListener.onShow(true);
        }
    }

    public void dismiss() {
        mPopup.dismiss();
        if (mIMenuShowListener != null) {
            mIMenuShowListener.onShow(false);
        }
    }

    public interface IMenuShowListener {
        void onShow(boolean isShow);
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
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_menu_pop, null, false);
                viewHolder = new ViewHolder();
                viewHolder.point = convertView.findViewById(R.id.v_menu_point);
                viewHolder.txt = (TextView) convertView.findViewById(R.id.tv_menu_txt);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Item item = mNames.get(position);
            viewHolder.txt.setText(item.name);
            convertView.setTag(R.id.iv_menu, position);
            convertView.setOnClickListener(mOnClickListener);
            if (item.hasPoint) {
                viewHolder.point.setVisibility(View.VISIBLE);
            } else {
                viewHolder.point.setVisibility(View.GONE);
            }
            if (item.drawable != null) {
                viewHolder.txt.setCompoundDrawables(item.drawable, null, null, null);
            }
            if (item.color != -1) {
                viewHolder.txt.setTextColor(item.color);
            } else {
                viewHolder.txt.setTextColor(mContext.getResources().getColor(R.color.disabled2_hint_color));
            }
            return convertView;
        }

        ViewHolder viewHolder;

        private View.OnClickListener mOnClickListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag(R.id.iv_menu);
                        if (mOnMenuClickListener != null) {
                            mOnMenuClickListener.onClick(v, position, mNames.get(position).name);
                        }
                    }
                };
    }

    private class ViewHolder {
        TextView txt;
        View point;
    }

    public class Item {
        private String name;
        private int color = -1;
        private Drawable drawable;
        private boolean hasPoint = false;

        public void setHasPoint(boolean hasPoint) {
            this.hasPoint = hasPoint;
            if (mPopup.isShowing() && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }

        public void setName(String name) {
            this.name = name;
            if (mPopup.isShowing() && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }

        public void setColor(int color) {
            this.color = color;
            if (mPopup.isShowing() && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
            if (mPopup.isShowing() && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }

        public String getName() {
            return name;
        }

        public int getColor() {
            return color;
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public boolean isHasPoint() {
            return hasPoint;
        }
    }

}
