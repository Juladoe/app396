package com.edusoho.kuozhi.v3.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.homework.HomeworkSummary;

/**
 * Created by Melomelon on 2015/10/10.
 */
public class ExerciseOptionDialog extends Dialog {

    private Context mContext;
    private LinearLayout mLayout;

    private TextView homework;
    private TextView exercise;

    private String mLessonTitle;

    public View.OnClickListener mClickListener;


    public ExerciseOptionDialog(Context context,String title) {
        super(context, R.style.FullDialogTheme);
        mContext = context;
        mLessonTitle = title;
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
                int id = view.getId();
                if (id == R.id.do_homework) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, HomeworkSummary.class);
                    intent.putExtra("lesson",mLessonTitle);
                    mContext.startActivity(intent);
                }
                if (id == R.id.do_exercise) {
                    //todo
                    Toast.makeText(mContext, "exercise", Toast.LENGTH_SHORT);
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
