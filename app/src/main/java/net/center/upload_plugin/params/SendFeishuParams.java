package net.center.upload_plugin.params;

import org.gradle.api.Project;

/**
 * Created by Android-ZX
 * 2021/9/3.
 * 
 * 发送到飞书的消息参数
 */
public class SendFeishuParams {

    public String webHookHostUrl;
    public String contentTitle;
    public String contentText;
    public String clickTxt;
    
    public SendFeishuParams() {

    }

    public SendFeishuParams(String webHookHostUrl) {
        this(webHookHostUrl, "","","");
    }

    public SendFeishuParams(String webHookHostUrl,String contentTitle, String contentText, String clickTxt) {
        this.webHookHostUrl = webHookHostUrl;
        this.contentText = contentText;
        this.contentTitle = contentTitle;
        this.clickTxt=clickTxt;
    }

    public static SendFeishuParams getFeishuParamsConfig(Project project) {
        SendFeishuParams extension = project.getExtensions().findByType(SendFeishuParams.class);
        if (extension == null) {
            extension = new SendFeishuParams();
        }
        return extension;
    }

}
