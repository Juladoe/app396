package com.edusoho.kuozhi.clean.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 2017/4/24.
 */

public class FragmentPageActivity extends AppCompatActivity {

    private static final String FRAGMENT_NAME = "FragmentName";

    public static void launchFragmentPageActivity(Context context, String fragmentName, Bundle bundle) {
        Intent intent = new Intent(context, FragmentPageActivity.class);
        intent.putExtra(FRAGMENT_NAME, fragmentName);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_page_layout);
        initView();
    }

    private void initView() {
        if (getIntent() == null) {
            finish();
        }
        String fragmentName = getIntent().getStringExtra(FRAGMENT_NAME);
        Bundle bundle = getIntent().getExtras();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = Fragment.instantiate(this, fragmentName, bundle);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
