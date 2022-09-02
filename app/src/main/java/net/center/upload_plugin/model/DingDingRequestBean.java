package net.center.upload_plugin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by center
 * 2021-09-04.
 * <p>
 * 目前只支持markdown语法的子集，具体支持的元素如下：
 * <p>
 * 标题
 * # 一级标题
 * ## 二级标题
 * ### 三级标题
 * #### 四级标题
 * ##### 五级标题
 * ###### 六级标题
 * <p>
 * 引用
 * > A man who stands for nothing will fall for anything.
 * <p>
 * 文字加粗、斜体
 * **bold**
 * *italic*
 * <p>
 * 链接
 * [this is a link](http://name.com)
 * <p>
 * 图片
 * ![](http://name.com/pic.jpg)
 * <p>
 * 无序列表
 * - item1
 * - item2
 * <p>
 * 有序列表
 * 1. item1
 * 2. item2
 */
public class DingDingRequestBean {


    @SerializedName("msgtype")
    private String msgtype;

    /**
     * link类型的消息
     */
    @SerializedName("link")
    private LinkDTO link;

    /**
     * markdown 类型的消息
     */
    @SerializedName("markdown")
    private MarkDownDTO markdown;
    @SerializedName("at")
    private AtDTO at;

    /**
     * 整体跳转ActionCard类型和独立跳转ActionCard类型
     */
    @SerializedName("actionCard")
    private ActionCardDTO actionCard;


    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public LinkDTO getLink() {
        return link;
    }

    public void setLink(LinkDTO link) {
        this.link = link;
    }

    public MarkDownDTO getMarkdown() {
        return markdown;
    }

    public void setMarkdown(MarkDownDTO markdown) {
        this.markdown = markdown;
    }

    public AtDTO getAt() {
        return at;
    }

    public void setAt(AtDTO at) {
        this.at = at;
    }

    public ActionCardDTO getActionCard() {
        return actionCard;
    }

    public void setActionCard(ActionCardDTO actionCard) {
        this.actionCard = actionCard;
    }

    public static class LinkDTO {
        @SerializedName("text")
        private String text;
        @SerializedName("title")
        private String title;
        @SerializedName("picUrl")
        private String picUrl;
        @SerializedName("messageUrl")
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

    /**
     * "markdown": {
     * "title":"杭州天气",
     * "text": "#### 杭州天气 @150XXXXXXXX \n > 9度，西北风1级，空气良89，相对温度73%\n > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n > ###### 10点20分发布 [天气](https://www.dingtalk.com) \n"
     * },
     */
    public static class MarkDownDTO {
        /**
         * markdown格式的消息。
         */
        @SerializedName("text")
        private String text;
        @SerializedName("title")
        private String title;

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
    }

    /**
     * "at": {
     * "atMobiles": [
     * "150XXXXXXXX"
     * ],
     * "atUserIds": [
     * "user123"
     * ],
     * "isAtAll": false
     * }
     */
    public static class AtDTO {
        @SerializedName("atMobiles")
        private List<String> atMobiles;
        @SerializedName("atUserIds")
        private List<String> atUserIds;
        @SerializedName("isAtAll")
        private boolean isAtAll;

        public List<String> getAtMobiles() {
            return atMobiles;
        }

        public void setAtMobiles(List<String> atMobiles) {
            this.atMobiles = atMobiles;
        }

        public List<String> getAtUserIds() {
            return atUserIds;
        }

        public void setAtUserIds(List<String> atUserIds) {
            this.atUserIds = atUserIds;
        }

        public boolean isAtAll() {
            return isAtAll;
        }

        public void setIsAtAll(boolean atAll) {
            isAtAll = atAll;
        }
        
    }

    /**
     * "actionCard": {
     * "title": "乔布斯 20 年前想打造一间苹果咖啡厅，而它正是 Apple Store 的前身",
     * "text": "![screenshot](https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png)
     * ### 乔布斯 20 年前想打造的苹果咖啡厅
     * Apple Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划",
     * "btnOrientation": "0",
     * "singleTitle" : "阅读全文",
     * "singleURL" : "https://www.dingtalk.com/"
     * },
     * <p>
     * <p>
     * "actionCard": {
     * "title": "我 20 年前想打造一间苹果咖啡厅，而它正是 Apple Store 的前身",
     * "text": "![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png) \n\n #### 乔布斯 20 年前想打造的苹果咖啡厅 \n\n Apple Store 的设计正从原来满满的科技感走向生活化，而其生活化的走向其实可以追溯到 20 年前苹果一个建立咖啡馆的计划",
     * "btnOrientation": "0",
     * "btns": [{
     * "title": "内容不错",
     * "actionURL": "https://www.dingtalk.com/"
     * },
     * {
     * "title": "不感兴趣",
     * "actionURL": "https://www.dingtalk.com/"
     * }
     * ]
     * }
     */
    public static class ActionCardDTO {
        @SerializedName("title")
        private String title;
        @SerializedName("text")
        private String text;
        @SerializedName("btnOrientation")
        private String btnOrientation;

        /**
         * 整体跳转ActionCard类型 使用
         */
        @SerializedName("singleTitle")
        private String singleTitle;
        @SerializedName("singleURL")
        private String singleURL;

        /**
         * 独立跳转ActionCard类型 使用
         */
        @SerializedName("btns")
        private List<BtnsDTO> btns;

        public static class BtnsDTO {
            @SerializedName("title")
            private String title;
            @SerializedName("actionURL")
            private String actionURL;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getActionURL() {
                return actionURL;
            }

            public void setActionURL(String actionURL) {
                this.actionURL = actionURL;
            }
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getBtnOrientation() {
            return btnOrientation;
        }

        public void setBtnOrientation(String btnOrientation) {
            this.btnOrientation = btnOrientation;
        }

        public String getSingleTitle() {
            return singleTitle;
        }

        public void setSingleTitle(String singleTitle) {
            this.singleTitle = singleTitle;
        }

        public String getSingleURL() {
            return singleURL;
        }

        public void setSingleURL(String singleURL) {
            this.singleURL = singleURL;
        }

        public List<BtnsDTO> getBtns() {
            return btns;
        }

        public void setBtns(List<BtnsDTO> btns) {
            this.btns = btns;
        }
    }
}
