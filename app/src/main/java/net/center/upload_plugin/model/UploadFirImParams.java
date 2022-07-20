package net.center.upload_plugin.model;

import org.gradle.api.Project;

/**
 * Created by Android-ZX
 * <p>
 * fir.im上传参数设置
 */
public class UploadFirImParams {

    //App 的 bundleId（发布新应用时必填）
    public String bundleId;
    //长度为 32, 用户在 fir 的 api_token
    public String apiToken;
    //应用名称（上传 ICON 时不需要）
    public String appName;
    //版本号（上传 ICON 时不需要）
    public String appVersion;
    //Build 号（上传 ICON 时不需要）
    public String buildCode;
    //更新日志（上传 ICON 时不需要）
    public String changelogText;

    public UploadFirImParams() {

    }

    public UploadFirImParams(String bundleId, String apiToken, String appName, String appVersion, String buildCode, String changelogText) {
        this.bundleId = bundleId;
        this.apiToken = apiToken;
        this.appName = appName;
        this.appVersion = appVersion;
        this.buildCode = buildCode;
        this.changelogText = changelogText;
    }

    public static UploadFirImParams getConfig(Project project) {
        UploadFirImParams extension = project.getExtensions().findByType(UploadFirImParams.class);
        if (extension == null) {
            extension = new UploadFirImParams();
        }
        return extension;
    }

}
