package com.edusoho.kuozhi.ui.note;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;


import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Note.NoteAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Note.Note;
import com.edusoho.kuozhi.model.Note.NoteInfo;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.RefreshGridViewWidget;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import library.PullToRefreshBase;
import library.PullToRefreshListView;
import library.PullToRefreshScrollView;


/**
 * Created by Melomelon on 2014/11/28.
 */
public class NoteListActivity extends ActionBarBaseActivity {

    private RefreshListWidget mNoteListView;
    private PullToRefreshScrollView emptyLayout;
    private View mLoadView;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_list_layout);
        initView();
    }

    private void initView() {
        setBackMode(BACK, "我的笔记");

        mLoadView = findViewById(R.id.load_layout);
        mNoteListView = (RefreshListWidget) this.findViewById(R.id.note_list_view);
        mNoteListView.setMode(PullToRefreshBase.Mode.BOTH);
        mNoteListView.setEmptyText(new String[] { "没有笔记" });

        noteAdapter = new NoteAdapter(mContext, R.layout.note_list_item);
        mNoteListView.setAdapter(noteAdapter);

        mNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            try{
                NoteInfo noteInfo = (NoteInfo) adapterView.getItemAtPosition(i);
                Bundle bundle = new Bundle();
                bundle.putString(Const.ACTIONBAT_TITLE, noteInfo.lessonTitle);
                bundle.putString(FragmentPageActivity.FRAGMENT, "NoteContentFragment");
                bundle.putString(NoteContentFragment.CONTENT, noteInfo.content);
                bundle.putInt(Const.LESSON_ID, noteInfo.lessonId);
                bundle.putInt(Const.COURSE_ID, noteInfo.coursesId);
                bundle.putString(Const.LESSON_NAME,noteInfo.lessonTitle);

                app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
            }catch (Exception e){
                e.printStackTrace();
            }

            }

        });


        setPullToRefreshListener();

        returnObjectFromGson(0);
    }



    public void setPullToRefreshListener() {
        mNoteListView.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                returnObjectFromGson(mNoteListView.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                returnObjectFromGson(0);
            }
        });
    }




    public void returnObjectFromGson(final int start) {
        RequestUrl url = app.bindUrl(Const.USER_NOTES, true);
        url.setParams(new String[]{
                "start", String.valueOf(start),
                "limit", String.valueOf(Const.LIMIT)
        });
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mNoteListView.onRefreshComplete();
//                if (mLoadView.getVisibility() == View.VISIBLE) {
//                    mLoadView.setVisibility(View.GONE);
//                }
                parasHttpDatas(start, object);
            }
        });
    }




    public void parasHttpDatas(int start, String object) {
        ArrayList<NoteInfo> noteInfos = parseJsonValue(
                object, new TypeToken<ArrayList<NoteInfo>>(){});

        if (noteInfos == null) {
            return;
        }
        Log.i(null,"noteInfos"+noteInfos.toString());
        mNoteListView.pushData(noteInfos);
        mNoteListView.setStart(start + Const.LIMIT);
    }
}
