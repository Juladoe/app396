package com.edusoho.kuozhi;

import com.edusoho.kuozhi.model.SchoolRoom.SchoolRoomResult;
import com.edusoho.kuozhi.ui.fragment.SchoolRoomFragment;

import java.util.Iterator;
import java.util.List;

/**
 * Created by howzhi on 15/7/10.
 */
public class HowSchoolRoomFragment extends SchoolRoomFragment {

    @Override
    protected void filterSchoolRoomDatas(List<SchoolRoomResult> list) {
        Iterator<SchoolRoomResult> iterator = list.iterator();
        while (iterator.hasNext()) {
            String title = iterator.next().title;
            if (title.equals("讨论") || title.equals("笔记")) {
                iterator.remove();
            }
        }
    }
}
