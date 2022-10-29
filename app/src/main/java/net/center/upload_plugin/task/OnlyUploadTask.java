package net.center.upload_plugin.task;


import net.center.upload_plugin.PluginUtils;
import net.center.upload_plugin.params.UploadPgyParams;

import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Objects;

/**
 * Created by Android-ZX
 * 2021/9/3.
 */
public class OnlyUploadTask extends BaseTask {

    @TaskAction
    public void uploadToPGY() {
        UploadPgyParams params = UploadPgyParams.getConfig(mTargetProject);
        if (PluginUtils.isEmpty(params.uploadApkFilePath)) {
            System.out.println("The configured APK upload path (uploadApkFilePath) is empty");
            throw new GradleException("The configured APK upload path (uploadApkFilePath) is empty!");
//            return;
        }
        File apkDir = new File(params.uploadApkFilePath);
        if (!apkDir.exists()) {
            throw new GradleException("The configured APK upload file does not exist!");
        }
        System.out.println("ApkDir path: " + apkDir.getAbsolutePath());
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
            throw new GradleException("The configured APK upload file does not exist!");
        }
        System.out.println("Final upload apk path: " + apk.getAbsolutePath());
//            uploadPgyAndSendMessage(params.apiKey, params.appName, params.buildInstallType
//                    , params.buildPassword, params.buildUpdateDescription
//                    , params.buildInstallDate, params.buildChannelShortcut, apk);
        uploadPgyQuickWay(params.apiKey, params.appName, params.buildInstallType
                , params.buildPassword, params.buildUpdateDescription
                , params.buildInstallDate, params.buildChannelShortcut, apk);
    }
}