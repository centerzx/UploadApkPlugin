package net.center.upload_plugin.helper;

import net.center.upload_plugin.PluginUtils;
import net.center.upload_plugin.model.DingDingRequestBean;
import net.center.upload_plugin.model.FeiShuRequestBean;
import net.center.upload_plugin.model.PgyUploadResult;
import net.center.upload_plugin.model.WXGroupRequestBean;
import net.center.upload_plugin.params.SendDingParams;
import net.center.upload_plugin.params.SendFeishuParams;
import net.center.upload_plugin.params.SendWeixinGroupParams;

import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

import groovy.json.JsonOutput;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMsgHelper {

    /**
     * 发送消息到钉钉
     *
     * @param project
     * @param dataDTO
     */
    public static void sendMsgToDingDing(Project project, PgyUploadResult.DataDTO dataDTO) {
        SendDingParams dingParams = SendDingParams.getDingParamsConfig(project);
        if (PluginUtils.isEmpty(dingParams.accessToken)) {
            System.out.println("send to Dingding failure：accessToken is empty");
            return;
        }
        DingDingRequestBean requestBean = new DingDingRequestBean();
        String title = dingParams.contentTitle;
        if (PluginUtils.isEmpty(title)) {
            title = "测试包版本：";
        }
        StringBuilder titleStr = new StringBuilder(title).append("V").append(dataDTO.getBuildVersion());
        String text = dingParams.contentText;
        if (PluginUtils.isEmpty(text)) {
            text = "最新开发测试包已上传 ";
        }
        if ("markdown".equals(dingParams.msgtype)) {
            requestBean.setMsgtype("markdown");
            DingDingRequestBean.MarkDownDTO markDownBean = new DingDingRequestBean.MarkDownDTO();
            markDownBean.setTitle(titleStr.toString());
            StringBuilder contentStr = new StringBuilder("**").append(text).append("**");
            contentStr.append("*").append(dataDTO.getBuildCreated()).append("*").append("\n");
            String clickTxt = dingParams.clickTxt;
            if (PluginUtils.isEmpty(clickTxt)) {
                clickTxt = "点我进行下载";
            }
            contentStr.append("[").append(clickTxt).append("](https://www.pgyer.com/").append(dataDTO.getBuildShortcutUrl()).append(")\n");
            contentStr.append("![二维码](").append(dataDTO.getBuildQRCodeURL()).append(")\n");
            String gitLog = CmdHelper.getGitLogByTimeAndCount(-1, -1);
            if (!PluginUtils.isEmpty(gitLog)) {
                contentStr.append("### 更新内容：\n ").append(gitLog);
            }
            markDownBean.setText(contentStr.toString());
            DingDingRequestBean.AtDTO atBean = new DingDingRequestBean.AtDTO();
            atBean.setIsAtAll(dingParams.isAtAll);
            requestBean.setAt(atBean);
            requestBean.setMarkdown(markDownBean);
        } else if ("actionCard".equals(dingParams.msgtype)) {
            requestBean.setMsgtype("actionCard");
            DingDingRequestBean.ActionCardDTO actionCardBean = new DingDingRequestBean.ActionCardDTO();
            actionCardBean.setTitle(titleStr.toString());
            StringBuilder contentStr = new StringBuilder("**").append(text).append("**");
            contentStr.append("*").append(dataDTO.getBuildCreated()).append("*").append("\n");
            contentStr.append("![二维码](").append(dataDTO.getBuildQRCodeURL()).append(")\n");
            String gitLog = CmdHelper.getGitLogByTimeAndCount(-1, -1);
            if (!PluginUtils.isEmpty(gitLog)) {
                contentStr.append("### 更新内容：\n ").append(gitLog);
            }
            actionCardBean.setText(contentStr.toString());
            //0：按钮竖直排列 1：按钮横向排列
            actionCardBean.setBtnOrientation("0");
            String clickTxt = dingParams.clickTxt;
            if (PluginUtils.isEmpty(clickTxt)) {
                clickTxt = "点我进行下载";
            }
            actionCardBean.setSingleTitle(clickTxt);
            actionCardBean.setSingleURL("https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
            requestBean.setActionCard(actionCardBean);
        } else {
            requestBean.setMsgtype("link");
            DingDingRequestBean.LinkDTO linkBean = new DingDingRequestBean.LinkDTO();
            StringBuilder contentStr = new StringBuilder(text).append(dataDTO.getBuildCreated());
//            String gitLog = CmdHelper.getGitLogByTimeAndCount(-1, -1);
//            if (!PluginUtils.isEmpty(gitLog)) {
//                contentStr.append("\n 更新内容：\n ").append(gitLog);
//            }
            linkBean.setText(contentStr.toString());
            linkBean.setTitle(titleStr.toString());
            linkBean.setPicUrl(dataDTO.getBuildQRCodeURL());
            linkBean.setMessageUrl("https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
            requestBean.setLink(linkBean);
        }


        String json = JsonOutput.toJson(requestBean);
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

    /**
     * 发送消息到飞书
     *
     * @param project
     * @param dataDTO
     */
    public static void sendMsgToFeishu(Project project, PgyUploadResult.DataDTO dataDTO) {
        SendFeishuParams feishuParamsConfig = SendFeishuParams.getFeishuParamsConfig(project);
        String webHookHostUrl = feishuParamsConfig.webHookHostUrl;
        if (PluginUtils.isEmpty(webHookHostUrl)) {
            System.out.println("send to feishu failure：webHookHostUrl is empty");
            return;
        }
        String title = feishuParamsConfig.contentTitle;
        if (PluginUtils.isEmpty(title)) {
            title = "测试包版本：";
        }
        StringBuilder titleStr = new StringBuilder(title).append("V").append(dataDTO.getBuildVersion());
        String text = feishuParamsConfig.contentText;
        if (PluginUtils.isEmpty(text)) {
            text = "最新开发测试包已上传 ";
        }
        
        FeiShuRequestBean feiShuRequestBean = new FeiShuRequestBean();
        if ("interactive".equals(feishuParamsConfig.msgtype)) {
            feiShuRequestBean.setMsg_type("interactive");

        } else {
            feiShuRequestBean.setMsg_type("post");

        }

        FeiShuRequestBean.ContentDTO contentDTO = new FeiShuRequestBean.ContentDTO();
        FeiShuRequestBean.ContentDTO.PostDTO postDTO = new FeiShuRequestBean.ContentDTO.PostDTO();

        FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean contentBeanText = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean();
        contentBeanText.setTag("text");
        contentBeanText.setText(text + dataDTO.getBuildCreated());
        FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean contentBeanA = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean();
        contentBeanA.setTag("a");
        contentBeanA.setText(PluginUtils.isEmpty(feishuParamsConfig.clickTxt) ? "点击进行下载" : feishuParamsConfig.clickTxt);
        contentBeanA.setHref("https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
        List<FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean> contentBeans = new ArrayList<>();
        contentBeans.add(contentBeanText);
        contentBeans.add(contentBeanA);
        if (feishuParamsConfig.isAtAll) {
            FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean contentBeanAt = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean();
            contentBeanAt.setTag("at");
            contentBeanAt.setUser_id("all");
            contentBeans.add(contentBeanAt);
        }

        String gitLog = CmdHelper.getGitLogByTimeAndCount(-1, -1);
        if (!PluginUtils.isEmpty(gitLog)) {
            List<FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean> contentGitLogBeans = new ArrayList<>();
            FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean contentGitLogBeanText = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean();
            contentGitLogBeanText.setTag("text");
            contentGitLogBeanText.setText("更新内容：\n " + gitLog);
            contentGitLogBeans.add(contentGitLogBeanText);
        }

        List<List<FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean>> zhCnContentList = new ArrayList<>();
        zhCnContentList.add(contentBeans);

        FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO zhCnDTO = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO();
        zhCnDTO.setTitle(titleStr.toString());
        zhCnDTO.setContent(zhCnContentList);
        postDTO.setZh_cn(zhCnDTO);
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
                List<String> mentionedList = new ArrayList<>();
                mentionedList.add("@all");
                textDTO.setMentioned_list(mentionedList);
                textDTO.setMentioned_mobile_list(mentionedList);
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
