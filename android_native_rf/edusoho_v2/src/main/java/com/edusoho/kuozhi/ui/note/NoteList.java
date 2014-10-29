package com.edusoho.kuozhi.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Note.NoteListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Note.HttpDatas;
import com.edusoho.kuozhi.model.Note.LessonList;
import com.edusoho.kuozhi.model.Note.NoteListData;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-10-9.
 */
public class NoteList extends ActionBarBaseActivity {
    private final int PAGELIMIT = 5;
    private int courseId;
    private int intentTotal;
    private int limit;
    private int pullStart;
    private String title;
    private PullToRefreshListView list;
    private TextView text;
    private FilterHttpData httpDatas;
    private NoteListData noteListData;
    private NoteListAdapter listadApter;
    public ArrayList<HttpDatas> requestDatas;
    private ArrayList<LessonList> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notelist_layout);
        pullStart = 0;
        initIntentData();
        returnObjectFromGson(limit,pullStart,PAGELIMIT,true);
        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        returnObjectFromGson(limit,0,PAGELIMIT,true);
        pullStart = 0;
    }

    public void init() {
        setBackMode(BACK, title);
        initView();
        isRefresh();
    }

    public void initIntentData() {
        Intent intent = getIntent();
        courseId = intent.getIntExtra("courseId", 0);
        intentTotal = intent.getIntExtra("total", 0);
        title = intent.getStringExtra("title");
        limit = intent.getIntExtra("limit",0);
    }

    public void initView() {
        text = (TextView) this.findViewById(R.id.list_title);
        text.setText("共" + intentTotal + "篇笔记");
        list = (PullToRefreshListView) this.findViewById(R.id.list_content);
        listadApter = new NoteListAdapter(getLayoutInflater(),this);
        list.setMode(PullToRefreshBase.Mode.BOTH);
        list.setAdapter(listadApter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LessonList lessonList = (LessonList) adapterView.getItemAtPosition(i);
                showNoteContent(lessonList.courseTitle, lessonList.courseContent,courseId,lessonList.lessonId);
            }
        });
    }

    public void showNoteContent(final String courseTitle,final String courseContent,final int courseId,final int lessonId) {
        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra("note_title", courseTitle);
                startIntent.putExtra("note_content", courseContent);
                startIntent.putExtra("note_courseId",courseId);
                startIntent.putExtra("note_lessonId",lessonId);
            }
        };
        app.mEngine.runNormalPlugin("NoteContent", mActivity, callback);
    }

    public void getLessonList() {
        data = new ArrayList<LessonList>();
        data.clear();
        for (int i = 0; noteListData != null && i < noteListData.courseNum.size(); i++) {
            LessonList temp = new LessonList(noteListData.courseNum.get(i), noteListData.courseTitle.get(i), noteListData.courseContent.get(i), noteListData.lessonId.get(i));
            data.add(temp);
        }
    }

    public void isRefresh() {
        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                returnObjectFromGson(limit,0,PAGELIMIT,true);
                pullStart = 0;
                list.setMode(PullToRefreshBase.Mode.BOTH);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullStart = pullStart + PAGELIMIT;
                returnObjectFromGson(limit,pullStart,PAGELIMIT,false);
            }
        });
    }

    public void returnObjectFromGson(int limit,final int pullStart,final int pullLimit,final boolean isPullDown) {
        RequestUrl url = app.bindUrl(Const.NOTE_LIST_DATA, true);
        url.setParams(new String[]{
                "start", 0 + "",
                "limit", limit + ""
        });
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                list.onRefreshComplete();
                if(isPullDown)
                    parasHttpDatas(object);
                setAdaperData(pullStart,pullLimit,isPullDown);
            }
        });
    }

    public void parasHttpDatas(String object) {
        if (!app.token.equals("")) {
            requestDatas = parseJsonValue(object, new TypeToken<ArrayList<HttpDatas>>(){});
            if(requestDatas == null)
                return ;
            httpDatas = new FilterHttpData(requestDatas);
            noteListData = httpDatas.getNoteListData(courseId);
            getLessonList();
            text.setText("共" + data.size() + "篇笔记");
        } else {
            LoginActivity.startForResult(mActivity);
        }
    }

    public void setAdaperData(int pullStart,int pullLimit,boolean isPullDown)
    {
        ArrayList<LessonList> pulldatas = new ArrayList<LessonList>();
        pulldatas.clear();
        int i = 0;
        for(i=0;i<pullLimit && (pullStart+i) < data.size();i++)
        {
            pulldatas.add(i,data.get(i+pullStart));
        }
        if(pullStart+i>data.size())
        {
            list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            Toast.makeText(this,"没有数据了",0).show();
            return ;
        }
        if (isPullDown)
            listadApter.setItem(pulldatas);
        else
            listadApter.addAllDatas(pulldatas);
    }
}
