package net.center.upload_plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by center
 * 2021-09-03.
 * 调用蒲公英getCOSToken接口后返回的数据
 * 
 */
public class PgyCOSTokenResult extends BasePgyResult {


    @SerializedName("data")
    private DataDTO data;

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("params")
        private ParamsDTO params;
        @SerializedName("key")
        private String key;
        @SerializedName("endpoint")
        private String endpoint;

        public ParamsDTO getParams() {
            return params;
        }

        public void setParams(ParamsDTO params) {
            this.params = params;
        }

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

        public static class ParamsDTO {
            @SerializedName("signature")
            private String signature;
            @SerializedName("x-cos-security-token")
            private String xcossecuritytoken;
            @SerializedName("key")
            private String key;

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }

            public String getXcossecuritytoken() {
                return xcossecuritytoken;
            }

            public void setXcossecuritytoken(String xcossecuritytoken) {
                this.xcossecuritytoken = xcossecuritytoken;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }
    }
}