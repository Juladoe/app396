package com.edusoho.kuozhi.homework;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.homework.model.HomeWorkModel;
import com.edusoho.kuozhi.homework.model.HomeworkProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

/**
 * Created by Melomelon on 2015/10/13.
 */
public class HomeworkSummaryActivity extends ActionBarBaseActivity {
    public static final String HOME_HORK = "homework";
    public static final String EXERCISE = "exercise";

    private int mLessonId;
    private String mType;
    private TextView tvCourseTitle;
    private TextView homeworkName;
    private TextView homeworkNameContent;
    private TextView homeworkInfo;
    private TextView homeworkInfoContent;
    private Button startBtn;

    private Bundle mBundle;
    private HomeworkProvider mHomeworkProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mBundle = intent.getExtras();

        mLessonId = mBundle == null ? 0 : mBundle.getInt(Const.LESSON_ID);
        mType = mBundle == null ? HOME_HORK : mBundle.getString("type");
        setBackMode(BACK, HOME_HORK.equals(mType) ? "作业" : "练习");
        setContentView(R.layout.homework_summary_layout);
        initView();
        ModelProvider.init(getBaseContext(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String url = new StringBuilder()
                .append(String.format(Const.HOMEWORK_CONTENT, mLessonId))
                .append("?_idType=lesson")
                .toString();
        RequestUrl requestUrl = app.bindNewUrl(url, true);
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();
        mHomeworkProvider.getHomeWork(requestUrl).success(new NormalCallback<HomeWorkModel>() {
            @Override
            public void success(HomeWorkModel homeWorkModel) {
                loadDialog.dismiss();
                renderView(homeWorkModel);
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                loadDialog.dismiss();
            }
        });
    }

    private void renderView(final HomeWorkModel homeWorkModel) {
        tvCourseTitle.setText(homeWorkModel.getCourseTitle());
        homeworkNameContent.setText(homeWorkModel.getLessonTitle());
        homeworkInfoContent.setText(AppUtil.coverCourseAbout(homeWorkModel.getDescription()));
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), HomeworkActivity.class);
                intent.putExtra(HomeworkActivity.HOMEWORK_ID, homeWorkModel.getId());
                intent.putExtra(HOME_HORK, mType);
                startActivity(intent);
            }
        });
    }

    public void initView() {

        tvCourseTitle = (TextView) findViewById(R.id.homework_belong_content);
        homeworkName = (TextView) findViewById(R.id.homework_name);
        homeworkNameContent = (TextView) findViewById(R.id.homework_name_content);
        homeworkInfo = (TextView) findViewById(R.id.homework_info);
        homeworkInfoContent = (TextView) findViewById(R.id.homework_info_content);
        startBtn = (Button) findViewById(R.id.start_homework_btn);
        if (HOME_HORK.equals(mType)) {
            homeworkName.setText("作业名称");
            homeworkInfo.setText("作业说明");
        } else {
            homeworkName.setText("练习名称");
            homeworkInfo.setText("练习说明");
        }

    }
}
