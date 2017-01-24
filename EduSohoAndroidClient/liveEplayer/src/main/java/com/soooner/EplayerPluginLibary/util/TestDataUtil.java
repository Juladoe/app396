package com.soooner.EplayerPluginLibary.util;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: SlothMonkey
 * Date: 13-10-10
 * Time: 下午2:53
 * To change this template use File | Settings | File Templates.
 */
public class TestDataUtil {


    public static final String[] FIRST_TAGS = new String[]{"电视剧","电影","综艺","娱乐","动漫","旅游.纪录片","搞笑","片花","教育","体育","音乐","时尚.生活",};
    public static final String[] SECOND_TAGS = new String[]{"最新","言情","古装","偶像","武侠","穿越","家庭","战争","历史","都市","农村","年代","喜剧","刑侦","悬疑","谍战","军旅","伦理","泰剧","台剧","韩剧","美剧","TVB","内地","港台","日韩","欧美","访谈","选秀","搞笑","时尚","杂谈","情感","曲艺","美食","舞蹈"};

    //随即Boolean值
    public static boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    //获取随机分类内容
    public static String[] getRandomTag(boolean isFirstTags){
        if(isFirstTags){
            return FIRST_TAGS;
        }
        Random random = new Random();
        int length = random.nextInt(SECOND_TAGS.length);
        String[] targetTags = new String[length];
        System.arraycopy(SECOND_TAGS, 0, targetTags, 0, length);
        return targetTags;
    }

    public static String ArrayToString(String[] stringArr){
        if(stringArr == null){
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (String str : stringArr) {
            builder.append(str).append(",");
        }
        return builder.toString();
    }



//    public static void main(String[] args) {
//        System.out.println(ArrayToString(getRandomTag(true)));
//        for (int i = 0; i < 100; i++) {
////            System.out.println(getRandomBoolean());
//            System.out.println(ArrayToString(getRandomTag(false)));
//        }
//    }





}
