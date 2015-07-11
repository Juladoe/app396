package com.edusoho.kuozhi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.SchoolRoomAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Push.PushMsg;
import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomEnum;
import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.schoolroom.LearningRoomActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import library.PullToRefreshBase;

/**
 * Created by JesseHuang on 14/12/23.
 */
public class SchoolRoomFragment extends BaseFragment {
    private static final String TAG = "SchoolRoomFragment";

    private View mView;
    private RefreshListWidget mSchoolRoomListView;
    private View mLoadView;

    public static final int REFRESH = 0010;
    public static final int LOGINT_WITH_TOKEN = 0020;
    public static final int LOGOUT = 0021;
    public static final int PUSH_ITEM = 0022;

    public static final String PUSH_MODEL = "push_model";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.school_room_layout, container, false);
        mTitle = "学堂";
        setHasOptionsMenu(true);
        initView();
        loadSchoolRoomData();
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.empty_menu, menu);
    }

    private void initView() {
        mSchoolRoomListView = (RefreshListWidget) mView.findViewById(R.id.lv_schoolroom);
        mLoadView = mView.findViewById(R.id.load_layout);
        mSchoolRoomListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mSchoolRoomListView.setEmptyText(new String[]{"数据访问出错"});
        mSchoolRoomListView.setAdapter(new SchoolRoomAdapter(mContext,
                R.layout.schoolroom_list_item));
        mSchoolRoomListView.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                loadSchoolRoomData();
            }
        });
        mSchoolRoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (app.loginUser == null) {
//                    LoginActivity.start(mActivity);
//                    return;
//                }
                SchoolRoomResult schoolRoomResult = (SchoolRoomResult) parent.getItemAtPosition(position);
                int schoolRoomType = SchoolRoomEnum.getIndex(schoolRoomResult.title);
                showItemActivity(schoolRoomType);
            }
        });
    }

    /**
     * item跳转
     *
     * @param type item类型
     */
    private void showItemActivity(int type) {
        switch (type) {
            case 1:
                //在学直播课
                goToLiveCourseDetailsActivity();
                break;
            case 2:
                //在学课程
                goToCourseDetailsActivity();
                break;
            case 3:
                //问答
                goToQuestionDetailActivity("问答", "question", "暂无提问", R.drawable.icon_question);
                break;
            case 4:
                //讨论
                goToQuestionDetailActivity("讨论", "discussion", "暂无讨论", R.drawable.icon_discussion);
                break;
            case 5:
                //笔记
                goToNoteContentFragment();
                break;
            case 6:
                //私信
                goToMessageLetterListActivity();
                break;
        }
    }

    private void goToLiveCourseDetailsActivity() {
        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
            }
        };
        app.mEngine.runNormalPlugin("LivingCourseActivity", mActivity, callback);
    }

    /**
     * 跳转到课程界面（CourseDetailsActivity）
     */
    private void goToCourseDetailsActivity() {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(Const.ACTIONBAR_TITLE, "在学课程");
            bundle.putString(LearningRoomActivity.FRAGMENT_NAME, "在学课程");
            app.mEngine.runNormalPluginWithBundle("LearningRoomActivity", mContext, bundle);
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    /**
     * 跳转到问答详情界面(QuestionDetailActivity)
     */
    private void goToQuestionDetailActivity(final String title, final String type, final String emptyText, final int emptyIcon) {

        PluginRunCallback callback = new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(Const.ACTIONBAR_TITLE, title);
                startIntent.putExtra(Const.QUESTION_TYPE, type);
                startIntent.putExtra("empty_text", emptyText);
                startIntent.putExtra("empty_icon", emptyIcon);
                startIntent.putExtra(Const.QUESTION_URL, Const.THREADS_BY_USER_COURSE_ID);
            }
        };
        app.mEngine.runNormalPlugin("QuestionNewActivity", mActivity, callback);
    }

    /**
     * 跳转到笔记详情界面(NoteContentFragment)
     */
    private void goToNoteContentFragment() {
        if (app.loginUser == null) {
            LoginActivity.start(mActivity);
            return;
        }

        app.mEngine.runNormalPlugin("NoteListActivity", mActivity, null);
    }

    /**
     * 跳转到私信详情界面(NoteContentFragment)
     */
    private void goToMessageLetterListActivity() {
        if (app.loginUser == null) {
            LoginActivity.start(mActivity);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Const.ACTIONBAR_TITLE, "私信");
        bundle.putString(FragmentPageActivity.FRAGMENT, "LetterFragment");
        app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
    }

    private void loadSchoolRoomData() {
        RequestUrl url = app.bindUrl(Const.SCHOOL_ROOM, true);
        ResultCallback callback = new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    mSchoolRoomListView.onRefreshComplete();
                    mLoadView.setVisibility(View.GONE);
                    ArrayList<SchoolRoomResult> schoolRoomList = mActivity.parseJsonValue(
                            object, new TypeToken<ArrayList<SchoolRoomResult>>() {
                            });
                    filterSchoolRoomDatas(schoolRoomList);
                    mSchoolRoomListView.pushData(schoolRoomList);
                } catch (Exception ex) {
                    Log.d(TAG, ex.toString());
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                try {
                    mLoadView.setVisibility(View.GONE);
                    ArrayList<SchoolRoomResult> schoolRoomList = new ArrayList<SchoolRoomResult>();
                    schoolRoomList.add(new SchoolRoomResult("在学直播", null));
                    schoolRoomList.add(new SchoolRoomResult("在学课程", null));
                    schoolRoomList.add(new SchoolRoomResult("问答", null));
                    schoolRoomList.add(new SchoolRoomResult("讨论", null));
                    schoolRoomList.add(new SchoolRoomResult("笔记", null));
                    schoolRoomList.add(new SchoolRoomResult("私信", null));
                    filterSchoolRoomDatas(schoolRoomList);
                    mSchoolRoomListView.pushData(schoolRoomList);
                } catch (Exception ex) {
                    Log.d(TAG, ex.toString());
                }
            }
        };
        mActivity.ajaxPost(url, callback);
    }

    private void loadOneItem() {
        Log.d(TAG, "new item");
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
                loadSchoolRoomData();
                break;
            case PUSH_ITEM:
                PushMsg pushMsg = (PushMsg) message.data.getSerializable(PUSH_MODEL);
                if (pushMsg != null) {
                    loadOneItem();
                }
                break;
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

    /**
     * 云大学App隐藏直播item
     *
     * @param list
     */
    protected void filterSchoolRoomDatas(List<SchoolRoomResult> list) {
        if (getString(R.string.show_live_in_school_room).equals("1")) {
            for (SchoolRoomResult item : list) {
                if (item.title.equals("在学直播")) {
                    list.remove(item);
                    break;
                }
            }
        }
    }
}
