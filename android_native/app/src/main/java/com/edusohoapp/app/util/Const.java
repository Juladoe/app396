package com.edusohoapp.app.util;

import com.edusohoapp.app.R;
import com.edusohoapp.app.entity.SettingItem;

import java.util.Objects;

public class Const {

    //popular热门 recommended推荐 latest最新
    public static final String[] SORT = {
        "latest", "popular", "recommended"
    };
    //默认分页数量
    public static final int LIMIT = 10;

    public static final int OK = 200;
    public static final int ERROR_200 = 200;
    public static final boolean memCacheNo = false;
    public static final boolean fileCacheYes = true;

    public static final String RESULT_OK = "ok";
    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_ERROR = "error";

    public static final String DB_NAME = "edusoho";
    public static final String T_CAROUSEL = "edusoho_carousel";
    public static final String INIT_SQL = "create table edusoho_carousel (title varchar(100), image varchar(200), action varchar(50))";
    public static final String CLEAR_DB = "delete from edusoho_carousel";

    public static final String RECOMMEND_SCHOOL = "recommendschool";
    public static final String CAROUSEL = "carousel";
    public static final String SEARCH = "searchschool/";
    public static final String COURSE_LIST = "courses";
    public static final String COURSE = "courses/";
    public static final String COURSELESSON = "courses/{courseId}/items?";
    public static final String CHECKTOKEN = "login_with_token?";
    public static final String USER = "user/";
    public static final String REGIST = "user_register";
    public static final String LOGIN = "login";
    public static final String ADDCOMMENT = "courses/{courseId}/review_create";
    public static final String COMMENTLIST = "commentlist/";
    public static final String ABOUT = "about";
    public static final String NOTICE = "me/notifications?";
    public static final String LEARN = "me/learning_courses";
    public static final String FAVORITES = "me/favorite_courses";
    public static final String PAYCOURSE = "courses/{courseId}/pay";
    public static final String FAVORITE = "courses/{courseId}/favorite?";
    public static final String UNFAVORITE = "courses/{courseId}/unfavorite?";
    public static final String REFUNDCOURSE = "courses/{courseId}/refund";
    public static final String CHECKORDER = "courses/{courseId}/can_learn?";
    public static final String VERIFYSCHOOL = "/mapi/login_with_site";
    public static final String LOGOUT = "logout?";
    public static final String LESSONITEM = "courses//lessons/?";
    public static final String LEARNSTATUS = "courses/{courseId}/lessons/{lessonId}/learn_status";

    public static final int LEFT = 0001;
    public static final int RIGHT = 0002;

    public static final int CHECKBOX_ITEM = 0001;
    public static final int NORMAL_ITEM = 0002;

    public static final Object[] QUICK_COMMENTS = {
            "不错", 2.0f,
            "感觉很好", 3.0f,
            "课程内容很好", 4.0f,
            "再接再厉，很喜欢这个课程的内容", 5.0f
    };


    public static final int LEARNING_REQUEST = 0x1001;
    public static final int LEARNING_RESULT = 0x1002;
    public static final int NORMAL_RESULT_REFRESH = 0x1003;

    public static final int FAVORITE_REQUEST = 0x1004;
    public static final int COURSEINFO_REQUEST = 0x1005;

    public static final int COURSELESSON_REQUEST = 0x1001;
}
