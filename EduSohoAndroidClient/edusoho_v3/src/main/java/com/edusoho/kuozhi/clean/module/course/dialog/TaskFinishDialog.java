package com.edusoho.kuozhi.clean.module.course.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by JesseHuang on 2017/5/2.
 */

public class TaskFinishDialog extends DialogFragment {

    public static TaskFinishDialog newInstance() {
        Bundle args = new Bundle();

        TaskFinishDialog fragment = new TaskFinishDialog();
        fragment.setArguments(args);
        return fragment;
    }
}
