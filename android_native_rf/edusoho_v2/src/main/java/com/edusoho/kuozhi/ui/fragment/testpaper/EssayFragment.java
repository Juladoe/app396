package com.edusoho.kuozhi.ui.fragment.testpaper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.testpaper.QuestionAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-24.
 */
public class EssayFragment extends SelectQuestionFragment{

    private QuestionType type = QuestionType.essay;
    public static final int PHOTO = 0001;
    public static final int CAMERA = 0002;

    /**
     * 从手机图库中选择图片返回结果表示
     */
    private static final int IMAGE_RESULT = 1;

    private static final int CAMERA_RESULT = 2;

    private File mCameraImageFile;
    private int mCameraIndex;

    private NormalCallback mEssayQWCallback;

    @Override
    public String getTitle() {
        return "问答题";
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        int type = message.type.code;
        mEssayQWCallback = message.callback;
        switch (type) {
            case PHOTO:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_RESULT);
                break;
            case CAMERA:
                camera();
        }
    }

    private void uploadImage(int type, String path, final NormalCallback<String> callback)
    {
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.setMessage("上传中...");
        loadDialog.show();

        File imageFile = null;
        if (type == PHOTO) {
            imageFile = new File(path);
            if (imageFile.exists()) {
                Log.d(null, "compress imageFile->" + imageFile);
                imageFile = compressImage(imageFile);
            }
        } else {
            imageFile = compressImage(new File(path));
        }
        RequestUrl requestUrl = app.bindUrl(Const.UPLOAD_IMAGE, true);
        requestUrl.setMuiltParams(new Object[] {
                "file", imageFile
        });

        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                loadDialog.dismiss();
                String result = mActivity.parseJsonValue(
                        object, new TypeToken<String>(){});

                Log.d(null, "upload result->" + result);
                if (result == null || TextUtils.isEmpty(result)) {
                    mActivity.longToast("上传失败!服务器暂不支持过大图片");
                }
                callback.success(String.format("<img src='%s'/>", result));
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                mActivity.longToast("上传失败!服务器暂不支持过大图片");
                loadDialog.dismiss();
            }
        });

    }

    private void camera()
    {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
        }
        File saveDir = AQUtility.getCacheDir(mContext);
        mCameraImageFile = new File(saveDir, "caremaImage" + mCameraIndex + ".jpg");
        mCameraIndex++;
        if (!mCameraImageFile.exists()) {
            try {
                mCameraImageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "照片创建失败!", Toast.LENGTH_LONG).show();
                return;
            }
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraImageFile));
        startActivityForResult(intent, CAMERA_RESULT);
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(PHOTO, source),
                new MessageType(CAMERA, source),
                new MessageType(Const.TESTPAPER_REFRESH_DATA)
        };
        return messageTypes;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.choice_fragment_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        refreshViewData();
    }

    @Override
    protected void refreshViewData() {
        ArrayList<QuestionTypeSeq> questionTypeSeqs = getQuestion(type);
        if (questionTypeSeqs == null) {
            return;
        }

        mQuestionCount = questionTypeSeqs.size();
        setQuestionTitle(type.title(), questionTypeSeqs);
        setQuestionNumber(mCurrentIndex);

        QuestionAdapter adapter = new QuestionAdapter(
                mContext, questionTypeSeqs);
        mQuestionPager.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IMAGE_RESULT:
                if (null != data) {
                    final String filePath = convertUriToPath(data.getData());
                    final Bundle bundle = new Bundle();
                    Log.d(null, "inser->" + filePath);
                    bundle.putString("file", filePath);
                    uploadImage(PHOTO, filePath, new NormalCallback<String>() {
                        @Override
                        public void success(String obj) {
                            if (mEssayQWCallback != null) {
                                bundle.putString("image", obj);
                                mEssayQWCallback.success(bundle);
                            }
                        }
                    });
                }
                break;
            case CAMERA_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    if (mCameraImageFile != null & mCameraImageFile.exists()) {
                        final Bundle bundle = new Bundle();
                        bundle.putString("file", mCameraImageFile.getPath());
                        uploadImage(CAMERA, mCameraImageFile.getPath(), new NormalCallback<String>() {
                            @Override
                            public void success(String obj) {
                                if (mEssayQWCallback != null) {
                                    bundle.putString("image", obj);
                                    mEssayQWCallback.success(bundle);
                                }
                            }
                        });
                    }
                }
                break;
        }
    }

    private File compressImage(File file)
    {
        Bitmap bitmap = null;
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), option);
        option.inSampleSize = AppUtil.computeSampleSize(option, -1, EdusohoApp.screenW * EdusohoApp.screenH);
        option.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), option);
        File cacheDir = AQUtility.getCacheDir(mContext);
        File targetFile = new File(cacheDir, file.getName());

        try{
            if (targetFile == null || !targetFile.exists()) {
                targetFile.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);
            bos.flush();
            bos.close();
        }catch (Exception e){
            e.printStackTrace();
            targetFile = file;
        }

        Log.d(null, "compress file->" + targetFile);
        return targetFile;
    }

    private String convertUriToPath(Uri uri) {
        return AppUtil.getPath(mContext, uri);
    }

}
