package net.center.upload_plugin;


import com.android.build.gradle.api.BaseVariant;
import com.android.build.gradle.api.BaseVariantOutput;
import com.google.gson.Gson;

import net.center.upload_plugin.model.DDContentModel;
import net.center.upload_plugin.model.FeiShuRequestBean;
import net.center.upload_plugin.model.PgyUploadResult;
import net.center.upload_plugin.params.SendDingParams;
import net.center.upload_plugin.params.SendFeishuParams;
import net.center.upload_plugin.model.UploadPgyParams;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import groovy.json.JsonOutput;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
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

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();
    }

    private void upload(String apiKey, String appName, int installType, String buildPassword, String buildUpdateDescription, int buildInstallDate, String buildChannelShortcut, File apkFile) {
        //builder
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        bodyBuilder.addFormDataPart("_api_key", apiKey);
        if (!PluginUtils.isEmpty(buildUpdateDescription))
            bodyBuilder.addFormDataPart("buildUpdateDescription", buildUpdateDescription);
        if (installType != 1)
            bodyBuilder.addFormDataPart("buildInstallType", installType + "");
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
            Response response = getOkHttpClient().newCall(request).execute();
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
                        sendMsgToDingDing(uploadResult.getData());
                        sendMsgToFeishu(uploadResult.getData());
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

    private void sendMsgToDingDing(PgyUploadResult.DataDTO dataDTO) {
        SendDingParams dingParams = SendDingParams.getDingParamsConfig(mTargetProject);
        if (PluginUtils.isEmpty(dingParams.accessToken)) {
            System.out.println("send to Dingding failure：accessToken is empty");
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("msgtype", "link");
        DDContentModel contentModel = new DDContentModel();
        String text = dingParams.contentText;
        if (PluginUtils.isEmpty(text)) {
            text = "最新开发测试包已上传 ";
        }
        contentModel.setText(text + dataDTO.getBuildCreated());
        String title = dingParams.contentTitle;
        if (PluginUtils.isEmpty(title)) {
            title = "测试包版本：";
        }
        contentModel.setTitle(title + "V" + dataDTO.getBuildVersion());
        contentModel.setPicUrl(dataDTO.getBuildQRCodeURL());
        contentModel.setMessageUrl("https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
        map.put("link", contentModel);
        String json = JsonOutput.toJson(map);
        System.out.println("send to Dingding request json：" + json);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json);
        Request request = new Request.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Charset", "UTF-8")
                .url("https://oapi.dingtalk.com/robot/send?access_token=" + dingParams.accessToken)
                .post(requestBody)
                .build();
        try {
            Response response = getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                System.out.println("send to Dingding result：" + result);
            } else {
                System.out.println("send to Dingding failure");
            }
            System.out.println("*************** sendMsgToDing finish ***************");
        } catch (Exception e) {
            System.out.println("send to Dingding failure " + e);
        }
    }

    private void sendMsgToFeishu(PgyUploadResult.DataDTO dataDTO) {
        SendFeishuParams feishuParamsConfig = SendFeishuParams.getFeishuParamsConfig(mTargetProject);
        String webHookHostUrl = feishuParamsConfig.webHookHostUrl;
        if (PluginUtils.isEmpty(webHookHostUrl)) {
            System.out.println("send to feishu failure：webHookHostUrl is empty");
            return;
        }
        FeiShuRequestBean feiShuRequestBean = new FeiShuRequestBean();
        feiShuRequestBean.setMsgType("post");
        FeiShuRequestBean.ContentDTO contentDTO = new FeiShuRequestBean.ContentDTO();
        FeiShuRequestBean.ContentDTO.PostDTO postDTO = new FeiShuRequestBean.ContentDTO.PostDTO();


        FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean contentBeanText = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean();
        contentBeanText.setTag("text");
        String text = feishuParamsConfig.contentText;
        if (PluginUtils.isEmpty(text)) {
            text = "最新开发测试包已上传 ";
        }
        contentBeanText.setText(text + dataDTO.getBuildCreated());

        FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean contentBeanA = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean();
        contentBeanA.setTag("a");
        contentBeanA.setText(PluginUtils.isEmpty(feishuParamsConfig.clickTxt) ? "点击进行下载" : feishuParamsConfig.clickTxt);
        contentBeanA.setHref("https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
        List<FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean> beans = new ArrayList<>();
        beans.add(contentBeanText);
        beans.add(contentBeanA);
        List<List<FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean>> zhCnContentList = new ArrayList<>();
        zhCnContentList.add(beans);

        FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO zhCnDTO = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO();
        String title = feishuParamsConfig.contentTitle;
        if (PluginUtils.isEmpty(title)) {
            title = "测试包版本：";
        }
        zhCnDTO.setTitle(title + "V" + dataDTO.getBuildVersion());
        zhCnDTO.setContent(zhCnContentList);
        postDTO.setZhCn(zhCnDTO);
        contentDTO.setPost(postDTO);
        feiShuRequestBean.setContent(contentDTO);
        /**
         * text	文本	
         * post	富文本	发送富文本消息
         * image	图片	上传图片
         * share_chat	分享群名片	群名片
         * interactive	消息卡片	消息卡片消息
         */
        String json = JsonOutput.toJson(feiShuRequestBean);
        System.out.println("send to feishu request json：" + json);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json);
        Request request = new Request.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Charset", "UTF-8")
                .url(webHookHostUrl)
                .post(requestBody)
                .build();
        try {
            Response response = getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                System.out.println("send to feishu result：" + result);
            } else {
                System.out.println("send to feishu failure");
            }
            System.out.println("*************** sendMsgToFeishu finish ***************");
        } catch (Exception e) {
            System.out.println("send to feishu failure " + e);
        }
    }
}
