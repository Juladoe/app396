package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.imserver.entity.ConvEntity;
import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.entity.message.Destination;
import com.edusoho.kuozhi.imserver.managar.IMConvManager;
import com.edusoho.kuozhi.imserver.managar.IMRoleManager;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.Friend;
import com.edusoho.kuozhi.v3.model.bal.SchoolApp;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.result.DiscussionGroupResult;
import com.edusoho.kuozhi.v3.model.result.FriendResult;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by 菊 on 2016/4/23.
 */
public class IMProvider extends ModelProvider {

    private static final int EXPAID_TIME = 3600 * 6 * 1000;
    private static final String TAG = "IMProvider";

    public IMProvider(Context context) {
        super(context);
    }

    public ProviderListener<ConvEntity> createConvInfoByUser(final String convNo, int targetId) {
        return getTargetInfoByUser(convNo, targetId);
    }

    public ProviderListener createConvInfoByClassRoom(final String convNo, Classroom classroom) {
        final ProviderListener<ConvEntity> providerListener = new ProviderListener() {
        };
        Role role = new Role();
        role.setType(Destination.CLASSROOM);
        role.setRid(classroom.id);
        role.setAvatar(classroom.middlePicture);
        role.setNickname(classroom.title);

        ConvEntity convEntity = createConvNo(getAppSettingProvider().getCurrentUser().id, convNo, role);
        providerListener.onResponse(convEntity);

        return providerListener;
    }

    public ProviderListener createConvInfoByCourse(String convNo, Course course) {
        final ProviderListener<ConvEntity> providerListener = new ProviderListener() {
        };
        Role role = new Role();
        role.setType(Destination.COURSE);
        role.setRid(course.id);
        role.setAvatar(course.middlePicture);
        role.setNickname(course.title);
        ConvEntity convEntity = createConvNo(getAppSettingProvider().getCurrentUser().id, convNo, role);
        providerListener.onResponse(convEntity);

        return providerListener;
    }

    private ProviderListener getTargetInfoByUser(final String convNo, int targetId) {
        final ProviderListener<ConvEntity> providerListener = new ProviderListener() {
        };
        new UserProvider(mContext).getUserInfo(targetId)
                .success(new NormalCallback<User>() {
                    @Override
                    public void success(User user) {
                        Role role = new Role();
                        role.setType(Destination.USER);
                        role.setRid(user.id);
                        role.setAvatar(user.mediumAvatar);
                        role.setNickname(user.nickname);
                        ConvEntity convEntity = createConvNo(getAppSettingProvider().getCurrentUser().id, convNo, role);
                        providerListener.onResponse(convEntity);
                    }
                });

        return providerListener;
    }

    public ProviderListener<ConvEntity> updateConvInfo(String convNo, String type, int targetId) {
        ProviderListener<ConvEntity> providerListener = new ProviderListener() {};
        IMConvManager imConvManager = IMClient.getClient().getConvManager();
        ConvEntity convEntity = imConvManager.getSingleConv(convNo);

        if ((System.currentTimeMillis() / 1000) - convEntity.getUpdatedTime() < EXPAID_TIME) {
            Log.d(TAG, "ConvEntity not update");
            return providerListener;
        }

        NormalCallback<Role> resultCallback = getConvNoUpdateCallback(convEntity);
        switch (type) {
            case Destination.USER:
                getRoleFromUser(targetId, resultCallback);
                break;
            case Destination.COURSE:
                getRoleFromCourse(targetId, resultCallback);
                break;
            case Destination.CLASSROOM:
                getRoleFromClassRoom(targetId, resultCallback);
                break;
        }

        return providerListener;
    }

    private NormalCallback<Role> getConvNoUpdateCallback(final ConvEntity convEntity) {
        return new NormalCallback<Role>() {
            @Override
            public void success(Role role) {
                updateRole(role);
                convEntity.setAvatar(role.getAvatar());
                convEntity.setTargetName(role.getNickname());
                convEntity.setUpdatedTime(System.currentTimeMillis());
                IMClient.getClient().getConvManager().updateConvByConvNo(convEntity);
                Log.d(TAG, "update convEntity" + convEntity.getConvNo());
                MessageEngine.getInstance().sendMsgToTaget(Const.REFRESH_LIST, null, NewsFragment.class);
            }
        };
    }

    private void updateRole(Role role) {
        IMRoleManager roleManager = IMClient.getClient().getRoleManager();
        Role localRole = roleManager.getRole(role.getType(), role.getRid());

        if (localRole.getRid() == 0) {
            roleManager.createRole(role);
            return;
        }
        roleManager.updateRole(role);
    }

    private void getRoleFromClassRoom(int classRoomId, final NormalCallback<Role> callback) {
        new ClassRoomProvider(mContext).getClassRoom(classRoomId)
                .success(new NormalCallback<Classroom>() {
                    @Override
                    public void success(Classroom classroom) {
                        Role role = new Role();
                        if (classroom != null) {
                            role.setAvatar(classroom.middlePicture);
                            role.setNickname(classroom.title);
                            role.setRid(classroom.id);
                            role.setType(Destination.CLASSROOM);
                        }
                        callback.success(role);
                    }
                });
    }

