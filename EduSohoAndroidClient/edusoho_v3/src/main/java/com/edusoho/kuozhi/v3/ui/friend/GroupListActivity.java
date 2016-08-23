package com.edusoho.kuozhi.v3.ui.friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cyberplayer.utils.A;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.v3.adapter.FriendFragmentAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.DiscussionGroup;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseResult;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.model.provider.DiscussionGroupProvider;
import com.edusoho.kuozhi.v3.model.provider.IMProvider;
import com.edusoho.kuozhi.v3.model.result.DiscussionGroupResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.ClassroomDiscussActivity;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.view.SideBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Melomelon on 2015/9/10.
 */
public class GroupListActivity extends ActionBarBaseActivity {

    private final String TAG = "GroupList";
    private ListView mListView;

    private FriendFragmentAdapter mAdapter;

    private SideBar mSidebar;
    private TextView mCharTextView;
    private FrameLayout mEmptyNotice;
    private FrameLayout mLoading;

    private DiscussionGroupProvider mDiscussionGroupProvider;
    private ArrayList<DiscussionGroup> mGroupList = new ArrayList<DiscussionGroup>();

    private CharacterParser characterParser;
    private GroupComparator groupComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackMode(BACK, "讨论组");
        setContentView(R.layout.group_list_layout);
        mListView = (ListView) findViewById(R.id.group_listview);
        mSidebar = (SideBar) findViewById(R.id.group_list_sidebar);
        mCharTextView = (TextView) findViewById(R.id.sidebar_char_hint);
        mEmptyNotice = (FrameLayout) findViewById(R.id.empty_discussion_group);
        mLoading = (FrameLayout) findViewById(R.id.discussion_group_loading);
        mSidebar.setTextView(mCharTextView);
        mSidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChangedListener(String string) {
                int position = mAdapter.getPositionForSection(string.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });
        mAdapter = new FriendFragmentAdapter(getBaseContext(), app);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DiscussionGroup discussionGroup = (DiscussionGroup) parent.getItemAtPosition(position);
                app.mEngine.runNormalPlugin("ClassroomDiscussActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(ClassroomDiscussActivity.FROM_ID, Integer.valueOf(discussionGroup.id));
                        startIntent.putExtra(Const.ACTIONBAR_TITLE, discussionGroup.title);
                    }
                });

            }
        });
        characterParser = CharacterParser.getInstance();
        groupComparator = new GroupComparator();
        mDiscussionGroupProvider = new DiscussionGroupProvider(mContext);

        mLoading.setVisibility(View.VISIBLE);
        mEmptyNotice.setVisibility(View.GONE);
        if (mGroupList.size() != 0) {
            mAdapter.clearList();
        }
        if (!app.getNetIsConnect()) {
            mLoading.setVisibility(View.GONE);
            Toast.makeText(mContext, "无网络连接", Toast.LENGTH_LONG).show();
        }
        loadGroup().then(new PromiseCallback() {
            @Override
            public Promise invoke(Object obj) {
                return loadCourseGroup();
            }
        }).then(new PromiseCallback<CourseResult>() {
            @Override
            public Promise invoke(CourseResult courseResult) {
                mLoading.setVisibility(View.GONE);
                if (courseResult == null || courseResult.resources == null || courseResult.resources.length == 0) {
                    return null;
                }
                List<DiscussionGroup> groupsList = coverCourseArray2FriendList(courseResult.resources);
                setSortChar(groupsList);
                Collections.sort(groupsList, groupComparator);
                mAdapter.addFriendList(groupsList);
                new IMProvider(mContext).updateRoles(Destination.COURSE, groupsList);
                return null;
            }
        });
    }

    private List<DiscussionGroup> coverCourseArray2FriendList(Course[] courses) {
        List<DiscussionGroup> groupsList = new ArrayList<>();
        for (Course course : courses) {
            DiscussionGroup friend = new DiscussionGroup();
            friend.setId(course.id);
            friend.setType(Destination.COURSE);
            friend.setTitle(course.title);
            friend.setNickname(course.title);
            friend.setMediumAvatar(course.middlePicture);
            friend.setLargeAvatar(course.largePicture);
            groupsList.add(friend);
        }

        return groupsList;
    }

    private Promise loadCourseGroup() {
        final Promise promise = new Promise();
        new CourseProvider(mContext).getLearnCourses()
        .success(new NormalCallback<CourseResult>() {
            @Override
            public void success(CourseResult courseResult) {
                promise.resolve(courseResult);
            }
        });

        return promise;
    }

    private Promise loadGroup() {

        RequestUrl requestUrl = app.bindNewUrl(Const.DISCUSSION_GROUP, true);
        StringBuffer stringBuffer = new StringBuffer(requestUrl.url);
        stringBuffer.append("?start=0&limit=10000/");
        requestUrl.url = stringBuffer.toString();

        final Promise promise = new Promise();
        mDiscussionGroupProvider.getClassrooms(requestUrl).success(new NormalCallback<DiscussionGroupResult>() {
            @Override
            public void success(DiscussionGroupResult discussionGroupResult) {
                if (discussionGroupResult.resources.length != 0) {
                    DiscussionGroup[] groups = discussionGroupResult.resources;
                    List<DiscussionGroup> groupsList = Arrays.asList(groups);
                    setSortChar(groupsList);
                    Collections.sort(groupsList, groupComparator);
                    mAdapter.addFriendList(groupsList);
                    new IMProvider(mContext).updateRoles(Destination.CLASSROOM, groupsList);
                } else {
                    mEmptyNotice.setVisibility(View.VISIBLE);
                }
                promise.resolve(discussionGroupResult);
            }
        });
        return promise;
    }

    public void setSortChar(List<DiscussionGroup> groupList) {
        for (DiscussionGroup discussionGroup : groupList) {
            discussionGroup.nickname = discussionGroup.title;
            discussionGroup.mediumAvatar = discussionGroup.picture;
            String pinyin = characterParser.getSelling(discussionGroup.title);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                discussionGroup.setSortLetters(sortString.toUpperCase());
            } else {
                discussionGroup.setSortLetters("#");
            }
        }
    }
}
