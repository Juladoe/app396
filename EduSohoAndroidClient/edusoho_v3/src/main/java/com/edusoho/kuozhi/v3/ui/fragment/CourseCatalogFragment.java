package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.CatalogueAdapter;
import com.edusoho.kuozhi.v3.entity.lesson.CourseCatalogue;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.google.gson.Gson;

import java.io.File;

/**
 * Created by DF on 2016/12/13.
 */
public class CourseCatalogFragment extends Fragment {

    private View view;
    private RelativeLayout rlSpace;
    private ListView lvCatalog;
    private CatalogueAdapter adapter;

    public CourseCatalogFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_catalog, container, false);
        rlSpace = (RelativeLayout) view.findViewById(R.id.rl_space);
        lvCatalog = (ListView) view.findViewById(R.id.lv_catalog);

        initCatalogue();
        initCache();

        return view;
    }

    private void initCatalogue() {
        final CourseCatalogue courseCatalogue = new Gson().fromJson( s, CourseCatalogue.class);
        courseCatalogue.getLessons().addAll(courseCatalogue.getLessons());
        adapter = new CatalogueAdapter(getActivity(), courseCatalogue);
        lvCatalog.setAdapter(adapter);
        lvCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.changeSelected(position);
                if ("0".equals(courseCatalogue.getLessons().get(position).getFree())) {
                    CommonUtil.shortCenterToast(getActivity(), getString(R.string.unjoin_course_hint));
                    return;
                }
            }
        });
    }

    /**
     * 获取手机可用空间,该界面要先判断是否显示rlSpace
     */
    private void initCache() {
        TextView tvSpace = (TextView) view.findViewById(R.id.tv_space);
        TextView tvCourse = (TextView) view.findViewById(R.id.tv_course);
        tvSpace.setText("可用空间:\t"+" "+getRomAvailableSize());
        tvCourse.setOnClickListener(getCacheCourse());
    }

    public View.OnClickListener getCacheCourse(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * TODO 跳转到课程缓存界面
                  */
            }
        };
    }

    private String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(getActivity(), blockSize * availableBlocks).replace("B","");
    }

    private String s = "{"+
            "    \"learnStatuses\": {\n" +
            "        \"219\": \"learning\",\n" +
            "        \"220\": \"learning\",\n" +
            "        \"226\": \"learning\",\n" +
            "        \"376\": \"finished\"\n" +
            "    },\n" +
            "    \"lessons\": [\n" +
            "        {\n" +
            "            \"content\": \"\",\n" +
            "            \"copyId\": \"0\",\n" +
            "            \"courseId\": \"49\",\n" +
            "            \"createdTime\": \"2016-10-28T16:20:28+08:00\",\n" +
            "            \"id\": \"53\",\n" +
            "            \"itemType\": \"chapter\",\n" +
            "            \"length\": \"\",\n" +
            "            \"number\": \"1\",\n" +
            "            \"parentId\": \"0\",\n" +
            "            \"seq\": \"1\",\n" +
            "            \"title\": \"古诗词\",\n" +
            "            \"type\": \"chapter\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"content\": \"\",\n" +
            "            \"copyId\": \"0\",\n" +
            "            \"courseId\": \"49\",\n" +
            "            \"createdTime\": \"2016-10-28T16:21:52+08:00\",\n" +
            "            \"id\": \"54\",\n" +
            "            \"itemType\": \"chapter\",\n" +
            "            \"length\": \"\",\n" +
            "            \"number\": \"1\",\n" +
            "            \"parentId\": \"53\",\n" +
            "            \"seq\": \"2\",\n" +
            "            \"title\": \"子\",\n" +
            "            \"type\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"chapterId\": \"54\",\n" +
            "            \"content\": \"\",\n" +
            "            \"copyId\": \"0\",\n" +
            "            \"courseId\": \"49\",\n" +
            "            \"createdTime\": \"2016-08-22T15:13:54+08:00\",\n" +
            "            \"endTime\": \"0\",\n" +
            "            \"exerciseId\": \"0\",\n" +
            "            \"free\": \"0\",\n" +
            "            \"giveCredit\": \"0\",\n" +
            "            \"homeworkId\": \"0\",\n" +
            "            \"id\": \"218\",\n" +
            "            \"itemType\": \"lesson\",\n" +
            "            \"learnedNum\": \"0\",\n" +
            "            \"length\": \"14:51\",\n" +
            "            \"liveProvider\": \"0\",\n" +
            "            \"materialNum\": \"0\",\n" +
            "            \"maxOnlineNum\": \"0\",\n" +
            "            \"mediaId\": \"1039\",\n" +
            "            \"mediaName\": \"最新爆笑集合，墙外两天破百万观看_高清.mp4\",\n" +
            "            \"mediaSource\": \"self\",\n" +
            "            \"mediaUri\": \"\",\n" +
            "            \"memberNum\": \"0\",\n" +
            "            \"number\": \"1\",\n" +
            "            \"quizNum\": \"0\",\n" +
            "            \"replayStatus\": \"ungenerated\",\n" +
            "            \"requireCredit\": \"0\",\n" +
            "            \"seq\": \"3\",\n" +
            "            \"startTime\": \"0\",\n" +
            "            \"status\": \"published\",\n" +
            "            \"summary\": \"最好笑的视频\",\n" +
            "            \"testMode\": \"normal\",\n" +
            "            \"testStartTime\": \"0\",\n" +
            "            \"title\": \"课时-mp4视频\",\n" +
            "            \"type\": \"video\",\n" +
            "            \"updatedTime\": \"1477642917\",\n" +
            "            \"userId\": \"1\",\n" +
            "            \"viewedNum\": \"0\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"chapterId\": \"54\",\n" +
            "            \"content\": \"\",\n" +
            "            \"copyId\": \"0\",\n" +
            "            \"courseId\": \"49\",\n" +
            "            \"createdTime\": \"2016-08-22T15:14:43+08:00\",\n" +
            "            \"endTime\": \"0\",\n" +
            "            \"exerciseId\": \"0\",\n" +
            "            \"free\": \"0\",\n" +
            "            \"giveCredit\": \"0\",\n" +
            "            \"homeworkId\": \"0\",\n" +
            "            \"id\": \"219\",\n" +
            "            \"itemType\": \"lesson\",\n" +
            "            \"learnedNum\": \"0\",\n" +
            "            \"length\": \"\",\n" +
            "            \"liveProvider\": \"0\",\n" +
            "            \"materialNum\": \"0\",\n" +
            "            \"maxOnlineNum\": \"0\",\n" +
            "            \"mediaId\": \"14\",\n" +
            "            \"mediaName\": \"Android开发进阶-目录与样章(Simple著).pdf\",\n" +
            "            \"mediaSource\": \"self\",\n" +
            "            \"mediaUri\": \"\",\n" +
            "            \"memberNum\": \"0\",\n" +
            "            \"number\": \"2\",\n" +
            "            \"quizNum\": \"0\",\n" +
            "            \"replayStatus\": \"ungenerated\",\n" +
            "            \"requireCredit\": \"0\",\n" +
            "            \"seq\": \"4\",\n" +
            "            \"startTime\": \"0\",\n" +
            "            \"status\": \"published\",\n" +
            "            \"summary\": \"Android开发进阶-目录与样章(Simple著)\",\n" +
            "            \"testMode\": \"normal\",\n" +
            "            \"testStartTime\": \"0\",\n" +
            "            \"title\": \"课时-pdf文档\",\n" +
            "            \"type\": \"document\",\n" +
            "            \"updatedTime\": \"1477642917\",\n" +
            "            \"userId\": \"1\",\n" +
            "            \"viewedNum\": \"0\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"chapterId\": \"54\",\n" +
            "            \"content\": \"\",\n" +
            "            \"copyId\": \"0\",\n" +
            "            \"courseId\": \"49\",\n" +
            "            \"createdTime\": \"2016-08-22T16:07:20+08:00\",\n" +
            "            \"endTime\": \"0\",\n" +
            "            \"exerciseId\": \"0\",\n" +
            "            \"free\": \"0\",\n" +
            "            \"giveCredit\": \"0\",\n" +
            "            \"homeworkId\": \"0\",\n" +
            "            \"id\": \"220\",\n" +
            "            \"itemType\": \"lesson\",\n" +
            "            \"learnedNum\": \"0\",\n" +
            "            \"length\": \"\",\n" +
            "            \"liveProvider\": \"0\",\n" +
            "            \"materialNum\": \"0\",\n" +
            "            \"maxOnlineNum\": \"0\",\n" +
            "            \"mediaId\": \"1043\",\n" +
            "            \"mediaName\": \"纳兰容若.ppt\",\n" +
            "            \"mediaSource\": \"self\",\n" +
            "            \"mediaUri\": \"\",\n" +
            "            \"memberNum\": \"0\",\n" +
            "            \"number\": \"3\",\n" +
            "            \"quizNum\": \"0\",\n" +
            "            \"replayStatus\": \"ungenerated\",\n" +
            "            \"requireCredit\": \"0\",\n" +
            "            \"seq\": \"5\",\n" +
            "            \"startTime\": \"0\",\n" +
            "            \"status\": \"published\",\n" +
            "            \"summary\": \"\",\n" +
            "            \"testMode\": \"normal\",\n" +
            "            \"testStartTime\": \"0\",\n" +
            "            \"title\": \"课时-纳兰容若ppt\",\n" +
            "            \"type\": \"ppt\",\n" +
            "            \"updatedTime\": \"1477642917\",\n" +
            "            \"uploadFile\": {\n" +
            "                \"bucket\": \"ese1a3b8c7d1of\",\n" +
            "                \"canDownload\": \"0\",\n" +
            "                \"convertHash\": \"courselesson-49/20160822040702-nrnxwkyp8f4k08ow\",\n" +
            "                \"convertParams\": {\n" +
            "                    \"convertor\": \"ppt\"\n" +
            "                },\n" +
            "                \"convertStatus\": \"success\",\n" +
            "                \"createdTime\": \"1471853222\",\n" +
            "                \"createdUserId\": \"1\",\n" +
            "                \"description\": \"\",\n" +
            "                \"directives\": {\n" +
            "                    \"output\": \"ppt\",\n" +
            "                    \"thumbOutputBucket\": \"ese1a3b8c7d1of-pub\"\n" +
            "                },\n" +
            "                \"endShared\": \"0\",\n" +
            "                \"endUser\": \"\",\n" +
            "                \"ext\": \"ppt\",\n" +
            "                \"extno\": \"1043\",\n" +
            "                \"fileSize\": \"1435136\",\n" +
            "                \"filename\": \"纳兰容若.ppt\",\n" +
            "                \"globalId\": \"b80748fbf7f1436ab9050982edc6e849\",\n" +
            "                \"hash\": \"cmd5|bbb42fe452860afe7cda40c9c17e2827\",\n" +
            "                \"id\": \"1043\",\n" +
            "                \"isPublic\": \"0\",\n" +
            "                \"isShared\": \"0\",\n" +
            "                \"length\": \"15\",\n" +
            "                \"mcStatus\": \"no\",\n" +
            "                \"name\": \"纳兰容若.ppt\",\n" +
            "                \"no\": \"b80748fbf7f1436ab9050982edc6e849\",\n" +
            "                \"private\": \"1\",\n" +
            "                \"processNo\": \"8e10a42bdbab41dfb29540bc96a1422f\",\n" +
            "                \"processProgress\": \"0\",\n" +
            "                \"processRetry\": \"0\",\n" +
            "                \"processStatus\": \"ok\",\n" +
            "                \"processedTime\": \"1471853245\",\n" +
            "                \"quality\": \"\",\n" +
            "                \"resType\": \"normal\",\n" +
            "                \"reskey\": \"courselesson-49/20160822040702-nrnxwkyp8f4k08ow\",\n" +
            "                \"size\": \"1435136\",\n" +
            "                \"status\": \"uploaded\",\n" +
            "                \"storage\": \"cloud\",\n" +
            "                \"targetId\": \"49\",\n" +
            "                \"targetType\": \"courselesson\",\n" +
            "                \"thumbnail\": \"http://ese1a3b8c7d1of-pub.upcdn.edusoho.net/courselesson-49/20160822040702-nrnxwkyp8f4k08ow/7a7a82a00380c366_thumb\",\n" +
            "                \"thumbnail_raw\": {\n" +
            "                    \"bucket\": \"ese1a3b8c7d1of-pub\",\n" +
            "                    \"key\": \"courselesson-49/20160822040702-nrnxwkyp8f4k08ow/7a7a82a00380c366_thumb\"\n" +
            "                },\n" +
            "                \"type\": \"ppt\",\n" +
            "                \"updatedTime\": \"0\",\n" +
            "                \"updatedUserId\": \"1\",\n" +
            "                \"usedCount\": \"14\",\n" +
            "                \"userId\": \"13871\",\n" +
            "                \"views\": \"0\"\n" +
            "            },\n" +
            "            \"userId\": \"1\",\n" +
            "            \"viewedNum\": \"0\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"chapterId\": \"54\",\n" +
            "            \"content\": \"\",\n" +
            "            \"copyId\": \"0\",\n" +
            "            \"courseId\": \"49\",\n" +
            "            \"createdTime\": \"2016-08-22T19:04:00+08:00\",\n" +
            "            \"endTime\": \"0\",\n" +
            "            \"exerciseId\": \"0\",\n" +
            "            \"free\": \"0\",\n" +
            "            \"giveCredit\": \"0\",\n" +
            "            \"homeworkId\": \"0\",\n" +
            "            \"id\": \"226\",\n" +
            "            \"itemType\": \"lesson\",\n" +
            "            \"learnedNum\": \"0\",\n" +
            "            \"length\": \"\",\n" +
            "            \"liveProvider\": \"0\",\n" +
            "            \"materialNum\": \"0\",\n" +
            "            \"maxOnlineNum\": \"0\",\n" +
            "            \"mediaId\": \"27\",\n" +
            "            \"mediaName\": \"\",\n" +
            "            \"mediaSource\": \"\",\n" +
            "            \"memberNum\": \"0\",\n" +
            "            \"number\": \"4\",\n" +
            "            \"quizNum\": \"0\",\n" +
            "            \"replayStatus\": \"ungenerated\",\n" +
            "            \"requireCredit\": \"0\",\n" +
            "            \"seq\": \"6\",\n" +
            "            \"startTime\": \"0\",\n" +
            "            \"status\": \"published\",\n" +
            "            \"testMode\": \"normal\",\n" +
            "            \"testStartTime\": \"0\",\n" +
            "            \"title\": \"单元测试-单选题\",\n" +
            "            \"type\": \"testpaper\",\n" +
            "            \"updatedTime\": \"1477642917\",\n" +
            "            \"userId\": \"1\",\n" +
            "            \"viewedNum\": \"0\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"content\": \"\",\n" +
            "            \"copyId\": \"0\",\n" +
            "            \"courseId\": \"49\",\n" +
            "            \"createdTime\": \"2016-10-28T16:22:24+08:00\",\n" +
            "            \"id\": \"55\",\n" +
            "            \"itemType\": \"chapter\",\n" +
            "            \"length\": \"\",\n" +
            "            \"number\": \"2\",\n" +
            "            \"parentId\": \"53\",\n" +
            "            \"seq\": \"7\",\n" +
            "            \"title\": \"请求\",\n" +
            "            \"type\": \"unit\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"chapterId\": \"55\",\n" +
            "            \"content\": \"\",\n" +
            "            \"copyId\": \"0\",\n" +
            "            \"courseId\": \"49\",\n" +
            "            \"createdTime\": \"2016-11-13T15:12:31+08:00\",\n" +
            "            \"endTime\": \"0\",\n" +
            "            \"exerciseId\": \"0\",\n" +
            "            \"free\": \"0\",\n" +
            "            \"giveCredit\": \"0\",\n" +
            "            \"homeworkId\": \"0\",\n" +
            "            \"id\": \"376\",\n" +
            "            \"itemType\": \"lesson\",\n" +
            "            \"learnedNum\": \"0\",\n" +
            "            \"length\": \"04:37\",\n" +
            "            \"liveProvider\": \"0\",\n" +
            "            \"materialNum\": \"0\",\n" +
            "            \"maxOnlineNum\": \"0\",\n" +
            "            \"mediaId\": \"1061\",\n" +
            "            \"mediaName\": \"爱神_标清.flv\",\n" +
            "            \"mediaSource\": \"self\",\n" +
            "            \"mediaUri\": \"\",\n" +
            "            \"memberNum\": \"0\",\n" +
            "            \"number\": \"5\",\n" +
            "            \"quizNum\": \"0\",\n" +
            "            \"replayStatus\": \"ungenerated\",\n" +
            "            \"requireCredit\": \"0\",\n" +
            "            \"seq\": \"8\",\n" +
            "            \"startTime\": \"0\",\n" +
            "            \"status\": \"published\",\n" +
            "            \"summary\": \"\",\n" +
            "            \"testMode\": \"normal\",\n" +
            "            \"testStartTime\": \"0\",\n" +
            "            \"title\": \"爱神_标清\",\n" +
            "            \"type\": \"video\",\n" +
            "            \"updatedTime\": \"1479021158\",\n" +
            "            \"userId\": \"1\",\n" +
            "            \"viewedNum\": \"0\"\n" +
            "        }\n" +

            "    ]\n" +
            "}";
}
