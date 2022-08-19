package net.center.upload_plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by center
 * 2021-09-03.
 * 调用蒲公英getCOSToken接口后返回的数据
 * 
 */
public class PgyCOSTokenResult extends BasePgyResult {
    
    /**
     * key 上传文件存储标识唯一 key
     */
    @SerializedName("key")
    private String key;
    /**
     * 	上传文件的 URL
     */
    @SerializedName("endpoint")
    private String endpoint;
    
    @SerializedName("params")
    private DataDTO data;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("signature")
        private String signature;
        @SerializedName("x-cos-security-token")
        private String cosSecurityToken;
        @SerializedName("key")
        private String key;


        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getCosSecurityToken() {
            return cosSecurityToken;
        }

        public void setCosSecurityToken(String cosSecurityToken) {
            this.cosSecurityToken = cosSecurityToken;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
