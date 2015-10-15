package com.edusoho.kuozhi.v3.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.homework.HomeworkSummary;
import com.edusoho.kuozhi.v3.util.appplugin.PluginUtil;

/**
 * Created by Melomelon on 2015/10/10.
 */
public class ExerciseOptionDialog extends Dialog {

    private Context mContext;
    private LinearLayout mLayout;

    private TextView homework;
    private TextView exercise;

    private String mLessonTitle;
    private int mLessonId;

    public View.OnClickListener mClickListener;


    public ExerciseOptionDialog(Context context, String title, int lessonId) {
        super(context, R.style.FullDialogTheme);
        mContext = context;
        mLessonTitle = title;
        mLessonId = lessonId;
        setContentView(R.layout.exercise_option_layout);

        initView();
        initWindow();
    }

    public void initView() {
        homework = (TextView) findViewById(R.id.do_homework);
        exercise = (TextView) findViewById(R.id.do_exercise);
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("lesson", mLessonTitle);
                bundle.putInt("lessonId", mLessonId);

                int id = view.getId();
                if (id == R.id.do_homework) {
                }
                if (id == R.id.do_exercise) {
                }
            }
        };
        homework.setOnClickListener(mClickListener);
        exercise.setOnClickListener(mClickListener);
    }

    public void initWindow() {
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        lp.width = display.getWidth();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 0;

        window.setAttributes(lp);
    }
}
