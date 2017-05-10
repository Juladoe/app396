package com.edusoho.kuozhi.clean.utils.biz;

import android.content.Context;
import android.util.Log;

import com.edusoho.kuozhi.clean.api.CommonApi;
import com.edusoho.kuozhi.clean.bean.CourseSetting;
import com.edusoho.kuozhi.clean.http.HttpUtils;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/5/10.
 */

public class CourseSettingHelper {

    public static void sync(final Context context) {
        HttpUtils.getInstance()
                .baseOnApi()
                .createApi(CommonApi.class)
                .getCourseSet()
                .filter(new Func1<CourseSetting, Boolean>() {
                    @Override
                    public Boolean call(CourseSetting courseSetting) {
                        return courseSetting != null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseSetting>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseSetting courseSetting) {
                        Log.d("Helpersync", "onNext: " + courseSetting.chapterName);
                        context.getApplicationContext()
                                .getSharedPreferences(CourseSetting.COURSE_SETTING, 0)
                                .edit()
                                .putString(CourseSetting.SHOW_STUDENT_NUM_ENABLED_KEY, courseSetting.showStudentNumEnabled)
                                .putString(CourseSetting.CHAPTER_NAME_KEY, courseSetting.chapterName)
                                .putString(CourseSetting.PART_NAME_KEY, courseSetting.partName)
                                .apply();
                    }
                });
    }
}
