package com.edusoho.kuozhi.ui.note;

import android.app.ProgressDialog;
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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.SubmitResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.edusoho.plugin.RichTextBox.RichTextBoxFragment;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.HashMap;

import cn.trinea.android.common.util.DigestUtils;

/**
 * Created by onewoman on 14-10-9.
 */
public class NoteContent extends ActionBarBaseActivity {
    private int courseId, lessonId;
    private String content, Title;

    private TextView titleView;
    private LinearLayout linear;
    private ViewGroup vgContent;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notecontent_layout);
        context = this;
        init();
        setBackMode(BACK, "笔记内容");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_reply_menu, menu);
        return true;
    }

    public void initIntentData() {
        Intent intent = getIntent();
        Title = intent.getStringExtra("note_title");
        content = intent.getStringExtra("note_content");
        courseId = intent.getIntExtra("note_courseId", 0);
        lessonId = intent.getIntExtra("note_lessonId", 0);
    }

    public void init() {
        initIntentData();
        titleView = (TextView) this.findViewById(R.id.content_title);
        vgContent = (ViewGroup) getLayoutInflater().inflate(R.layout.note_content_inflate,null);
        TextView contentView = (TextView) vgContent.findViewById(R.id.content);

        titleView.setText(Title);
        contentView.setText(Html.fromHtml(content,new NetImageGetter(contentView,content),null));
        linear = (LinearLayout) findViewById(R.id.rich_text_box_show);
        linear.addView(vgContent);
    }

    public void turnToNoteReply(final int courseId, final int lessonId, final String title,final String content)
    {
        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra("note_courseId", courseId);
                startIntent.putExtra("note_lessonId", lessonId);
                startIntent.putExtra("note_title",title);
                startIntent.putExtra(Const.NORMAL_CONTENT,content);
            }
        };
        app.mEngine.runNormalPlugin("NoteReplyActivity", mActivity, callback);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.note_edit)
            turnToNoteReply(courseId, lessonId,Title,content);
        return super.onOptionsItemSelected(item);
    }

    private class NetImageGetter implements Html.ImageGetter {
        private TextView mTextView;
        private String html;

        public NetImageGetter(TextView textView, String html) {
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
            } catch (Exception e) {
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
