package net.center.upload_plugin;


import com.android.build.gradle.api.BaseVariant;
import com.android.build.gradle.api.BaseVariantOutput;
import com.google.gson.Gson;

import net.center.upload_plugin.helper.CmdHelper;
import net.center.upload_plugin.helper.HttpHelper;
import net.center.upload_plugin.helper.SendMsgHelper;
import net.center.upload_plugin.model.PgyCOSTokenResult;
import net.center.upload_plugin.model.PgyUploadResult;
import net.center.upload_plugin.model.UploadPgyParams;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
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
//    private ScheduledExecutorService executorService;
//    private Timer mTimer;

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
//            uploadPgyAndSendMessage(params.apiKey, params.appName, params.buildInstallType
//                    , params.buildPassword, params.buildUpdateDescription
//                    , params.buildInstallDate, params.buildChannelShortcut, apk);
            uploadPgyQuickWay(params.apiKey, params.appName, params.buildInstallType
                    , params.buildPassword, params.buildUpdateDescription
                    , params.buildInstallDate, params.buildChannelShortcut, apk);
//            CmdHelper.getGitLogByTimeAndCount(-1, -1);
        }
    }

    /**
     * 本接口上传速度很慢，即将废弃，强烈建议您使用 快速上传App 中的方式来替代。
     * https://www.pgyer.com/doc/view/api#fastUploadApp
     *
     * @param apiKey
     * @param appName
     * @param installType
     * @param buildPassword
     * @param buildUpdateDescription
     * @param buildInstallDate
     * @param buildChannelShortcut
     * @param apkFile
     */
    private void uploadPgyAndSendMessage(String apiKey, String appName, int installType, String buildPassword, String buildUpdateDescription, int buildInstallDate, String buildChannelShortcut, File apkFile) {
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
        Request request = getRequestBuilder()
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
                        String gitLog = CmdHelper.checkGetGitParamsWithLog(mTargetProject);
                        SendMsgHelper.sendMsgToDingDing(mTargetProject, uploadResult.getData(), gitLog);
                        SendMsgHelper.sendMsgToFeishu(mTargetProject, uploadResult.getData(), gitLog);
                        SendMsgHelper.sendMsgToWeiXinGroup(mTargetProject, uploadResult.getData(), gitLog);
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

    /**
     * 快速上传方式 获取上传的 token
     *
     * @param apiKey
     * @param appName
     * @param installType
     * @param buildPassword
     * @param buildUpdateDescription
     * @param buildInstallDate
     * @param buildChannelShortcut
     * @param apkFile
     */
    private void uploadPgyQuickWay(String apiKey, String appName, int installType, String buildPassword, String buildUpdateDescription, int buildInstallDate, String buildChannelShortcut, File apkFile) {
        //builder
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        bodyBuilder.addFormDataPart("_api_key", apiKey);
        bodyBuilder.addFormDataPart("buildType", "android");
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
        System.out.println("upload pgy --- 快速上传方式接口：Start getCOSToken");
        Request request = getRequestBuilder()
                .url("https://www.pgyer.com/apiv2/app/getCOSToken")
                .post(bodyBuilder.build())
                .build();
        try {
            Response response = HttpHelper.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                System.out.println("upload pgy --- getCOSToken result: " + result);
                if (!PluginUtils.isEmpty(result)) {
                    PgyCOSTokenResult pgyCOSTokenResult = new Gson().fromJson(result, PgyCOSTokenResult.class);
                    if (pgyCOSTokenResult.getCode() != 0 || pgyCOSTokenResult.getData() == null) {
                        System.out.println("upload pgy --- getCOSToken result error msg: " + pgyCOSTokenResult.getMessage());
                        return;
                    }
                    uploadFileToPgy(pgyCOSTokenResult, apkFile, apiKey);
                }
            } else {
                System.out.println("upload pgy ---- request getCOSToken call failed");
            }
            System.out.println("******************* getCOSToken: finish *******************");
        } catch (Exception e) {
            System.out.println("upload pgy ---- request getCOSToken call failed " + e);
        }
    }

    /**
     * 上传文件到第上一步获取的 URL
     *
     * @param tokenResult
     * @param apkFile
     * @param apiKey
     */
    private void uploadFileToPgy(PgyCOSTokenResult tokenResult, File apkFile, String apiKey) {
        if (PluginUtils.isEmpty(tokenResult.getData().getEndpoint())) {
            System.out.println("upload pgy --- endpoint url is empty");
            return;
        }
        if (tokenResult.getData().getParams() == null) {
            System.out.println("upload pgy --- endpoint params is empty");
            return;
        }
        //builder
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        bodyBuilder.addFormDataPart("key", tokenResult.getData().getParams().getKey());
        bodyBuilder.addFormDataPart("signature", tokenResult.getData().getParams().getSignature());
        bodyBuilder.addFormDataPart("x-cos-security-token", tokenResult.getData().getParams().getXcossecuritytoken());
        //add file
        bodyBuilder.addFormDataPart("file", apkFile.getName(), RequestBody
                .create(MediaType.parse("*/*"), apkFile));
        System.out.println("upload pgy --- Start endpoint : " + tokenResult.getData().getEndpoint());
        Request request = getRequestBuilder()
                .url(tokenResult.getData().getEndpoint())
                .post(bodyBuilder.build())
                .build();
        try {
            Response response = HttpHelper.getOkHttpClient().newCall(request).execute();
            System.out.println("upload pgy --- endpoint: upload apkFile to pgy response: " + response.isSuccessful());
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                System.out.println("upload pgy --- endpoint: upload apkFile to pgy result: " + result);
//                if (!PluginUtils.isEmpty(result)) {
//                    BasePgyResult uploadResult = new Gson().fromJson(result, BasePgyResult.class);
//                    if (uploadResult.getCode() != 204) {
//                        System.out.println("endpoint: upload apkFile to pgy result error msg: " + uploadResult.getMessage());
//                        return;
//                    }
//                    checkPgyUploadBuildInfo(apiKey, tokenResult.getData().getKey());
//                }
                checkPgyUploadBuildInfo(apiKey, tokenResult.getData().getKey());
            } else {
                System.out.println("upload pgy --- endpoint: upload apkFile to pgy result failure");
            }
            System.out.println("******************* endpoint: finish *******************");
        } catch (Exception e) {
            System.out.println("upload pgy --- endpoint: upload apkFile to pgy result failure " + e);
        }
    }

    /**
     * 检测应用是否发布完成，并获取发布应用的信息
     *
     * @param apiKey
     * @param buildKey 发布成功失败返回数据
     *                 code	Integer	错误码，1216 应用发布失败
     *                 message	String	信息提示
     *                 <p>
     *                 正在发布返回数据
     *                 code	Integer	错误码，1246 应用正在发布中
     *                 message	String	信息提示
     *                 如果返回 code = 1246 ，可间隔 3s ~ 5s 重新调用 URL 进行检测，直到返回成功或失败。
     *                 <p>
     *                 buildInfo: upload pgy buildInfo result: {"code":1247,"message":"App is uploded, please wait"}
     */
    private void checkPgyUploadBuildInfo(String apiKey, String buildKey) {
//        Map<String, String> paramsMap = new HashMap<>(2);
//        paramsMap.put("_api_key", apiKey);
//        paramsMap.put("buildKey", buildKey);
//        String url = "https://www.pgyer.com/apiv2/app/buildInfo";
//        System.out.println("upload pgy --- Start buildInfo");
//        //request
//        Request request = getRequestBuilder("get", url, paramsMap)
//                .build();
        String url = "https://www.pgyer.com/apiv2/app/buildInfo?_api_key=" + apiKey + "&buildKey=" + buildKey;
        System.out.println("upload pgy --- Start buildInfo : " + url);
        Request request = getRequestBuilder()
                .url(url)
                .get()
                .build();
        try {
            Response response = HttpHelper.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                System.out.println("upload pgy --- buildInfo: upload pgy buildInfo result: " + result);
                if (!PluginUtils.isEmpty(result)) {
                    PgyUploadResult uploadResult = new Gson().fromJson(result, PgyUploadResult.class);
                    if (uploadResult.getCode() == 0) {
                        if (uploadResult.getData() != null) {
                            String apkDownUrl = "https://www.pgyer.com/" + uploadResult.getData().getBuildShortcutUrl();
                            System.out.println("上传成功，应用链接: " + apkDownUrl);
                            String gitLog = CmdHelper.checkGetGitParamsWithLog(mTargetProject);
                            SendMsgHelper.sendMsgToDingDing(mTargetProject, uploadResult.getData(), gitLog);
                            SendMsgHelper.sendMsgToFeishu(mTargetProject, uploadResult.getData(), gitLog);
                            SendMsgHelper.sendMsgToWeiXinGroup(mTargetProject, uploadResult.getData(), gitLog);
                        } else {
                            System.out.println("upload pgy --- buildInfo: upload pgy result error : data is empty");
                        }
                    } else if (uploadResult.getCode() == 1246 || uploadResult.getCode() == 1247) {
                        //正在发布返回数据
                        System.out.println("upload pgy --- buildInfo: upload pgy buildInfo code( " + uploadResult.getCode() + "):" + uploadResult.getMessage());
//                        checkPgyUploadBuildInfo(apiKey, buildKey);
                        pgyUploadBuildInfoTimer(apiKey, buildKey);
                    } else {
                        System.out.println("upload pgy --- buildInfo: upload pgy buildInfo result error msg: " + uploadResult.getMessage());
                    }
                }
            } else {
                System.out.println("upload pgy --- buildInfo: upload pgy buildInfo result failure");
            }
            System.out.println("******************* buildInfo: finish *******************");
        } catch (Exception e) {
            System.out.println("upload pgy --- buildInfo: upload pgy buildInfo result failure " + e);
        }
    }

    private void pgyUploadBuildInfoTimer(String apiKey, String buildKey) {
        System.out.println("upload pgy --- buildInfo: upload pgy buildInfo request again(pgyUploadBuildInfoTimer)");
//        if (executorService == null) {
//            executorService = new ScheduledThreadPoolExecutor(1);
//        }
//        executorService.scheduleWithFixedDelay(() -> {
//            try {
//                checkPgyUploadBuildInfo(apiKey, buildKey);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }, 0, 3, TimeUnit.SECONDS);

//        if (mTimer == null) {
//            mTimer = new Timer();
//        }
//        mTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    System.out.println("buildInfo: upload pgy buildInfo request again");
//                    checkPgyUploadBuildInfo(apiKey, buildKey);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 3000);
        try {
            TimeUnit.SECONDS.sleep(3);
//            Thread.sleep(3000);
            checkPgyUploadBuildInfo(apiKey, buildKey);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Request.Builder getRequestBuilder() {
        return new Request.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Charset", "UTF-8");
    }


    private Request.Builder getRequestBuilder(String method, String url, Map<String, String> params) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            return new Request.Builder();
        }
        HttpUrl.Builder httpBuilder = httpUrl.newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        return new Request.Builder()
                .url(httpBuilder.build())
                .method(method, new FormBody.Builder().build())
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Charset", "UTF-8");
    }
}