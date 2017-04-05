package com.edusoho.kuozhi.clean.module.course.rate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;
import com.edusoho.kuozhi.clean.widget.ESDividerItemDecoration;
import com.edusoho.kuozhi.v3.view.circleImageView.CircularImageView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseProjectRatesFragment extends Fragment implements
        CourseProjectRatesContract.View, CourseProjectFragmentListener {

    private static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    private CourseProjectRatesContract.Presenter mPresenter;
    private RecyclerView rateRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_project_rates, container, false);
        rateRecyclerView = (RecyclerView) view.findViewById(R.id.rv_content);
        return view;
    }

    public CourseProjectFragmentListener newInstance(CourseProject courseProject) {
        CourseProjectRatesFragment fragment = new CourseProjectRatesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COURSE_PROJECT_MODEL, courseProject);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        CourseProject courseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mPresenter = new CourseProjectRatesPresenter(courseProject, this);
        mPresenter.subscribe();
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }

    @Override
    public void showRates(List<Review> reviews) {
        CourseProjectRatingAdapter adapter = new CourseProjectRatingAdapter(getActivity(), reviews);
        rateRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ESDividerItemDecoration esDividerItemDecoration = new ESDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        esDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.course_project_rate_divider));
        rateRecyclerView.addItemDecoration(esDividerItemDecoration);
        rateRecyclerView.setAdapter(adapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView userAvatar;
        public TextView username;
        public RatingBar courseRating;
        public TextView postTime;
        public TextView ratingContent;

        public ViewHolder(View view) {
            super(view);
            userAvatar = (CircularImageView) view.findViewById(R.id.civ_user_avatar);
            username = (TextView) view.findViewById(R.id.tv_user_name);
            courseRating = (RatingBar) view.findViewById(R.id.rb_rating);
            postTime = (TextView) view.findViewById(R.id.tv_post_time);
            ratingContent = (TextView) view.findViewById(R.id.tv_rate_content);
        }
    }
}
