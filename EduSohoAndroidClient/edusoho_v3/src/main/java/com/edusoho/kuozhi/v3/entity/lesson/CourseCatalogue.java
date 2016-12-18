package com.edusoho.kuozhi.v3.entity.lesson;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by DF on 2016/12/14.
 */

public class CourseCatalogue {

    private Map<Integer, String> learnStatuses;

    private List<LessonsBean> lessons;

    public Map<Integer, String> getLearnStatuses() {
        return learnStatuses;
    }

    public void setLearnStatuses(Map<Integer, String> learnStatuses) {
        this.learnStatuses = learnStatuses;
    }

    public List<LessonsBean> getLessons() {
        return lessons;
    }

    public void setLessons(List<LessonsBean> lessons) {
        this.lessons = lessons;
    }

    public static class LearnStatusesBean {
        /**
         * 219 : learning
         * 220 : learning
         * 226 : learning
         * 376 : finished
         */

        @SerializedName("219")
        private String value219;
        @SerializedName("220")
        private String value220;
        @SerializedName("226")
        private String value226;
        @SerializedName("376")
        private String value376;

        public String getValue219() {
            return value219;
        }

        public void setValue219(String value219) {
            this.value219 = value219;
        }

        public String getValue220() {
            return value220;
        }

        public void setValue220(String value220) {
            this.value220 = value220;
        }

        public String getValue226() {
            return value226;
        }

        public void setValue226(String value226) {
            this.value226 = value226;
        }

        public String getValue376() {
            return value376;
        }

        public void setValue376(String value376) {
            this.value376 = value376;
        }
    }

    public static class LessonsBean {

        private String content;
        private String copyId;
        private String courseId;
        private String createdTime;
        private String id;
        private String itemType;
        private String length;
        private String number;
        private String parentId;
        private String seq;
        private String title;
        private String type;
        private String chapterId;
        private String endTime;
        private String exerciseId;
        private String free;
        private String giveCredit;
        private String homeworkId;
        private String learnedNum;
        private String liveProvider;
        private String materialNum;
        private String maxOnlineNum;
        private String mediaId;
        private String mediaName;
        private String mediaSource;
        private String mediaUri;
        private String memberNum;
        private String quizNum;
        private String replayStatus;
        private String requireCredit;
        private String startTime;
        private String status;
        private String summary;
        private String testMode;
        private String testStartTime;
        private String updatedTime;
        private String userId;
        private String viewedNum;
        private UploadFileBean uploadFile;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCopyId() {
            return copyId;
        }

