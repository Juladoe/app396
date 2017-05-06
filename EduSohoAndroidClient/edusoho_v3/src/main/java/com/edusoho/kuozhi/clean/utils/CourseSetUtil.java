package com.edusoho.kuozhi.clean.utils;

import com.edusoho.kuozhi.R;

/**
 * Created by DF on 2017/5/4.
 */

public class CourseSetUtil {

    public static int joinCourseProject(String code){
        int result = 0;
        switch (code) {
            case "user.locked":
                result =  R.string.course_user_locked;
                break;
            case "course.unpublished":
                result =  R.string.course_unpublish;
                break;
            case "course.not_buyable":
                result =  R.string.course_not_buy;
                break;
            case "course.closed":
                result =  R.string.course_limit_join;
                break;
            case "course.expired":
                result =  R.string.course_date_limit;
                break;
            case "course.buy_expired":
                result =  R.string.course_project_expire_hint;
                break;
        }
        return result;
    }

}
