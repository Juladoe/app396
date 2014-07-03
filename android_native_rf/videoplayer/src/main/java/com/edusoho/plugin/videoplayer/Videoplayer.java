package com.edusoho.plugin.videoplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.broov.player.DemoRenderer;
import com.broov.player.FileManager;
import com.broov.player.Globals;
import com.broov.player.NativeVideoPlayer;
import com.broov.player.VideoPlayer;

public class Videoplayer extends Activity {

    private String[] playList = {
            "/sdcard-ext/q.mp4",
            "/sdcard-ext/seg0.mp4"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
        //readSettings();
        String filename =   playList[0];
        Intent intent = new Intent();

        if (Globals.isNativeVideoPlayerFeatureEnabled()) {
            intent = new Intent(this, NativeVideoPlayer.class);
        } else {
            intent = new Intent(this, VideoPlayer.class);
        }
        intent.putExtra("videofilename", filename);
        startActivity(intent);
    }

    private static FileManager flmg;
    private static SharedPreferences settings;

    public void readSettings() {

        settings = getSharedPreferences(Globals.PREFS_NAME, MODE_PRIVATE);

        Globals.dbHide         = settings.getBoolean(Globals.PREFS_HIDDEN, Globals.dbHide);
        Globals.dbSubtitle     = settings.getBoolean(Globals.PREFS_SUBTITLE, Globals.dbSubtitle);
        Globals.dbColor        = settings.getInt(Globals.PREFS_COLOR, Globals.dbColor);
        Globals.dbSort         = settings.getInt(Globals.PREFS_SORT, Globals.dbSort);
        Globals.dbAudioLoop    = settings.getInt(Globals.PREFS_AUDIOLOOP, Globals.dbAudioLoop);
        Globals.dbVideoLoop    = settings.getInt(Globals.PREFS_VIDEOLOOP, Globals.dbVideoLoop);
        Globals.dbSubtitleSize = settings.getInt(Globals.PREFS_SUBTITLESIZE, Globals.dbSubtitleSize);
        Globals.dbLastOpenDir  = settings.getString(Globals.PREFS_LASTOPENDIR, Globals.dbLastOpenDir);
        Globals.dbSubtitleEncoding        = settings.getInt(Globals.PREFS_SUBTITLEENCODING, Globals.dbSubtitleEncoding);
        Globals.dbDefaultHome = settings.getString(Globals.PREFS_DEFAULTHOME, Globals.dbDefaultHome);
        Globals.dbSubtitleFont = settings.getString(Globals.PREFS_SUBTITLEFONT, Globals.dbSubtitleFont);
        Globals.dbSkipframes   = settings.getBoolean(Globals.PREFS_SKIPFRAME, Globals.dbSkipframes);
        //System.out.println("On Main Start skipframes:"+Globals.dbSkipframes);
        Globals.dbadvancedskip = settings.getBoolean(Globals.PREFS_ADVSKIPFRAMES, Globals.dbadvancedskip);
        Globals.dbadvancedbidirectional = settings.getBoolean(Globals.PREFS_BIDIRECTIONAL, Globals.dbadvancedbidirectional);
        Globals.dbadvancedffmpeg = settings.getBoolean(Globals.PREFS_ADVFFMPEG, Globals.dbadvancedffmpeg);
        Globals.dbadvancedyuv = settings.getInt(Globals.PREFS_ADVYUV2RGB, Globals.dbadvancedyuv);

        Globals.dbadvancedminvideoq=settings.getInt(Globals.PREFS_ADVMINVIDEOQ, Globals.dbadvancedminvideoq);
        Globals.dbadvancedmaxvideoq=settings.getInt(Globals.PREFS_ADVMAXVIDEOQ, Globals.dbadvancedmaxvideoq);
        Globals.dbadvancedmaxaudioq=settings.getInt(Globals.PREFS_ADVMAXAUDIOQ, Globals.dbadvancedmaxaudioq);
        Globals.dbadvancedstreamminvideoq=settings.getInt(Globals.PREFS_ADVSTREAMMINVIDEOQ, Globals.dbadvancedstreamminvideoq);
        Globals.dbadvancedstreammaxvideoq=settings.getInt(Globals.PREFS_ADVSTREAMMAXVIDEOQ, Globals.dbadvancedstreammaxvideoq);
        Globals.dbadvancedstreammaxaudioq=settings.getInt(Globals.PREFS_ADVSTREAMMAXAUDIOQ, Globals.dbadvancedstreammaxaudioq);
        Globals.dbadvanceddebug = settings.getBoolean(Globals.PREFS_ADVDEBUG, Globals.dbadvanceddebug);
        Globals.dbadvancedpixelformat=settings.getInt(Globals.PREFS_ADVPIXELFORMAT, Globals.dbadvancedpixelformat);
        Globals.dbadvancedavsyncmode=settings.getInt(Globals.PREFS_ADVAVSYNCMODE, Globals.dbadvancedavsyncmode);
        Globals.dbadvancedswsscaler=settings.getInt(Globals.PREFS_ADVSWSSCALER, Globals.dbadvancedswsscaler);


        flmg = new FileManager();
        Globals.setShowHiddenFiles(Globals.dbHide);
        Globals.setShowSubTitle(Globals.dbSubtitle);
        Globals.setSortType(Globals.dbSort);
        Globals.setAudioLoop(Globals.dbAudioLoop);
        Globals.setVideoLoop(Globals.dbVideoLoop);
        Globals.setSubTitleSize(Globals.dbSubtitleSize);
        Globals.setLastOpenDir(Globals.dbLastOpenDir);
        Globals.setSubTitleFont(Globals.dbSubtitleFont);
        Globals.setSkipFrames(Globals.dbSkipframes);
        //advanced
        Globals.setadvSkipFrames(Globals.dbadvancedskip);
        Globals.setadvbidirectional(Globals.dbadvancedbidirectional);
        Globals.setadvffmpeg(Globals.dbadvancedffmpeg);
        Globals.setadvyuv2rgb(Globals.dbadvancedyuv);
        Globals.setadvminvideoq(Globals.dbadvancedminvideoq);
        Globals.setadvmaxvideoq(Globals.dbadvancedmaxvideoq);
        Globals.setadvmaxaudioq(Globals.dbadvancedmaxaudioq);

        Globals.setadvstreamminvideoq(Globals.dbadvancedstreamminvideoq);
        Globals.setadvstreammaxvideoq(Globals.dbadvancedstreammaxvideoq);
        Globals.setadvstreammaxaudioq(Globals.dbadvancedstreammaxaudioq);
        Globals.setadvpixelformat(Globals.dbadvancedpixelformat);
        Globals.setadvavsyncmode(Globals.dbadvancedavsyncmode);
        Globals.setadvdebug(Globals.dbadvanceddebug);
        Globals.setadvswsscaler(Globals.dbadvancedswsscaler);

        DemoRenderer.UpdateValuesFromSettings();
    }

}