        public void setCopyId(String copyId) {
            this.copyId = copyId;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getChapterId() {
            return chapterId;
        }

        public void setChapterId(String chapterId) {
            this.chapterId = chapterId;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getExerciseId() {
            return exerciseId;
        }

        public void setExerciseId(String exerciseId) {
            this.exerciseId = exerciseId;
        }

        public String getFree() {
            return free;
        }

        public void setFree(String free) {
            this.free = free;
        }

        public String getGiveCredit() {
            return giveCredit;
        }

        public void setGiveCredit(String giveCredit) {
            this.giveCredit = giveCredit;
        }

        public String getHomeworkId() {
            return homeworkId;
        }

        public void setHomeworkId(String homeworkId) {
            this.homeworkId = homeworkId;
        }

        public String getLearnedNum() {
            return learnedNum;
        }

        public void setLearnedNum(String learnedNum) {
            this.learnedNum = learnedNum;
        }

        public String getLiveProvider() {
            return liveProvider;
        }

        public void setLiveProvider(String liveProvider) {
            this.liveProvider = liveProvider;
        }

        public String getMaterialNum() {
            return materialNum;
        }

        public void setMaterialNum(String materialNum) {
            this.materialNum = materialNum;
        }

        public String getMaxOnlineNum() {
            return maxOnlineNum;
        }

        public void setMaxOnlineNum(String maxOnlineNum) {
            this.maxOnlineNum = maxOnlineNum;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public String getMediaName() {
            return mediaName;
        }

        public void setMediaName(String mediaName) {
            this.mediaName = mediaName;
        }

        public String getMediaSource() {
            return mediaSource;
        }

        public void setMediaSource(String mediaSource) {
            this.mediaSource = mediaSource;
        }

        public String getMediaUri() {
            return mediaUri;
        }

        public void setMediaUri(String mediaUri) {
            this.mediaUri = mediaUri;
        }

        public String getMemberNum() {
            return memberNum;
        }

        public void setMemberNum(String memberNum) {
            this.memberNum = memberNum;
        }

        public String getQuizNum() {
            return quizNum;
        }

        public void setQuizNum(String quizNum) {
            this.quizNum = quizNum;
        }

        public String getReplayStatus() {
            return replayStatus;
        }

        public void setReplayStatus(String replayStatus) {
            this.replayStatus = replayStatus;
        }

        public String getRequireCredit() {
            return requireCredit;
        }

        public void setRequireCredit(String requireCredit) {
            this.requireCredit = requireCredit;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getTestMode() {
            return testMode;
        }

        public void setTestMode(String testMode) {
            this.testMode = testMode;
        }

        public String getTestStartTime() {
            return testStartTime;
        }

        public void setTestStartTime(String testStartTime) {
            this.testStartTime = testStartTime;
        }

        public String getUpdatedTime() {
            return updatedTime;
        }

        public void setUpdatedTime(String updatedTime) {
            this.updatedTime = updatedTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getViewedNum() {
            return viewedNum;
        }

        public void setViewedNum(String viewedNum) {
            this.viewedNum = viewedNum;
        }

        public UploadFileBean getUploadFile() {
            return uploadFile;
        }

        public void setUploadFile(UploadFileBean uploadFile) {
            this.uploadFile = uploadFile;
        }

        public static class UploadFileBean {

            private String bucket;
            private String canDownload;
            private String convertHash;
            private ConvertParamsBean convertParams;
            private String convertStatus;
            private String createdTime;
            private String createdUserId;
            private String description;
            private DirectivesBean directives;
            private String endShared;
            private String endUser;
            private String ext;
            private String extno;
            private String fileSize;
            private String filename;
            private String globalId;
            private String hash;
            private String id;
            private String isPublic;
            private String isShared;
            private String length;
            private String mcStatus;
            private String name;
            private String no;
            @SerializedName("private")
            private String privateX;
            private String processNo;
            private String processProgress;
            private String processRetry;
            private String processStatus;
            private String processedTime;
            private String quality;
            private String resType;
            private String reskey;
            private String size;
            private String status;
            private String storage;
            private String targetId;
            private String targetType;
            private String thumbnail;
            private ThumbnailRawBean thumbnail_raw;
            private String type;
            private String updatedTime;
            private String updatedUserId;
            private String usedCount;
            private String userId;
            private String views;

            public String getBucket() {
                return bucket;
            }

            public void setBucket(String bucket) {
                this.bucket = bucket;
            }

            public String getCanDownload() {
                return canDownload;
            }

            public void setCanDownload(String canDownload) {
                this.canDownload = canDownload;
            }

            public String getConvertHash() {
                return convertHash;
            }

            public void setConvertHash(String convertHash) {
                this.convertHash = convertHash;
            }

            public ConvertParamsBean getConvertParams() {
                return convertParams;
            }

            public void setConvertParams(ConvertParamsBean convertParams) {
                this.convertParams = convertParams;
            }

            public String getConvertStatus() {
                return convertStatus;
            }

            public void setConvertStatus(String convertStatus) {
                this.convertStatus = convertStatus;
            }

            public String getCreatedTime() {
                return createdTime;
            }

            public void setCreatedTime(String createdTime) {
                this.createdTime = createdTime;
            }

            public String getCreatedUserId() {
                return createdUserId;
            }

            public void setCreatedUserId(String createdUserId) {
                this.createdUserId = createdUserId;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public DirectivesBean getDirectives() {
                return directives;
            }

            public void setDirectives(DirectivesBean directives) {
                this.directives = directives;
            }

            public String getEndShared() {
                return endShared;
            }

            public void setEndShared(String endShared) {
                this.endShared = endShared;
            }

            public String getEndUser() {
                return endUser;
            }

            public void setEndUser(String endUser) {
                this.endUser = endUser;
            }

            public String getExt() {
                return ext;
            }

            public void setExt(String ext) {
                this.ext = ext;
            }

            public String getExtno() {
                return extno;
            }

            public void setExtno(String extno) {
                this.extno = extno;
            }

            public String getFileSize() {
                return fileSize;
            }

            public void setFileSize(String fileSize) {
                this.fileSize = fileSize;
            }

            public String getFilename() {
                return filename;
            }

            public void setFilename(String filename) {
                this.filename = filename;
            }

            public String getGlobalId() {
                return globalId;
            }

            public void setGlobalId(String globalId) {
                this.globalId = globalId;
            }

            public String getHash() {
                return hash;
            }

            public void setHash(String hash) {
                this.hash = hash;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getIsPublic() {
                return isPublic;
            }

            public void setIsPublic(String isPublic) {
                this.isPublic = isPublic;
            }

            public String getIsShared() {
                return isShared;
            }

            public void setIsShared(String isShared) {
                this.isShared = isShared;
            }

            public String getLength() {
                return length;
            }

            public void setLength(String length) {
                this.length = length;
            }

            public String getMcStatus() {
                return mcStatus;
            }

            public void setMcStatus(String mcStatus) {
                this.mcStatus = mcStatus;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getNo() {
                return no;
            }

            public void setNo(String no) {
                this.no = no;
            }

            public String getPrivateX() {
                return privateX;
            }

            public void setPrivateX(String privateX) {
                this.privateX = privateX;
            }

            public String getProcessNo() {
                return processNo;
            }

            public void setProcessNo(String processNo) {
                this.processNo = processNo;
            }

            public String getProcessProgress() {
                return processProgress;
            }

            public void setProcessProgress(String processProgress) {
                this.processProgress = processProgress;
            }

            public String getProcessRetry() {
                return processRetry;
            }

            public void setProcessRetry(String processRetry) {
                this.processRetry = processRetry;
            }

            public String getProcessStatus() {
                return processStatus;
            }

            public void setProcessStatus(String processStatus) {
                this.processStatus = processStatus;
            }

            public String getProcessedTime() {
                return processedTime;
            }

            public void setProcessedTime(String processedTime) {
                this.processedTime = processedTime;
            }

            public String getQuality() {
                return quality;
            }

            public void setQuality(String quality) {
                this.quality = quality;
            }

            public String getResType() {
                return resType;
            }

            public void setResType(String resType) {
                this.resType = resType;
            }

            public String getReskey() {
                return reskey;
            }

            public void setReskey(String reskey) {
                this.reskey = reskey;
            }

            public String getSize() {
                return size;
            }

            public void setSize(String size) {
                this.size = size;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStorage() {
                return storage;
            }

            public void setStorage(String storage) {
                this.storage = storage;
            }

            public String getTargetId() {
                return targetId;
            }

            public void setTargetId(String targetId) {
                this.targetId = targetId;
            }

            public String getTargetType() {
                return targetType;
            }

            public void setTargetType(String targetType) {
                this.targetType = targetType;
            }

            public String getThumbnail() {
                return thumbnail;
            }

            public void setThumbnail(String thumbnail) {
                this.thumbnail = thumbnail;
            }

            public ThumbnailRawBean getThumbnail_raw() {
                return thumbnail_raw;
            }

            public static class ConvertParamsBean {
                private String convertor;
            }

            public static class DirectivesBean {

                private String output;
                private String thumbOutputBucket;

            }

            public static class ThumbnailRawBean {
                private String bucket;
                private String key;

            }
        }
    }
}
