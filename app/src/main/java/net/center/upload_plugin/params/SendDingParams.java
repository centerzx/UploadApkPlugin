package net.center.upload_plugin.params;

import org.gradle.api.Project;

/**
 * Created by Android-ZX
 * 2021/9/3.
 * 
 * 发送到钉钉的消息参数
 */
public class SendDingParams {

    public String accessToken;
    public String contentText;
    public String contentTitle;

    public SendDingParams() {

    }

    public SendDingParams(String accessToken) {
        this(accessToken, "","测试包版本：");
    }

    public SendDingParams(String accessToken,String contentText, String contentTitle) {
        this.accessToken = accessToken;
        this.contentText = contentText;
        this.contentTitle = contentTitle;
    }

    public static SendDingParams getDingParamsConfig(Project project) {
        SendDingParams extension = project.getExtensions().findByType(SendDingParams.class);
        if (extension == null) {
            extension = new SendDingParams();
        }
        return extension;
    }

}
