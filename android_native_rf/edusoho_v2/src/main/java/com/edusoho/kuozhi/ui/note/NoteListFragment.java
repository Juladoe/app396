package com.edusoho.kuozhi.ui.note;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Note.NoteListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Note.Note;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-10-31.
 */
public class NoteListFragment extends BaseFragment {

    private int mCourseId;
    private int mNoteNum;
    private String mTitle;
    private TextView mNoteListTitleView;
    private RefreshListWidget mListView;
    private View mLoadView;

    public static final String NOTENUM = "noteNum";

    @Override
    public String getTitle() {
        return "笔记列表";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.note_list_fragment_layout);
        initIntentData();
    }

    public void initIntentData() {
        Bundle bundle = getArguments();
        mCourseId = bundle.getInt(Const.COURSE_ID, 0);
        mTitle = bundle.getString(Const.ACTIONBAT_TITLE);
        mNoteNum = bundle.getInt("noteNum", 0);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mLoadView = view.findViewById(R.id.load_layout);
        mListView = (RefreshListWidget) view.findViewById(R.id.note_listview);
        mNoteListTitleView = (TextView) view.findViewById(R.id.note_list_title);

        changeTitle(mTitle);
        mNoteListTitleView.setText(String.format("共%d篇笔记", mNoteNum));
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setAdapter(new NoteListAdapter(mContext, R.layout.lesson_note_item_layout, true));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note note = (Note) adapterView.getItemAtPosition(i);
                Bundle bundle = new Bundle();
                bundle.putString(Const.ACTIONBAT_TITLE, note.lessonTitle);
                bundle.putString(FragmentPageActivity.FRAGMENT, "NoteContentFragment");
                bundle.putString(NoteContentFragment.CONTENT, note.content);
                bundle.putInt(Const.COURSE_ID, note.courseId);
                bundle.putInt(Const.LESSON_ID, note.lessonId);

                startAcitivityWithBundle("FragmentPageActivity", bundle);
            }
        });
        mListView.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                returnObjectFromGson(mListView.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                returnObjectFromGson(0);
            }
        });

        returnObjectFromGson(0);
    }

    public void returnObjectFromGson(final int start) {
        RequestUrl url = app.bindUrl(Const.COURSE_NOTES, true);
        url.setParams(new String[]{
                "start", String.valueOf(start),
                "limit", String.valueOf(Const.LIMIT),
                Const.COURSE_ID, String.valueOf(mCourseId)
        });
        showProgress(true);
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                showProgress(false);
                if (mLoadView.getVisibility() == View.VISIBLE) {
                    mLoadView.setVisibility(View.GONE);
                }
                mListView.onRefreshComplete();

                ArrayList<Note> notes = mActivity.parseJsonValue(
                        object, new TypeToken<ArrayList<Note>>(){});
                if (notes == null) {
                    return;
                }

                mListView.pushData(notes);
                mListView.setStart(start + Const.LIMIT);
            }
        });
    }
}
