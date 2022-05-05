package net.center.upload_plugin;


import com.android.build.gradle.api.BaseVariant;
import com.android.build.gradle.api.BaseVariantOutput;
import com.google.gson.Gson;

import net.center.upload_plugin.helper.HttpHelper;
import net.center.upload_plugin.helper.SendMsgHelper;
import net.center.upload_plugin.model.PgyUploadResult;
import net.center.upload_plugin.model.UploadPgyParams;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Android-ZX
 * 2021/9/3.
 */
public class UploadTask extends DefaultTask {

    private BaseVariant mVariant;
    private Project mTargetProject;

    public void init(BaseVariant variant, Project project) {
        this.mVariant = variant;
        this.mTargetProject = project;
        setDescription(PluginConstants.TASK_DES);
        setGroup(PluginConstants.TASK_GROUP_NAME);
    }

    @TaskAction
    public void uploadToPGY() {
        for (BaseVariantOutput output : mVariant.getOutputs()) {
            File apkDir = output.getOutputFile();
            if (apkDir == null || !apkDir.exists()) {
                throw new GradleException("apkDir OutputFile is not exist!");
            }
            System.out.println("apkDir path: " + apkDir.getAbsolutePath());
            File apk = null;
            if (apkDir.getName().endsWith(".apk")) {
                apk = apkDir;
            } else {
                if (apkDir.listFiles() != null) {
                    for (int i = Objects.requireNonNull(apkDir.listFiles()).length - 1; i >= 0; i--) {
                        File apkFile = Objects.requireNonNull(apkDir.listFiles())[i];
                        if (apkFile != null && apkFile.exists() && apkFile.getName().endsWith(".apk")) {
                            apk = apkFile;
                            break;
                        }
                    }
                }
            }
            if (apk == null || !apk.exists()) {
                throw new GradleException("apk file is not exist!");
            }
            System.out.println("final upload apk path: " + apk.getAbsolutePath());
            UploadPgyParams params = UploadPgyParams.getConfig(mTargetProject);
            upload(params.apiKey, params.appName, params.buildInstallType
                    , params.buildPassword, params.buildUpdateDescription
                    , params.buildInstallDate, params.buildChannelShortcut, apk);
        }
    }


    private void upload(String apiKey, String appName, int installType, String buildPassword, String buildUpdateDescription, int buildInstallDate, String buildChannelShortcut, File apkFile) {
        //builder
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        bodyBuilder.addFormDataPart("_api_key", apiKey);
        if (!PluginUtils.isEmpty(buildUpdateDescription)) {
            bodyBuilder.addFormDataPart("buildUpdateDescription", buildUpdateDescription);
        }
        if (installType != 1) {
            bodyBuilder.addFormDataPart("buildInstallType", installType + "");
        }
        if (installType == 2 && !PluginUtils.isEmpty(buildPassword)) {
            bodyBuilder.addFormDataPart("buildPassword", buildPassword);
        }
        bodyBuilder.addFormDataPart("buildInstallDate", buildInstallDate + "");
        if (!PluginUtils.isEmpty(buildChannelShortcut)) {
            bodyBuilder.addFormDataPart("buildChannelShortcut", buildChannelShortcut);
        }
        //add file
        bodyBuilder.addFormDataPart("file", apkFile.getName(), RequestBody
                .create(MediaType.parse("*/*"), apkFile));
        //request
        Request request = new Request.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Charset", "UTF-8")
                .url("https://www.pgyer.com/apiv2/app/upload")
                .post(bodyBuilder.build())
                .build();
        try {
            Response response = HttpHelper.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                System.out.println("upload pgy result: " + result);
                if (!PluginUtils.isEmpty(result)) {
                    PgyUploadResult uploadResult = new Gson().fromJson(result, PgyUploadResult.class);
                    if (uploadResult.getCode() != 0) {
                        System.out.println("upload pgy result error msg: " + uploadResult.getMessage());
                        return;
                    }
                    if (uploadResult.getData() != null) {
                        String url = "https://www.pgyer.com/" + uploadResult.getData().getBuildShortcutUrl();
                        System.out.println("上传成功，应用链接: " + url);
                        SendMsgHelper.sendMsgToDingDing(mTargetProject, uploadResult.getData());
                        SendMsgHelper.sendMsgToFeishu(mTargetProject, uploadResult.getData());
                        SendMsgHelper.sendMsgToWeiXinGroup(mTargetProject, uploadResult.getData());
                    } else {
                        System.out.println("upload pgy result error : data is empty");
                    }
                }
            } else {
                System.out.println("upload pgy failure");
            }
            System.out.println("******************* upload finish *******************");
        } catch (Exception e) {
            System.out.println("upload pgy failure " + e);
        }
    }

}