    private void getRoleFromCourse(int courseId, final NormalCallback<Role> callback) {
        new CourseProvider(mContext).getCourse(courseId)
                .success(new NormalCallback<CourseDetailsResult>() {
                    @Override
                    public void success(CourseDetailsResult courseDetailsResult) {
                        Role role = new Role();
                        Course course = courseDetailsResult.course;
                        if (course != null) {
                            role.setAvatar(course.middlePicture);
                            role.setNickname(course.title);
                            role.setRid(course.id);
                            role.setType(Destination.COURSE);
                        }
                        callback.success(role);
                    }
                });
    }

    private void getRoleFromUser(int userId, final NormalCallback<Role> callback) {
        new UserProvider(mContext).getUserInfo(userId)
                .success(new NormalCallback<User>() {
                    @Override
                    public void success(User user) {
                        Role role = new Role();
                        role.setAvatar(user.mediumAvatar);
                        role.setNickname(user.nickname);
                        role.setRid(user.id);
                        role.setType(Destination.USER);
                        callback.success(role);
                    }
                });
    }

    public void updateRoleByUser(User user) {
        IMRoleManager roleManager = IMClient.getClient().getRoleManager();
        Role role = roleManager.getRole(Destination.USER, user.id);
        role.setAvatar(user.mediumAvatar);
        role.setNickname(user.nickname);
        role.setType(Destination.USER);

        if (role.getRid() == 0) {
            role.setRid(user.id);
            roleManager.createRole(role);
            return;
        }
        roleManager.updateRole(role);
    }

    public void updateRolesByCourse(String type, List<Course> courses) {
        IMRoleManager roleManager = IMClient.getClient().getRoleManager();
        Map<Integer, Role> roleMap = roleManager.getRoleMap(getIntArrayFromListByCourse(courses));
        for (Course course : courses) {
            Role role = new Role();
            role.setRid(course.id);
            role.setAvatar(course.middlePicture);
            role.setNickname(course.title);
            role.setType(type);
            if (roleMap.containsKey(course.id)) {
                roleManager.updateRole(role);
                continue;
            }

            roleManager.createRole(role);
        }
    }

    public <T extends Friend> void updateRoles(List<T> friends) {
        IMRoleManager roleManager = IMClient.getClient().getRoleManager();
        for (Friend friend : friends) {
            Role role = roleManager.getRole(friend.getType(), friend.getId());

            role.setAvatar(friend.getMediumAvatar());
            role.setNickname(friend.getNickname());
            if (role.getRid() != 0) {
                roleManager.updateRole(role);
                continue;
            }
            role.setRid(friend.getId());
            role.setType(friend.getType());
            roleManager.createRole(role);
        }
    }

    private int[] getIntArrayFromListByCourse(List<Course> courses) {
        int[] array = new int[courses.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = courses.get(i).id;
        }

        return array;
    }

    private <T extends Friend> int[] getIntArrayFromList(List<T> friends) {
        int[] array = new int[friends.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = friends.get(i).id;
        }

        return array;
    }

    private ConvEntity createConvNo(int uid, String convNo, Role role) {
        ConvEntity convEntity = new ConvEntity();
        convEntity.setTargetId(role.getRid());
        convEntity.setTargetName(role.getNickname());
        convEntity.setConvNo(convNo);
        convEntity.setType(role.getType());
        convEntity.setAvatar(role.getAvatar());
        convEntity.setUid(uid);
        convEntity.setCreatedTime(System.currentTimeMillis());
        convEntity.setUpdatedTime(0);
        IMClient.getClient().getConvManager().createConv(convEntity);

        return convEntity;
    }

    public void syncIMRoleData() {
        syncFriendList().then(new PromiseCallback<List<? extends Friend>>() {
            @Override
            public Promise invoke(List<? extends Friend> friends) {
                if (friends != null) {
                    updateRoles(friends);
                }
                return syncGroupList().then(new PromiseCallback<List<? extends Friend>>() {
                    @Override
                    public Promise invoke(List<? extends Friend> friends) {
                        if (friends != null) {
                            updateRoles(friends);
                        }
                        return syncServiceList().then(new PromiseCallback<List<? extends Friend>>() {
                            @Override
                            public Promise invoke(List<? extends Friend> friends) {
                                if (friends != null) {
                                    updateRoles(friends);
                                }
                                return null;
                            }
                        });
                    }
                });
            }
        });
    }

    private Promise syncFriendList() {
        final Promise promise = new Promise();
        new FriendProvider(mContext).getFriendList()
                .success(new NormalCallback<FriendResult>() {
                    @Override
                    public void success(FriendResult friendResult) {
                        Log.d(TAG, "sync friends");
                        promise.resolve(Arrays.asList(friendResult.data));
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                promise.resolve(null);
            }
        });

        return promise;
    }

    private Promise syncGroupList() {
        final Promise promise = new Promise();
        new FriendProvider(mContext).getSchoolApps()
                .success(new NormalCallback<List<SchoolApp>>() {
                    @Override
                    public void success(List<SchoolApp> schoolApps) {
                        Log.d(TAG, "sync group");
                        promise.resolve(schoolApps);
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                promise.resolve(null);
            }
        });
        return promise;
    }

    private Promise syncServiceList() {
        final Promise promise = new Promise();
        new DiscussionGroupProvider(mContext).getGroupList()
                .success(new NormalCallback<DiscussionGroupResult>() {
                    @Override
                    public void success(DiscussionGroupResult discussionGroupResult) {
                        Log.d(TAG, "sync service");
                        promise.resolve(Arrays.asList(discussionGroupResult.resources));
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                promise.resolve(null);
            }
        });
        return promise;
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
