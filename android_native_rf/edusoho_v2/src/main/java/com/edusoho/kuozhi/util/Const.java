package com.edusoho.kuozhi.util;

public class Const {

    //popular热门 recommended推荐 latest最新
    public static final String[] SORT = {
            "latest", "popular", "recommended"
    };

    public static final String[] COURSE_INFO_TITLE = {
            "课程", "教师", "评价"
    };

    public static final String[] MY_COURSE_TITLE = {
            "在学课程", "收藏课程", "已学课程"
    };

    /**
     * 考试题目类别
     */
    public static final String[] TESTPAPER_QUESTION_TYPE = {
            "多选题", "判断题", "问答题", "填空题", "材料题", "单选题", "不定项题"
    };

    /**
     * 考试题目 fragment
     */
    public static final String[] TESTPAPER_QUESTIONS = {
            "ChoiceFragment",
            "DetermineFragment",
            "EssayFragment",
            "FillFragment",
            "MaterialFragment",
            "SingleChoiceFragment",
            "UncertainChoiceFragment"
    };

    public static final String[] MY_COURSE_FRAGMENT = {
            "LearningCourseFragment",
            "FavoriteCourseFragment",
            "LearnedCourseFragment"
    };

    public static final String[] COURSE_INFO_FRAGMENT = {
            "CourseInfoFragment",
            "TeacherInfoFragment",
            "ReviewInfoFragment"
    };

    //默认分页数量
    public static final int LIMIT = 10;

    //public message type
    public static final String TAB_MENU_CLICK = "tab_menu_click";
    public static final String TAB_MENU_ID = "tab_menu_id";
    public static final String TESTPAPER_REFRESH_DATA = "testpaper_refresh_data";

    public static final int OK = 200;
    public static final int ERROR_200 = 200;
    public static final boolean memCacheNo = false;
    public static final boolean fileCacheYes = true;

    public static final String RESULT_OK = "ok";
    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_ERROR = "error";
    public static final String CLIENT_CLOSE = "client_closed";

    public static final String DB_NAME = "edusoho";

    public static final String REGIST_DEVICE = "regist_device";
    public static final String COURSES = "Course/getCourses";
    public static final String COURSE = "Course/getCourse";
    public static final String COURSELESSON = "Lesson/getLesson";

    /**
     * course status
     */
    public static final String COURSE_CLOSE = "closed";
    public static final String COURSE_PUBLISHED = "published";
    public static final String COURSE_SERIALIZE = "serialize";

    /**
     * 根据登录UserToken获取问答列表
     */
    public static final String QUESTION = "Course/getCourseThreads";

    /**
     * 获取所有回答
     * 传递参数：courseId，threadId
     */
    public static final String NORMAL_REPLY = "Course/getThreadPost";

    /**
     * 获取教师回复
     * 传递参数：courseId，threadId
     */
    public static final String TEACHER_REPLY = "Course/getThreadTeacherPost";

    /**
     * 回复提交
     */
    public static final String REPLY_SUBMIT = "Course/postThread";

    /**
     * 编辑回复提交
     */
    public static final String REPLY_EDIT_SUBMIT = "Course/updatePost";

    /**
     * 分享url
     */
    public static final String SHARD_COURSE_URL = "School/getShradCourseUrl";

    /**
     * 获取一个问题的详细信息（问题标题，内容，发问者，发问时间等）
     * 传递参数：courseId，threadId
     */
    public static final String QUESTION_INFO = "Course/getThread";

    public static final String EDIT_QUESTION_INFO = "Course/updateThread";

