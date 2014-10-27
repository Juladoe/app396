package com.edusoho.kuozhi.ui.note;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.plugin.RichTextBox.RichTextBoxFragment;

import java.io.File;
import java.util.HashMap;

import cn.trinea.android.common.util.DigestUtils;

/**
 * Created by onewoman on 14-10-9.
 */
public class NoteContent extends ActionBarBaseActivity {
    private String content,Title;
    private TextView titleView;
    private Context context;

    private RichTextBoxFragment richFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notecontent_layout);
        context = this;
        init();
        setBackMode(BACK,"笔记内容");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question_reply_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (richFragment.getContent().toString() == null || richFragment.getContent().toString().equals("")) {
            Toast.makeText(mActivity, "内容不能为空", Toast.LENGTH_LONG).show();
            return true;
        }
        if (item.getItemId() == R.id.reply_submit) {
            switch (richFragment.getTypeCode()) {
                case Const.REPLY: {
                    RequestUrl url = app.bindUrl(Const.REPLY_SUBMIT, true);
                    HashMap<String, String> params = url.getParams();
                    params.put("courseId", richFragment.getCourseId());
                    params.put("threadId", richFragment.getThreadId());
                    final String content = AppUtil.removeHtml(Html.toHtml(richFragment.getContent()));
                    params.put("content", richFragment.setContent(content));
                    params.put("imageCount", String.valueOf(richFragment.getImageHashMapSize()));
                    url.setMuiltParams(richFragment.getObjects());
                    url.setParams(params);
//                    submitReply(url);
                    break;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void init()
    {
        initIntentData();
        initView();
    }

    public void initIntentData()
    {
        Intent intent = getIntent();
        Title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
    }

    public void initView()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        richFragment = new RichTextBoxFragment();
        byte[] itemArgs = new byte[]{View.VISIBLE, View.VISIBLE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE};
        Bundle bundle = new Bundle();
        bundle.putByteArray(Const.RICH_ITEM_AGRS, itemArgs);
        richFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.rich_text_box_show, richFragment);
        fragmentTransaction.commit();

        titleView = (TextView) this.findViewById(R.id.content_title);
//        contentView = (TextView) this.findViewById(R.id.content);

        titleView.setText(Title);
//        contentView.setText(Html.fromHtml(content,new NetImageGetter(contentView,content),null));
    }

    private class NetImageGetter implements Html.ImageGetter
    {
        private TextView mTextView;
        private String html;

        public NetImageGetter(TextView textView, String html)
        {
            this.html = html;
            mTextView = textView;
        }

        @Override
        public Drawable getDrawable(String s) {
            Drawable drawable = null;
            AQuery aQuery = new AQuery(getLayoutInflater().inflate(R.layout.notecontent_layout, null));
            File cacheDir = AQUtility.getCacheDir(context);
            String fileName = DigestUtils.md5(s);

            File file = new File(cacheDir, fileName);
            Log.d(null, "update file->" + file);
            if (file != null && file.exists()) {
                Bitmap bitmap = AppUtil.getBitmapFromFile(file);
                drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }

            try {
                file.createNewFile();
            }catch (Exception e) {
                e.printStackTrace();
            }
            aQuery.download(s, file, new AjaxCallback<File>() {
                @Override
                public void callback(String url, File object, AjaxStatus status) {
                    super.callback(url, object, status);
                    mTextView.setText(Html.fromHtml(html, new NetImageGetter(mTextView, html), null));
                }
            });
            return drawable;
        }
    }
}
