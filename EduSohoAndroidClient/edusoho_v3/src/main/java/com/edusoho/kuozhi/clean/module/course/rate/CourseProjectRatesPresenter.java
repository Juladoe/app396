package com.edusoho.kuozhi.clean.module.course.rate;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.v3.model.bal.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseProjectRatesPresenter implements CourseProjectRatesContract.Presenter {

    private CourseProject mCourseProject;
    private CourseProjectRatesContract.View mView;

    public CourseProjectRatesPresenter(CourseProject courseProject, CourseProjectRatesContract.View view) {
        this.mCourseProject = courseProject;
        this.mView = view;
    }

    private void getRates(String courseProjectId) {
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Review review = new Review();
            review.content = "this is content ";
            User user = new User();
            user.nickname = "Jesse";
            user.avatar = "http://devtest.edusoho.cn:82/files/user/2017/03-27/100636c6d340042729.png";
            review.user = user;
            review.updatedTime = "2017-03-30";
            review.rating = 4.3f;
            reviews.add(review);
        }
        mView.showRates(reviews);
    }

    @Override
    public void subscribe() {
        getRates(mCourseProject.id);
    }

    @Override
    public void unsubscribe() {

    }
}
