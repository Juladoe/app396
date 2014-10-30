package com.edusoho.kuozhi.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Note.NoteAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Note.CollectNode;
import com.edusoho.kuozhi.model.Note.HttpDatas;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-10-9.
 */
public class NoteActivity extends ActionBarBaseActivity {
    private int start;
    private final int PAGELIMIT = 6;
    private PullToRefreshGridView grid;
    private PullToRefreshScrollView emptyLayout;
    private NoteAdapter noteAdapter;
    private FilterHttpData httpDatas;
    public ArrayList<HttpDatas> requestDatas;
    private ArrayList<CollectNode> collect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_main);
        start = 0;
        returnObjectFromGson(0, PAGELIMIT, false);
        initView();
        setBackMode(BACK, "我的笔记");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        returnObjectFromGson(0, start + PAGELIMIT, true);
    }

    private void initView() {
        grid = (PullToRefreshGridView) this.findViewById(R.id.notegrid);
        grid.setMode(PullToRefreshBase.Mode.BOTH);

        noteAdapter = new NoteAdapter(getLayoutInflater());
        grid.setAdapter(noteAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println(adapterView.getCount());
                CollectNode collectNode = (CollectNode) adapterView.getItemAtPosition(i);
                trunToNoteList(collectNode.courseName, collectNode.courseId, collectNode.total, start + PAGELIMIT);
            }
        });
        setPullToRefreshListener();
    }

    private PullToRefreshScrollView initEmptyLayout() {
        PullToRefreshScrollView scrollView = new PullToRefreshScrollView(this);
        scrollView.getRefreshableView().setFillViewport(true);
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View emptyView = getLayoutInflater().inflate(R.layout.course_empty_layout, null);
        TextView text = (TextView) emptyView.findViewById(R.id.list_empty_text);
        text.setText("没有笔记");

        scrollView.addView(emptyView);
        return scrollView;
    }

    private void refreshLayout() {
        ViewGroup view = (ViewGroup) findViewById(R.id.notemain);
        if (noteAdapter.isEmpty()) {
            if (emptyLayout == null) {
                emptyLayout = initEmptyLayout();
                view.addView(emptyLayout);
            }
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            if (emptyLayout != null)
                emptyLayout.setVisibility(View.GONE);
            view.postInvalidate();
        }
    }

    public void setPullToRefreshListener() {
        grid.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                returnObjectFromGson(0, PAGELIMIT, true);
                start = 0;
                grid.setMode(PullToRefreshBase.Mode.BOTH);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                start = start + PAGELIMIT;
                returnObjectFromGson(start, PAGELIMIT, false);
            }
        });
    }

    public void trunToNoteList(final String courseTitle, final int courseId, final int total, final int intentLlmit) {
        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra("courseId", courseId);
                startIntent.putExtra("total", total);
                startIntent.putExtra("title", courseTitle);
                startIntent.putExtra("limit", intentLlmit);
            }
        };
        app.mEngine.runNormalPlugin("NoteList", mActivity, callback);
    }

    public void returnObjectFromGson(int start, int limit, final boolean pullDown) {
        RequestUrl url = app.bindUrl(Const.NOTE_LIST_DATA, true);
        url.setParams(new String[]{
                "start", start + "",
                "limit", limit + ""
        });
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                grid.onRefreshComplete();
                parasHttpDatas(object, pullDown);
            }
        });
    }

    public void parasHttpDatas(String object, boolean pullDown) {
        if (!app.token.equals("")) {
            requestDatas = parseJsonValue(object, new TypeToken<ArrayList<HttpDatas>>() {
            });
            if(requestDatas==null)
                return ;
            httpDatas = new FilterHttpData(requestDatas);
            collect = httpDatas.getCollect();
            if (pullDown)
                noteAdapter.setItem(collect);
            else
                noteAdapter.addAllDatas(collect);
            refreshLayout();
            if (requestDatas.size() == 0) {
                grid.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                Toast.makeText(this,"没有数据了",0).show();
            }
        } else {
            LoginActivity.startForResult(mActivity);
        }
    }
}
