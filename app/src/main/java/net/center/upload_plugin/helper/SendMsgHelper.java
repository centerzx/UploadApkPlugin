package net.center.upload_plugin.helper;

import net.center.upload_plugin.PluginUtils;
import net.center.upload_plugin.model.DDContentModel;
import net.center.upload_plugin.model.FeiShuRequestBean;
import net.center.upload_plugin.model.PgyUploadResult;
import net.center.upload_plugin.model.WXGroupRequestBean;
import net.center.upload_plugin.params.SendDingParams;
import net.center.upload_plugin.params.SendFeishuParams;
import net.center.upload_plugin.params.SendWeixinGroupParams;

import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import groovy.json.JsonOutput;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMsgHelper {

    public static void sendMsgToDingDing(Project project, PgyUploadResult.DataDTO dataDTO) {
        SendDingParams dingParams = SendDingParams.getDingParamsConfig(project);
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
            Response response = HttpHelper.getOkHttpClient().newCall(request).execute();
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

    public static void sendMsgToFeishu(Project project, PgyUploadResult.DataDTO dataDTO) {
        SendFeishuParams feishuParamsConfig = SendFeishuParams.getFeishuParamsConfig(project);
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
            Response response = HttpHelper.getOkHttpClient().newCall(request).execute();
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

    public static void sendMsgToWeiXinGroup(Project project, PgyUploadResult.DataDTO dataDTO) {
        SendWeixinGroupParams weixinGroupParamsConfig = SendWeixinGroupParams.getWeixinGroupConfig(project);
        String webHookUrl = weixinGroupParamsConfig.webHookUrl;
        if (PluginUtils.isEmpty(webHookUrl)) {
            System.out.println("send to weixin group failure：webHookUrl is empty");
            return;
        }
        String contentTitle = weixinGroupParamsConfig.contentTitle;
        String contentText = weixinGroupParamsConfig.contentText;

        WXGroupRequestBean wxGroupRequestBean = new WXGroupRequestBean();
        if ("markdown".equals(weixinGroupParamsConfig.msgtype)) {
            wxGroupRequestBean.setMsgtype("markdown");
            WXGroupRequestBean.MarkdownDTO markdownDTO = new WXGroupRequestBean.MarkdownDTO();
            String markStr = "**" + dataDTO.getBuildName() + "** V" + dataDTO.getBuildVersion() + "\n" + contentTitle + "\n" + contentText + "\n" + "<font color=\"info\">[下载链接，点击下载](https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl() + ")</font>";
            markdownDTO.setContent(markStr);
            wxGroupRequestBean.setMarkdown(markdownDTO);
        } else if ("news".equals(weixinGroupParamsConfig.msgtype)) {
            wxGroupRequestBean.setMsgtype("news");
            WXGroupRequestBean.NewsDTO newsDTO = new WXGroupRequestBean.NewsDTO();
            WXGroupRequestBean.NewsDTO.ArticlesDTO articlesDTO = new WXGroupRequestBean.NewsDTO.ArticlesDTO();
            articlesDTO.setTitle(dataDTO.getBuildName() + " V" + dataDTO.getBuildVersion());
            if (!PluginUtils.isEmpty(contentTitle) && !PluginUtils.isEmpty(contentTitle)) {
                articlesDTO.setDescription(contentTitle + "—" + contentText + " " + dataDTO.getBuildCreated());
            } else if (!PluginUtils.isEmpty(contentTitle)) {
                articlesDTO.setDescription(contentTitle + " " + dataDTO.getBuildCreated());
            } else if (!PluginUtils.isEmpty(contentText)) {
                articlesDTO.setDescription(contentText + " " + dataDTO.getBuildCreated());
            } else {
                articlesDTO.setDescription("最新开发测试包已上传,请下载测试吧！ " + dataDTO.getBuildCreated());
            }
            articlesDTO.setUrl("https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
            articlesDTO.setPicurl(dataDTO.getBuildQRCodeURL());
            List<WXGroupRequestBean.NewsDTO.ArticlesDTO> articlesDTOList = new ArrayList<>();
            articlesDTOList.add(articlesDTO);
            newsDTO.setArticles(articlesDTOList);
            wxGroupRequestBean.setNews(newsDTO);
        } else {
            wxGroupRequestBean.setMsgtype("text");
            WXGroupRequestBean.TextDTO textDTO = new WXGroupRequestBean.TextDTO();
            if (!PluginUtils.isEmpty(contentTitle) && !PluginUtils.isEmpty(contentTitle)) {
                textDTO.setContent(contentTitle + "，" + contentText);
            } else if (!PluginUtils.isEmpty(contentTitle)) {
                textDTO.setContent(contentTitle);
            } else if (!PluginUtils.isEmpty(contentText)) {
                textDTO.setContent(contentText);
            } else {
                textDTO.setContent(dataDTO.getBuildName() + "\n最新开发测试包已上传,请下载测试吧！");
            }
            textDTO.setContent(textDTO.getContent() + "下载链接：https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
            if (weixinGroupParamsConfig.isAtAll) {
                List<String> mentionedList = new ArrayList<String>();
                mentionedList.add("@all");
                textDTO.setMentionedList(mentionedList);
                textDTO.setMentionedMobileList(mentionedList);
            }
            wxGroupRequestBean.setText(textDTO);
        }
        String json = JsonOutput.toJson(wxGroupRequestBean);
        System.out.println("send to WeiXin group request json：" + json);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json);
        Request request = new Request.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Charset", "UTF-8")
                .url(webHookUrl)
                .post(requestBody)
                .build();
        try {
            Response response = HttpHelper.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                System.out.println("send to WeiXin group result：" + result);
            } else {
                System.out.println("send to WeiXin group failure");
            }
            System.out.println("*************** sendMsgToWeiXinGroup finish ***************");
        } catch (Exception e) {
            System.out.println("send to WeiXin group failure " + e);
        }

    }

}
