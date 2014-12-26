package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.SchoolRoomAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Note.NoteInfo;
import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomEnum;
import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.message.MessageLetterListActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.note.NoteContentFragment;
import com.edusoho.kuozhi.ui.question.QuestionDetailActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import library.PullToRefreshBase;

/**
 * Created by JesseHuang on 14/12/23.
 */
public class SchoolRoomFragment extends BaseFragment {
    private String mTitle = "学堂";
    private static final String TAG = "SchoolRoomFragment";

    private View mView;
    private RefreshListWidget mSchoolRoomListView;
    private View mLoadView;

    public static final int REFRESH = 0010;
    public static final int LOGINT_WITH_TOKEN = 0020;
    public static final int LOGOUT = 0021;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.school_room_layout, container, false);
        initView();
        loadSchoolRoomData();
        return mView;
    }

    private void initView() {
        mSchoolRoomListView = (RefreshListWidget) mView.findViewById(R.id.lv_schoolroom);
        mLoadView = mView.findViewById(R.id.load_layout);
        mSchoolRoomListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mSchoolRoomListView.setEmptyText(new String[]{"您尚未登录"});
        mSchoolRoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SchoolRoomResult schoolRoomResult = (SchoolRoomResult) parent.getItemAtPosition(position);
                int schoolRoomType = SchoolRoomEnum.getIndex(schoolRoomResult.title);
                showItemActivity(schoolRoomType, schoolRoomResult);
            }
        });
    }

    /**
     * item跳转
     *
     * @param type
     * @param result
     */
    private void showItemActivity(int type, final SchoolRoomResult result) {
        switch (type) {
            case 1:
                //在学课程
                goToCourseDetailsActivity(result);
                break;
            case 2:
                //问答
                goToQuestionDetailActivity(result);
                break;
            case 3:
                //讨论
                goToQuestionDetailActivity(result);
                break;
            case 4:
                //笔记
                goToNoteContentFragment(result);
                break;
            case 5:
                //私信
                goToMessageLetterListActivity(result);
                break;

        }
    }

    /**
     * 跳转到课程界面（CourseDetailsActivity）
     */
    private void goToCourseDetailsActivity(final SchoolRoomResult result) {
        mActivity.app.mEngine.runNormalPlugin(
                CourseDetailsActivity.TAG, mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.COURSE_ID, Integer.parseInt(result.data.courseId));
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, result.data.content);
                        startIntent.putExtra(CourseDetailsActivity.COURSE_PIC, result.data.lessonId);
                    }
                });
    }

    /**
     * 跳转到问答详情界面(QuestionDetailActivity)
     *
     * @param result
     */
    private void goToQuestionDetailActivity(final SchoolRoomResult result) {
        mActivity.app.mEngine.runNormalPlugin(QuestionDetailActivity.TAG, mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(Const.COURSE_ID, Integer.parseInt(result.data.courseId));
                startIntent.putExtra(Const.THREAD_ID, Integer.parseInt(result.data.id));
            }
        });
    }

    /**
     * 跳转到笔记详情界面(NoteContentFragment)
     *
     * @param result
     */
    private void goToNoteContentFragment(final SchoolRoomResult result) {
        RequestUrl url = app.bindUrl(Const.ONE_NOTE, true);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("noteId", result.data.id);
        url.setParams(params);
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                Bundle bundle = new Bundle();
                NoteInfo noteInfo = mActivity.parseJsonValue(object, new TypeToken<NoteInfo>() {
                });
                bundle.putString(Const.ACTIONBAR_TITLE, noteInfo.lessonTitle);
                bundle.putString(FragmentPageActivity.FRAGMENT, "NoteContentFragment");
                bundle.putString(NoteContentFragment.CONTENT, noteInfo.content);
                bundle.putInt(Const.LESSON_ID, noteInfo.lessonId);
                bundle.putInt(Const.COURSE_ID, noteInfo.coursesId);
                bundle.putString(Const.LESSON_NAME, noteInfo.lessonTitle);
                bundle.putString(Const.LEARN_STATUS, noteInfo.learnStatus);
                mActivity.app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
            }
        });
    }

    /**
     * 跳转到私信详情界面(NoteContentFragment)
     */
    private void goToMessageLetterListActivity(final SchoolRoomResult result) {
        mActivity.app.mEngine.runNormalPlugin("MessageLetterListActivity", mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(MessageLetterListActivity.CONVERSATION_ID, Integer.parseInt(result.data.id));
                startIntent.putExtra(MessageLetterListActivity.CONVERSATION_FROM_NAME, "1");
                startIntent.putExtra(MessageLetterListActivity.CONVERSATION_FROM_ID, Integer.parseInt(result.data.courseId));
            }
        });
    }

    private void loadSchoolRoomData() {
        RequestUrl url = app.bindUrl(Const.SCHOOL_ROOM, true);
        ResultCallback callback = new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    mLoadView.setVisibility(View.GONE);
                    List<SchoolRoomResult> schoolRoomList = mActivity.parseJsonValue(
                            object, new TypeToken<ArrayList<SchoolRoomResult>>() {
                            });
                    if (schoolRoomList == null) {
                        return;
                    }

                    SchoolRoomAdapter<SchoolRoomResult> schoolRoomAdapter = new SchoolRoomAdapter(mContext,
                            R.layout.schoolroom_list_item);
                    schoolRoomAdapter.addItems(schoolRoomList);
                    mSchoolRoomListView.setAdapter(schoolRoomAdapter);
                } catch (Exception ex) {
                    Log.d(TAG, ex.toString());
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
            }
        };
        mActivity.ajaxPost(url, callback);
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        switch (message.type.code) {
            case REFRESH:
                loadSchoolRoomData();
                break;
            case LOGINT_WITH_TOKEN:
                loadSchoolRoomData();
                break;
            case LOGOUT:
        }

    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(REFRESH, source),
                new MessageType(LOGOUT, source),
                new MessageType(LOGINT_WITH_TOKEN, source)
        };
        return messageTypes;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }
}
