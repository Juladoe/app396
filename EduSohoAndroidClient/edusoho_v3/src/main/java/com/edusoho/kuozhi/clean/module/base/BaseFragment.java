package com.edusoho.kuozhi.clean.module.base;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by JesseHuang on 2017/4/21.
 */

public class BaseFragment<T extends BasePresenter> extends Fragment implements BaseView<T> {

    @Override
    public void showToast(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_LONG).show();
    }
}