    public static final String CHECKTOKEN = "User/loginWithToken";
    public static final String REGIST = "User/regist";
    public static final String LOGIN = "User/login";
    public static final String ADDCOMMENT = "Course/commitCourse";
    public static final String COMMENTLIST = "courses/{courseId}/reviews";
    public static final String ABOUT = "School/getSchoolInfo";
    public static final String COURSE_COLUMN = "course_column";
    public static final String NOTICE = "me/notifications?";
    public static final String LEARNING = "Course/getLearningCourse";
    public static final String LEARNED = "Course/getLearnedCourse";
    public static final String FAVORITES = "Course/getFavoriteCoruse";
    public static final String PAYCOURSE = "Order/payCourse";
    public static final String FAVORITE = "Course/favoriteCourse";
    public static final String UNFAVORITE = "Course/unFavoriteCourse";
    public static final String REFUNDCOURSE = "courses/{courseId}/refund";
    public static final String VERIFYSCHOOL = "/School/getSchoolSite";
    public static final String VERIFYVERSION = "/systeminfo?version=2";
    public static final String LOGOUT = "User/logout";
    public static final String DEFAULT_UPDATE_URL = "http://open.edusoho.com/mobile/meta.php";

    public static final String SCHOOL_BANNER = "School/getSchoolBanner";
    public static final String SCHOOL_Announcement = "School/getSchoolAnnouncement";
    public static final String RECOMMEND_COURSES = "School/getRecommendCourses";
    public static final String LASTEST_COURSES = "School/getLatestCourses";
    public static final String WEEK_COURSES = "School/getWeekRecommendCourses";
    public static final String CATEGORYS = "Category/getAllCategories";
    public static final String USERTERMS = "School/getUserterms";
    public static final String USERINFO = "User/getUserInfo";
    public static final String REVIEWS = "Course/getReviews";
    public static final String LESSONS = "Lesson/getCourseLessons";
    public static final String SEARCH_COURSE = "Course/searchCourse";
    public static final String TEACHER_COURSES = "Course/getTeacherCourses";
    public static final String COURSE_NOTICE = "Course/getCourseNotice";
    public static final String UN_LEARN_COURSE = "Course/unLearnCourse";
    public static final String LESSON_RESOURCE = "Lesson/getLessonMaterial";
    public static final String LEARN_LESSON = "Lesson/learnLesson";
    public static final String UNLEARN_LESSON = "Lesson/unLearnLesson";
    public static final String LEARN_STATUS = "Lesson/getLearnStatus";
    public static final String VIP_LEARN_COURSE = "Course/vipLearn";

    /**
     * 获取课程状态（是否包含资料，学习状态）
     */
    public static final String LESSON_STATUS = "Lesson/getLessonStatus";

    /**
     * 获取考试课程详情
     */
    public static final String TESTPAPER_INFO = "Lesson/getTestpaperInfo";

    /**
     * 获取考试课程详情
     */
    public static final String TESTPAPER_FULL_INFO = "Testpaper/doTestpaper";

    /**
     * 获取考试课程详情
     */
    public static final String COURSE_CODE = "Course/coupon";

    //意见反馈
    public static final String SUGGESTION = "School/sendSuggestion";

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

    public static final int CACHE_CODE = 0x1111;

    //服务器api版本
    public static final int NORMAL_VERSIO = 0;
    public static final int HEIGHT_VERSIO = 1;
    public static final int LOW_VERSIO = -1;

    public static final String DEFAULT_SCHOOL = "defaultSchool";
    public static final String PUBLISHED = "published";

    public static final String COURSE_ID = "courseId";
    public static final String FREE = "free";
    public static final String LESSON_ID = "lessonId";
    public static final String MEDIA_URL = "mediaUrl";
    public static final String MEDIA_ID = "mediaId";
    public static final String STATUS = "status";
    public static final String MEDIA_SOURCE = "mediaSource";
    public static final String LESSON_TYPE = "type";
    public static final String ACTIONBAT_TITLE = "title";
    public static final String LIST_JSON = "list_json";

    public static final String QUESTION_TITLE = "title";
    public static final String QUESTION_EDIT_RESULT = "question_edit_result";
    public static final String THREAD_ID = "thread_id";
    public static final String POST_ID = "post_id";
    public static final String QUESTION_CONTENT = "question_content";
    public static final String NORMAL_CONTENT = "content";

    public static final String REQUEST_CODE = "reply_type";

    /**
     * 普通回复
     */
    public static final int REPLY = 0x01;
    /**
     * 问题编辑
     */
    public static final int EDIT_QUESTION = 0x02;
    /**
     * 回复编辑
     */
    public static final int EDIT_REPLY = 0x03;


}
