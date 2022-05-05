package net.center.upload_plugin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by center
 * 企业微信发送消息请求实体类
 */
public class WXGroupRequestBean {

    @SerializedName("msgtype")
    private String msgtype;

    @SerializedName("text")
    private TextDTO text;

    @SerializedName("markdown")
    private MarkdownDTO markdown;

    @SerializedName("news")
    private NewsDTO news;


    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public TextDTO getText() {
        return text;
    }

    public void setText(TextDTO text) {
        this.text = text;
    }

    public MarkdownDTO getMarkdown() {
        return markdown;
    }

    public void setMarkdown(MarkdownDTO markdown) {
        this.markdown = markdown;
    }

    public NewsDTO getNews() {
        return news;
    }

    public void setNews(NewsDTO news) {
        this.news = news;
    }

    public static class NewsDTO {
        @SerializedName("articles")
        private List<ArticlesDTO> articles;

        public List<ArticlesDTO> getArticles() {
            return articles;
        }

        public void setArticles(List<ArticlesDTO> articles) {
            this.articles = articles;
        }

        public static class ArticlesDTO {
            @SerializedName("title")
            private String title;
            @SerializedName("description")
            private String description;
            @SerializedName("url")
            private String url;
            @SerializedName("picurl")
            private String picurl;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getPicurl() {
                return picurl;
            }

            public void setPicurl(String picurl) {
                this.picurl = picurl;
            }
        }
    }

    /**
     * "content": "实时新增用户反馈<font color=\"warning\">132例</font>，请相关同事注意。\n
     * >类型:<font color=\"comment\">用户反馈</font>
     * >普通用户反馈:<font color=\"comment\">117例</font>
     * >VIP用户反馈:<font color=\"comment\">15例</font>"
     * <p>
     * [这是一个链接](http://work.weixin.qq.com/api/doc)
     *
     * <font color="info">绿色</font>
     * <font color="comment">灰色</font>
     * <font color="warning">橙红色</font>
     */
    public static class MarkdownDTO {
        @SerializedName("content")
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    /**
     * {
     *     "msgtype": "text",
     *     "text": {
     *         "content": "广州今日天气：29度，大部分多云，降雨概率：60%",
     * 		"mentioned_list":["wangqing","@all"],
     * 		"mentioned_mobile_list":["13800001111","@all"]
     *     }
     * }
     */
    public static class TextDTO {
        @SerializedName("content")
        private String content;
        @SerializedName("mentioned_list")
        private List<String> mentionedList;
        @SerializedName("mentioned_mobile_list")
        private List<String> mentionedMobileList;


        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getMentionedList() {
            return mentionedList;
        }

        public void setMentionedList(List<String> mentionedList) {
            this.mentionedList = mentionedList;
        }

        public List<String> getMentionedMobileList() {
            return mentionedMobileList;
        }

        public void setMentionedMobileList(List<String> mentionedMobileList) {
            this.mentionedMobileList = mentionedMobileList;
        }
    }
}
