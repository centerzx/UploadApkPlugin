package net.center.upload_plugin.model;

/**
 * Created by center
 * 2021-09-04.
 * 
 * 发送钉钉消息的内容
 */
public class DDContentModel {

    private String text;
    private String title;
    private String picUrl;
    private String messageUrl;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }
}
