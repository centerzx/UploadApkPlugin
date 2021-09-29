package net.center.upload_plugin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by center
 * 2021-09-04.
 */
public class FeiShuRequestBean {


    @SerializedName("msg_type")
    private String msgType;
    @SerializedName("content")
    private ContentDTO content;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public ContentDTO getContent() {
        return content;
    }

    public void setContent(ContentDTO content) {
        this.content = content;
    }

    public static class ContentDTO {
        @SerializedName("post")
        private PostDTO post;

        public PostDTO getPost() {
            return post;
        }

        public void setPost(PostDTO post) {
            this.post = post;
        }

        public static class PostDTO {
            @SerializedName("zh_cn")
            public ZhCnDTO zhCn;

            public ZhCnDTO getZhCn() {
                return zhCn;
            }

            public void setZhCn(ZhCnDTO zhCn) {
                this.zhCn = zhCn;
            }

            public static class ZhCnDTO {
                @SerializedName("title")
                private String title;
                @SerializedName("content")
                private List<List<ContentBean>> content;

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public List<List<ContentBean>> getContent() {
                    return content;
                }

                public void setContent(List<List<ContentBean>> content) {
                    this.content = content;
                }

                public static class ContentBean {
                    @SerializedName("tag")
                    private String tag;
                    @SerializedName("text")
                    private String text;
                    @SerializedName("href")
                    private String href;
                    @SerializedName("user_id")
                    private String userId;

                    public String getTag() {
                        return tag;
                    }

                    public void setTag(String tag) {
                        this.tag = tag;
                    }

                    public String getText() {
                        return text;
                    }

                    public void setText(String text) {
                        this.text = text;
                    }

                    public String getHref() {
                        return href;
                    }

                    public void setHref(String href) {
                        this.href = href;
                    }

                    public String getUserId() {
                        return userId;
                    }

                    public void setUserId(String userId) {
                        this.userId = userId;
                    }
                }
            }
        }
    }
}
