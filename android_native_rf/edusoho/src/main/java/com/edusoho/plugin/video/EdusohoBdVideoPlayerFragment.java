package com.edusoho.plugin.video;

import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;
import com.plugin.edusoho.bdvideoplayer.BdVideoPlayerFragment;

/**
 * Created by howzhi on 14-8-5.
 */
public class EdusohoBdVideoPlayerFragment extends BdVideoPlayerFragment{
    @Override
    public boolean onError(int what, int extra) {
        PopupDialog.createNormal(
                getActivity(), "视频播放", "不好意思～此视频不能在该设备上播放，请联系网站管理员！").show();
        return super.onError(what, extra);
    }
}
