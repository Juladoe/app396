package com.gensee.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by suju on 16/10/25.
 */
public class GenseePlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null || bundle.isEmpty()) {
            finish();
            return;
        }

        Intent intent = null;
        boolean replayState = bundle.getBoolean("replayState", false);
        if (replayState) {
            intent = new Intent(getBaseContext(), GenseeVodPlayerActivity.class);
            intent.putExtras(bundle);
        } else {
            intent = new Intent(getBaseContext(), GenseeLivePlayActivity.class);
            intent.putExtras(bundle);
        }

        startActivity(intent);
        finish();
    }
}
