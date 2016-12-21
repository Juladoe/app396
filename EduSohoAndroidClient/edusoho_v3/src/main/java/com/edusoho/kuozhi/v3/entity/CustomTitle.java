package com.edusoho.kuozhi.v3.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DF on 2016/12/21.
 */

public class CustomTitle {
//    public String customChapterEnable;
//    public String chapterName;
//    public String partName;
//    public static class CustomTitleDeserializer implements JsonDeserializer<CustomTitle> {
//        @Override
//        public CustomTitle deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//            CustomTitle customTitle = new CustomTitle();
//            JsonObject jsonObject = jsonElement.getAsJsonObject();
//            customTitle.customChapterEnable = jsonObject.get("custom_chapter_enabled").getAsString();
//            customTitle.chapterName = jsonObject.get("chapter_name").getAsString();
//            customTitle.partName = jsonObject.get("part_name").getAsString();
//            return customTitle;
//        }
//    }

    @SerializedName("custom_chapter_enabled")
    private String customChapterEnable;

    @SerializedName("chapter_name")
    private String chapterName;

    @SerializedName("part_name")
    private String partName;

    public String getCustomChapterEnable(){
        return customChapterEnable;
    }

    public String getChapterName(){
        return chapterName;
    }

    public String getPartName(){
        return partName;
    }
}
