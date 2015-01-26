package com.edusoho.kuozhi.ui.QuestionNew;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.EmptyAdapter;
import com.edusoho.kuozhi.adapter.Question.QuestionGridViewImageAdapter;
import com.edusoho.kuozhi.adapter.QuestionNew.QuestionDetatilAnswerListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.Question.ReplyModel;
import com.edusoho.kuozhi.model.Question.ReplyResult;
import com.edusoho.kuozhi.shard.ShareHandler;
import com.edusoho.kuozhi.shard.ShareUtil;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.BaseRefreshListWidget;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import library.PullToRefreshBase;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionDetatilFragment extends BaseFragment {
    private RefreshListWidget mQuestionDetatileAnswerList;
    private QuestionDetatilAnswerListAdapter mQuestionDetatilAnswerListAdapter;
    private View mQuestionDetailDescribe;
    private View mQuestionLoadView;
    private RelativeLayout mQuestionMainLayout;
    private QuestionDetailModel mQuestionDetailModel;
    private int intentThreadId;
    private int intentCourseId;
    private int mQuestionUserId;

    /**
     * gridview内部间隙
     */
    private static final int GRIDVIEW_SPACING = 10;
    private static final StringBuilder SHARE_QUESTION_URL = new StringBuilder();
    private static final float GRIDVIEW_CONTENT_PROPORTION = 0.9f;
    private static int mContentImageSize = 0;

    private static final int REPLYRESULT = 4;

    private HashMap<String, String> mParams = new HashMap<String, String>();

    private String mEmptyText;
    private int mEmptyIcon;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.question_detatil_fragmentlayout);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mQuestionUserId == mActivity.app.loginUser.id) {
            inflater.inflate(R.menu.question_describe_menu, menu);
        } else {
            inflater.inflate(R.menu.question_describe_menu_without_edit, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.question_describe_share) {
            shareQuestion();
        } else if (id == R.id.question_describe_edit) {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.REQUEST_CODE, Const.EDIT_QUESTION);
            bundle.putString(Const.THREAD_ID, String.valueOf(intentThreadId));
            bundle.putString(Const.COURSE_ID, String.valueOf(intentCourseId));
            bundle.putString(Const.QUESTION_TITLE, mQuestionDetailModel.title);
            bundle.putString(Const.QUESTION_CONTENT, mQuestionDetailModel.content);
            bundle.putString(FragmentPageActivity.FRAGMENT, "QuestionReplyActivity");
            startActivityWithBundleAndResult("QuestionReplyActivity", Const.EDIT_QUESTION, bundle);
        }
        return true;
    }

    public void shareQuestion() {
        ShareUtil shareUtil = new ShareUtil(mActivity);
        shareUtil.initShareParams(
                R.drawable.icon,
                mQuestionDetailModel.title,
                SHARE_QUESTION_URL.toString(),
                AppUtil.coverCourseAbout(mQuestionDetailModel.content),
                AQUtility.getCacheFile(AQUtility.getCacheDir(mActivity), mQuestionDetailModel.coursePicture).getAbsolutePath(),
                app.host
        );
        shareUtil.show(new ShareHandler() {
            @Override
            public void handler(String type) {
                //朋友圈
                int wxType = SendMessageToWX.Req.WXSceneTimeline;
                if ("Wechat".equals(type)) {
                    wxType = SendMessageToWX.Req.WXSceneSession;
                }
                shardToMM(mContext, wxType);
            }
        });
    }

    //微信分享
    private boolean shardToMM(Context context, int type) {
        String APP_ID = getResources().getString(R.string.app_id);
        IWXAPI wxApi;
        wxApi = WXAPIFactory.createWXAPI(context, APP_ID, true);
        wxApi.registerApp(APP_ID);
        WXTextObject wXTextObject = new WXTextObject();
        wXTextObject.text = "分享问答";
        WXWebpageObject wxobj = new WXWebpageObject();
        wxobj.webpageUrl = SHARE_QUESTION_URL.toString();
        WXMediaMessage wXMediaMessage = new WXMediaMessage();
        wXMediaMessage.mediaObject = wxobj;
        wXMediaMessage.description = AppUtil.coverCourseAbout(mQuestionDetailModel.content);
        wXMediaMessage.title = mQuestionDetailModel.title;
        wXMediaMessage.setThumbImage(app.query.getCachedImage(mQuestionDetailModel.coursePicture, 99));

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = type;
        req.transaction = System.currentTimeMillis() + "";
        req.message = wXMediaMessage;
        return wxApi.sendReq(req);
    }

    public void initData() {
        Bundle bundle = getArguments();
        changeTitle(bundle.getString(Const.QUESTION_TITLE));
        intentThreadId = bundle.getInt(Const.THREAD_ID);
        intentCourseId = bundle.getInt(Const.COURSE_ID);
        mQuestionUserId = bundle.getInt(Const.QUESTION_USER_ID);
        mEmptyText = bundle.getString("empty_text");
        mEmptyIcon = bundle.getInt("empty_icon");
        SHARE_QUESTION_URL.append(app.host);
        SHARE_QUESTION_URL.append("course/");
        SHARE_QUESTION_URL.append(String.valueOf(intentCourseId) + "/");
        SHARE_QUESTION_URL.append("thread/");
        SHARE_QUESTION_URL.append(String.valueOf(intentThreadId));

        mParams.put("limit", String.valueOf(Const.LIMIT));
        mParams.put("threadId", String.valueOf(intentThreadId));
        mParams.put("courseId", String.valueOf(intentCourseId));
    }

    @Override
    protected void initView(View view) {
        changeTitle("我的提问");
        initData();
        view.findViewById(R.id.question_reply_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Const.REQUEST_CODE, Const.REPLY);
                bundle.putString(Const.THREAD_ID, String.valueOf(intentThreadId));
                bundle.putString(Const.COURSE_ID, String.valueOf(intentCourseId));
                startActivityWithBundleAndResult("QuestionReplyActivity", Const.REPLY, bundle);
            }
        });
        mQuestionMainLayout = (RelativeLayout) view.findViewById(R.id.question_main_layout);
        //问题描述
        mQuestionDetailDescribe = mActivity.getLayoutInflater().inflate(R.layout.question_detatil_describe_inflate, null);
        mQuestionLoadView = view.findViewById(R.id.load_layout);
        getQuestionDetatilDescribeReponseData();

        //问题回复
        mQuestionDetatileAnswerList = (RefreshListWidget) view.findViewById(R.id.question_detail_answer_list);
        mQuestionDetatileAnswerList.getRefreshableView().addHeaderView(mQuestionDetailDescribe);
        mQuestionDetatileAnswerList.setMode(PullToRefreshBase.Mode.BOTH);
        mQuestionDetatileAnswerList.setEmptyText(new String[]{mEmptyText}, mEmptyIcon, EmptyAdapter.MATCH_PARENT);
        mQuestionDetatilAnswerListAdapter = new QuestionDetatilAnswerListAdapter(mContext, R.layout.question_detatil_answer_list_item);
        mQuestionDetatileAnswerList.setAdapter(mQuestionDetatilAnswerListAdapter);
        getQuestionReplyListReponseData(0);
        mQuestionDetatileAnswerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.equals(mQuestionDetailDescribe)) {
                    return;
                }
                ReplyModel replyModel = (ReplyModel) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Const.QUESTION_CONTENT, replyModel);
                bundle.putString(Const.QUESTION_TITLE, mQuestionDetailModel.title);
                bundle.putInt(Const.THREAD_ID, intentThreadId);
                bundle.putInt(Const.USER_ID, mQuestionDetailModel.user.id);
                bundle.putString(FragmentPageActivity.FRAGMENT, "QuestionReplyFragment");
                startActivityWithBundleAndResult("FragmentPageActivity", REPLYRESULT, bundle);
            }
        });
        refushListener();
    }

    public void refushListener() {
        mQuestionDetatileAnswerList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                getQuestionReplyListReponseData(mQuestionDetatileAnswerList.getStart());
                getQuestionDetatilDescribeReponseData();
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                getQuestionReplyListReponseData(0);
                getQuestionDetatilDescribeReponseData();
            }
        });
    }

    public void getQuestionReplyListReponseData(final int start) {
        RequestUrl requestUrl = app.bindUrl(Const.NORMAL_REPLY, true);
        mParams.put("start", String.valueOf(start));
        requestUrl.setParams(mParams);
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mQuestionDetatileAnswerList.onRefreshComplete();
                ReplyResult replyResult = mActivity.parseJsonValue(object, new TypeToken<ReplyResult>() {
                });

                ArrayList<ReplyModel> arrayList = new ArrayList<ReplyModel>();
                for (int i = 0; i < replyResult.data.length; i++) {
                    arrayList.add(replyResult.data[i]);
                }

                mQuestionDetatileAnswerList.pushData(arrayList);
                mQuestionDetatileAnswerList.setStart(start, replyResult.total);
            }
        });
    }

    public void getQuestionDetatilDescribeReponseData() {
        RequestUrl requestUrl = app.bindUrl(Const.QUESTION_INFO, true);
        requestUrl.setParams(mParams);
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mQuestionLoadView.setVisibility(View.GONE);
                mQuestionDetailModel = mActivity.parseJsonValue(object, new TypeToken<QuestionDetailModel>() {
                });
                setQuestionDescribeData();
            }
        });
    }

    public void setQuestionDescribeData() {
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detail_describe_title)).setText(mQuestionDetailModel.title);
        ImageView questionDetailAnswerUserHeadImage = (ImageView) mQuestionDetailDescribe.findViewById(R.id.question_detatil_describe_user_head_image);
        ImageLoader.getInstance().displayImage(mQuestionDetailModel.user.mediumAvatar, questionDetailAnswerUserHeadImage);
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detatil_describe_uesr_name)).setText(mQuestionDetailModel.user.nickname);
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detatile_describe_time)).setText(AppUtil.getPostDays(mQuestionDetailModel.createdTime));
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detatil_describe_content)).setText(Html.fromHtml(fitlerImgTag(mQuestionDetailModel.content)));
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detail_course_title)).setText(mQuestionDetailModel.courseTitle);
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detail_describe_answer_count)).setText(mQuestionDetailModel.postNum + "");
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detail_describe_browse_count)).setText(mQuestionDetailModel.hitNum + "");

        /*-----------------添加GridView图片显示控件------------------------*/
        ArrayList<String> mUrlList = convertUrlStringList(mQuestionDetailModel.content);
        if (mUrlList.size() > 0) {
            GridView gvImage = new GridView(mContext);
            addGridView(gvImage, mQuestionDetailDescribe, mUrlList.size());
            QuestionGridViewImageAdapter qgvia = new QuestionGridViewImageAdapter(mContext, R.layout.question_item_grid_image_view,
                    mUrlList, mContentImageSize, AppUtil.px2sp(mContext, mContext.getResources().getDimension(R.dimen.question_content_image_num_font_size)));
            gvImage.setAdapter(qgvia);
        }
    }

    //过滤html标签里的img图片
    private String fitlerImgTag(String content) {
        return content.replaceAll("(<img src=\".*?\" .>)", "");
    }

    private ArrayList<String> convertUrlStringList(String content) {
        ArrayList<String> urlLits = new ArrayList<String>();
        Matcher m = Pattern.compile("(img src=\".*?\")").matcher(content);
        while (m.find()) {
            String[] s = m.group(1).split("src=");
            String strUrl = s[1].toString().substring(1, s[1].length() - 1);
            urlLits.add(strUrl);
        }
        return urlLits;
    }

    private void addGridView(GridView gvImage, View parent, int imageNum) {
        LinearLayout rlPostInfo = (LinearLayout) parent.findViewById(R.id.question_describe_image);
        rlPostInfo.removeAllViews();
        int horizontalSpacingNum = 2;
        if (imageNum < 3) {
            horizontalSpacingNum = imageNum % 3 - 1;
        }
        int verticalSpacingNum = (int) Math.ceil(imageNum / 3.0) - 1;

        int gridviewWidth = (int) ((EdusohoApp.screenW - 15 * 2) * GRIDVIEW_CONTENT_PROPORTION + horizontalSpacingNum * GRIDVIEW_SPACING);
        int gridviewHeight = (int) ((EdusohoApp.screenW - 15 * 2) * GRIDVIEW_CONTENT_PROPORTION / 3 + verticalSpacingNum * GRIDVIEW_SPACING);

        mContentImageSize = gridviewWidth / 3;

        RelativeLayout.LayoutParams gvLayout = new RelativeLayout.LayoutParams(gridviewWidth,
                gridviewHeight);
        gvLayout.addRule(RelativeLayout.BELOW, R.id.htv_post_content);
        gvLayout.setMargins(0, 15, 0, 0);
        gvImage.setLayoutParams(gvLayout);
        gvImage.setVerticalScrollBarEnabled(false);
        gvImage.setNumColumns(3);
        gvImage.setVerticalSpacing(GRIDVIEW_SPACING);
        gvImage.setHorizontalSpacing(GRIDVIEW_SPACING);
        gvImage.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        rlPostInfo.addView(gvImage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.EDIT_QUESTION:
                getQuestionDetatilDescribeReponseData();
                break;

            case Const.REPLY:
                getQuestionDetatilDescribeReponseData();
                mQuestionDetatilAnswerListAdapter.clear();
                getQuestionReplyListReponseData(0);
                break;

            case REPLYRESULT:
                mQuestionDetatilAnswerListAdapter.clear();
                getQuestionReplyListReponseData(0);
                break;
        }
    }
}
