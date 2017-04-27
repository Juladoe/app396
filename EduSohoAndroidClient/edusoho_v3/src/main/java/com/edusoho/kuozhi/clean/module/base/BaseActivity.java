package com.edusoho.kuozhi.clean.module.base;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.edusoho.kuozhi.clean.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by JesseHuang on 2017/4/21.
 */

public class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView<T> {


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onReceiveMessage(MessageEvent messageEvent) {

    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
