package com.edusoho.kuozhi.shard;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by howzhi on 15/10/8.
 */
public class ShardDialog extends Dialog {

    private Context mContext;
    private GridView mGridView;
    private View mCancelView;

    public ShardDialog(Context context)
    {
        super(context, R.style.FullDialogTheme);
        mContext = context;
        initView();
        initWindow();
    }

    private void initView() {
        setContentView(R.layout.shard_content_layout);
        mGridView = (GridView) findViewById(R.id.shard_gridview);
        mCancelView = findViewById(R.id.shard_cancelBtn);
        mCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.LEFT | Gravity.BOTTOM);

        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        lp.width = display.getWidth();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 0;

        window.setAttributes(lp);
    }

    public void setShardDatas(ArrayList<ListData> list) {
        ShardListAdapter adapter = new ShardListAdapter(mContext, list, R.layout.shard_list_item);
        mGridView.setAdapter(adapter);
    }

    public void setShardItemClick(AdapterView.OnItemClickListener onItemClickListener) {
        mGridView.setOnItemClickListener(onItemClickListener);
    }
}
