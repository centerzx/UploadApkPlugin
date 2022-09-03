package net.center.upload_plugin.helper;

import net.center.upload_plugin.PluginUtils;
import net.center.upload_plugin.model.DingDingRequestBean;
import net.center.upload_plugin.model.PgyUploadResult;
import net.center.upload_plugin.model.WXGroupRequestBean;
import net.center.upload_plugin.model.feishu.ElementsDTO;
import net.center.upload_plugin.model.feishu.FeiShuRequestBean;
import net.center.upload_plugin.model.feishu.TextDTO;
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

    private static final String defaultTitle = "测试包";
    private static final String defaultText = "最新开发测试包已上传 ";
    private static final String defaultClickText = "点我进行下载";
    private static final String defaultLogTitle = "更新内容：\n ";

    /**
     * 发送消息到钉钉
     *
     * @param project
     * @param dataDTO
     */
    public static void sendMsgToDingDing(Project project, PgyUploadResult.DataDTO dataDTO, String gitLog) {
        SendDingParams dingParams = SendDingParams.getDingParamsConfig(project);
        if (PluginUtils.isEmpty(dingParams.accessToken)) {
            System.out.println("send to Dingding failure：accessToken is empty");
            return;
        }
        DingDingRequestBean requestBean = new DingDingRequestBean();
        String title = dingParams.contentTitle;
        if (PluginUtils.isEmpty(title)) {
            title = defaultTitle;
        }
        StringBuilder titleStr = new StringBuilder(dataDTO.getBuildName()).append("V").append(dataDTO.getBuildVersion()).append(" ").append(title);
        String text = dingParams.contentText;
        if (PluginUtils.isEmpty(text)) {
            text = defaultText;
        }
        if ("markdown".equals(dingParams.msgtype)) {
            requestBean.setMsgtype("markdown");
            DingDingRequestBean.MarkDownDTO markDownBean = new DingDingRequestBean.MarkDownDTO();
            markDownBean.setTitle(titleStr.toString());
            StringBuilder contentStr = new StringBuilder("**").append(titleStr).append("** \n ").append(text).append(" ");
            contentStr.append("*").append(dataDTO.getBuildCreated()).append("*").append("\n");
            String clickTxt = dingParams.clickTxt;
            if (PluginUtils.isEmpty(clickTxt)) {
                clickTxt = defaultClickText;
            }
            contentStr.append("[").append(clickTxt).append("](https://www.pgyer.com/").append(dataDTO.getBuildShortcutUrl()).append(")\n");
            contentStr.append("![二维码](").append(dataDTO.getBuildQRCodeURL()).append(")\n");
            if (!PluginUtils.isEmpty(gitLog) && dingParams.isSupportGitLog) {
                contentStr.append("### ").append(defaultLogTitle).append(gitLog);
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
            StringBuilder contentStr = new StringBuilder("**").append(titleStr).append("** \n ").append(text).append(" ");
            contentStr.append("*").append(dataDTO.getBuildCreated()).append("*").append("\n");
            contentStr.append("![二维码](").append(dataDTO.getBuildQRCodeURL()).append(")\n");
            if (!PluginUtils.isEmpty(gitLog) && dingParams.isSupportGitLog) {
                contentStr.append("### ").append(defaultLogTitle).append(gitLog);
            }
            actionCardBean.setText(contentStr.toString());
            //0：按钮竖直排列 1：按钮横向排列
            actionCardBean.setBtnOrientation("0");
            String clickTxt = dingParams.clickTxt;
            if (PluginUtils.isEmpty(clickTxt)) {
                clickTxt = defaultClickText;
            }
            actionCardBean.setSingleTitle(clickTxt);
            actionCardBean.setSingleURL("https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
            requestBean.setActionCard(actionCardBean);
        } else {
            requestBean.setMsgtype("link");
            DingDingRequestBean.LinkDTO linkBean = new DingDingRequestBean.LinkDTO();
            StringBuilder contentStr = new StringBuilder(text).append(dataDTO.getBuildCreated());
            if (!PluginUtils.isEmpty(gitLog) && dingParams.isSupportGitLog) {
                contentStr.append("\n ").append(defaultLogTitle).append(gitLog);
            }
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
    public static void sendMsgToFeishu(Project project, PgyUploadResult.DataDTO dataDTO, String gitLog) {
        SendFeishuParams feishuParams = SendFeishuParams.getFeishuParamsConfig(project);
        String webHookHostUrl = feishuParams.webHookHostUrl;
        if (PluginUtils.isEmpty(webHookHostUrl)) {
            System.out.println("send to feishu failure：webHookHostUrl is empty");
            return;
        }
        String title = feishuParams.contentTitle;
        if (PluginUtils.isEmpty(title)) {
            title = defaultTitle;
        }
        StringBuilder titleStr = new StringBuilder(dataDTO.getBuildName()).append("V").append(dataDTO.getBuildVersion()).append(" ").append(title);
        String text = feishuParams.contentText;
        if (PluginUtils.isEmpty(text)) {
            text = defaultText;
        }
        StringBuilder textStr = new StringBuilder("**").append(text).append("** ").append(dataDTO.getBuildCreated()).append(" \n");
        FeiShuRequestBean feiShuRequestBean = new FeiShuRequestBean();
        if ("interactive".equals(feishuParams.msgtype)) {
            FeiShuRequestBean.CardDTO cardDTO = new FeiShuRequestBean.CardDTO();

            feiShuRequestBean.setMsg_type("interactive");
            FeiShuRequestBean.CardDTO.ConfigDTO cardConfigBean = new FeiShuRequestBean.CardDTO.ConfigDTO();
            cardConfigBean.setWide_screen_mode(true);
            cardConfigBean.setEnable_forward(true);
            cardDTO.setConfig(cardConfigBean);
            FeiShuRequestBean.CardDTO.HeaderDTO cardHeaderBean = new FeiShuRequestBean.CardDTO.HeaderDTO();
            cardHeaderBean.setTemplate("green");
            TextDTO cardHeaderTitleBean = new TextDTO();
            cardHeaderTitleBean.setTag("plain_text");
            cardHeaderTitleBean.setContent("\uD83D\uDE18 " + titleStr);
            cardHeaderBean.setTitle(cardHeaderTitleBean);
            cardDTO.setHeader(cardHeaderBean);
            List<ElementsDTO> elementsDTOList = new ArrayList<>();
            ElementsDTO elements1 = new ElementsDTO();
            elements1.setTag("div");
            TextDTO elements1TextBean = new TextDTO();
            elements1TextBean.setTag("lark_md");
            textStr.append("[").append(PluginUtils.isEmpty(feishuParams.clickTxt) ? defaultClickText : feishuParams.clickTxt)
                    .append("]").append("(https://www.pgyer.com/").append(dataDTO.getBuildShortcutUrl()).append(")").append("   ");
            textStr.append("[查看下载二维码]").append("(").append(dataDTO.getBuildQRCodeURL()).append(")");
            if (feishuParams.isAtAll) {
                textStr.append(" \n").append("<at id=all></at>");
            }
            elements1TextBean.setContent(textStr.toString());
            elements1.setText(elements1TextBean);
            elementsDTOList.add(elements1);
            ElementsDTO elements2 = new ElementsDTO();
            elements2.setTag("hr");
            elementsDTOList.add(elements2);
            if (!PluginUtils.isEmpty(gitLog) && feishuParams.isSupportGitLog) {
                ElementsDTO elements3 = new ElementsDTO();
                elements3.setTag("div");
                TextDTO elements3TextBean = new TextDTO();
                elements3TextBean.setTag("lark_md");
                StringBuilder logStrBuilder = new StringBuilder("**").append(defaultLogTitle).append("**").append(gitLog);
                elements3TextBean.setContent(logStrBuilder.toString());
                elements3.setText(elements3TextBean);
                elementsDTOList.add(elements3);
                ElementsDTO elements4 = new ElementsDTO();
                elements4.setTag("hr");
                elementsDTOList.add(elements4);
            }
            ElementsDTO elements5 = new ElementsDTO();
            elements5.setTag("action");
            List<ElementsDTO> actionsList = new ArrayList<>();
            ElementsDTO actionBtnDownBtn = new ElementsDTO();
            actionBtnDownBtn.setTag("button");
            actionBtnDownBtn.setType("primary");
            actionBtnDownBtn.setUrl("https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
            TextDTO actionDownBtnText = new TextDTO();
            actionDownBtnText.setTag("plain_text");
            actionDownBtnText.setContent(PluginUtils.isEmpty(feishuParams.clickTxt) ? defaultClickText : feishuParams.clickTxt);
            actionBtnDownBtn.setText(actionDownBtnText);
            actionsList.add(actionBtnDownBtn);

            ElementsDTO actionBtnQRCodeBtn = new ElementsDTO();
            actionBtnQRCodeBtn.setTag("button");
            actionBtnQRCodeBtn.setType("primary");
            actionBtnQRCodeBtn.setUrl(dataDTO.getBuildQRCodeURL());
            TextDTO actionQRCodeBtnText = new TextDTO();
            actionQRCodeBtnText.setTag("plain_text");
            actionQRCodeBtnText.setContent("查看下载二维码");
            actionBtnQRCodeBtn.setText(actionQRCodeBtnText);
            actionsList.add(actionBtnQRCodeBtn);
            elements5.setActions(actionsList);
            elementsDTOList.add(elements5);
            cardDTO.setElements(elementsDTOList);
            feiShuRequestBean.setCard(cardDTO);
        } else {
            FeiShuRequestBean.ContentDTO contentDTO = new FeiShuRequestBean.ContentDTO();

            feiShuRequestBean.setMsg_type("post");
            FeiShuRequestBean.ContentDTO.PostDTO postDTO = new FeiShuRequestBean.ContentDTO.PostDTO();

            FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean contentBeanText = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean();
            contentBeanText.setTag("text");
            contentBeanText.setText(textStr.toString());
            FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean contentBeanA = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean();
            contentBeanA.setTag("a");
            contentBeanA.setText(PluginUtils.isEmpty(feishuParams.clickTxt) ? defaultClickText : feishuParams.clickTxt);
            contentBeanA.setHref("https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
            List<FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean> contentBeans = new ArrayList<>();
            contentBeans.add(contentBeanText);
            contentBeans.add(contentBeanA);
            if (feishuParams.isAtAll) {
                FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean contentBeanAt = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean();
                contentBeanAt.setTag("at");
                contentBeanAt.setUser_id("all");
                contentBeans.add(contentBeanAt);
            }
            List<List<FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean>> zhCnContentList = new ArrayList<>();
            zhCnContentList.add(contentBeans);
            if (!PluginUtils.isEmpty(gitLog) && feishuParams.isSupportGitLog) {
                List<FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean> contentGitLogBeans = new ArrayList<>();
                FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean contentGitLogBeanText = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO.ContentBean();
                contentGitLogBeanText.setTag("text");
                contentGitLogBeanText.setText("** " + defaultLogTitle + gitLog);
                contentGitLogBeans.add(contentGitLogBeanText);
                zhCnContentList.add(contentGitLogBeans);
            }

            FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO zhCnDTO = new FeiShuRequestBean.ContentDTO.PostDTO.ZhCnDTO();
            zhCnDTO.setTitle(titleStr.toString());
            zhCnDTO.setContent(zhCnContentList);
            postDTO.setZh_cn(zhCnDTO);
            contentDTO.setPost(postDTO);
            feiShuRequestBean.setContent(contentDTO);
        }


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

    /**
     * 发送消息到微信群
     *
     * @param project
     * @param dataDTO
     */
    public static void sendMsgToWeiXinGroup(Project project, PgyUploadResult.DataDTO dataDTO, String gitLog) {
        SendWeixinGroupParams weixinGroupParams = SendWeixinGroupParams.getWeixinGroupConfig(project);
        String webHookUrl = weixinGroupParams.webHookUrl;
        if (PluginUtils.isEmpty(webHookUrl)) {
            System.out.println("send to weixin group failure：webHookUrl is empty");
            return;
        }
        String contentTitle = weixinGroupParams.contentTitle;
        if (PluginUtils.isEmpty(contentTitle)) {
            contentTitle = defaultTitle;
        }
        String contentText = weixinGroupParams.contentText;
        if (PluginUtils.isEmpty(contentText)) {
            contentText = defaultText;
        }
        WXGroupRequestBean wxGroupRequestBean = new WXGroupRequestBean();
        if ("text".equals(weixinGroupParams.msgtype)) {
            wxGroupRequestBean.setMsgtype("text");
            WXGroupRequestBean.TextDTO textDTO = new WXGroupRequestBean.TextDTO();
            String contentStr = dataDTO.getBuildName() + contentTitle + "\n" + "下载链接：https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl() + " \n" + contentText;
            textDTO.setContent(contentStr);
            if (weixinGroupParams.isAtAll) {
                List<String> mentionedList = new ArrayList<>();
                mentionedList.add("@all");
                textDTO.setMentioned_list(mentionedList);
                textDTO.setMentioned_mobile_list(mentionedList);
            }
            wxGroupRequestBean.setText(textDTO);
        } else if ("news".equals(weixinGroupParams.msgtype)) {
            wxGroupRequestBean.setMsgtype("news");
            WXGroupRequestBean.NewsDTO newsDTO = new WXGroupRequestBean.NewsDTO();
            WXGroupRequestBean.NewsDTO.ArticlesDTO articlesDTO = new WXGroupRequestBean.NewsDTO.ArticlesDTO();
            articlesDTO.setTitle(dataDTO.getBuildName() + " V" + dataDTO.getBuildVersion() + " " + dataDTO.getBuildCreated());
            String desStr = "最新开发测试包已上传,请下载测试吧！ ";
            if (!PluginUtils.isEmpty(contentTitle) && !PluginUtils.isEmpty(contentText)) {
                desStr = contentTitle + " \n" + contentText;
            } else if (!PluginUtils.isEmpty(contentTitle)) {
                desStr = contentTitle;
            } else if (!PluginUtils.isEmpty(contentText)) {
                desStr = contentText;
            }
            articlesDTO.setDescription(desStr);
            articlesDTO.setUrl("https://www.pgyer.com/" + dataDTO.getBuildShortcutUrl());
            articlesDTO.setPicurl(dataDTO.getBuildQRCodeURL());
            List<WXGroupRequestBean.NewsDTO.ArticlesDTO> articlesDTOList = new ArrayList<>();
            articlesDTOList.add(articlesDTO);
            newsDTO.setArticles(articlesDTOList);
            wxGroupRequestBean.setNews(newsDTO);
        } else {
            wxGroupRequestBean.setMsgtype("markdown");
            WXGroupRequestBean.MarkdownDTO markdownDTO = new WXGroupRequestBean.MarkdownDTO();
            StringBuilder markStr = new StringBuilder();
            markStr.append("**").append(dataDTO.getBuildName()).append("** V").append(dataDTO.getBuildVersion())
                    .append("  ").append(dataDTO.getBuildCreated()).append(" \n")
                    .append(contentTitle).append(" \n").append(contentText).append(" \n")
                    .append("<font color=\"info\">[下载链接，点击下载](https://www.pgyer.com/")
                    .append(dataDTO.getBuildShortcutUrl()).append(")</font>");
            if (!PluginUtils.isEmpty(gitLog) && weixinGroupParams.isSupportGitLog) {
                markStr.append(" \n").append("**").append(defaultLogTitle).append("**").append(gitLog);
            }
            markdownDTO.setContent(markStr.toString());
            wxGroupRequestBean.setMarkdown(markdownDTO);
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
