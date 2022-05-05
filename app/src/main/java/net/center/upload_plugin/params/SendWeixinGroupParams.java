package net.center.upload_plugin.params;

import org.gradle.api.Project;

/**
 * Created by Android-ZX
 * <p>
 * 发送到企业微信群的消息参数
 */
public class SendWeixinGroupParams {

    public String webHookUrl;
    public String msgtype = "text";
    /**
     * 如果使用文本可添加参数是否@全体群人员，默认true：isAtAll = true。其他类型不支持
     */
    public boolean isAtAll = true;
    public String contentTitle;
    public String contentText;

    public SendWeixinGroupParams() {

    }

    public SendWeixinGroupParams(String webHookUrl, String msgtype, boolean isAtAll, String contentTitle, String contentText) {
        this.webHookUrl = webHookUrl;
        this.msgtype = msgtype;
        this.isAtAll = isAtAll;
        this.contentText = contentText;
        this.contentTitle = contentTitle;
    }

    public static SendWeixinGroupParams getWeixinGroupConfig(Project project) {
        SendWeixinGroupParams extension = project.getExtensions().findByType(SendWeixinGroupParams.class);
        if (extension == null) {
            extension = new SendWeixinGroupParams();
        }
        return extension;
    }

}
