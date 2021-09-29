package net.center.upload_plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by center
 * 2021-09-03.
 * 蒲公英上传成功后的数据model
 * 
 */
public class PgyUploadResult {

    @SerializedName("code")
    private Integer code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private DataDTO data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("buildKey")
        private String buildKey;
        @SerializedName("buildType")
        private String buildType;
        @SerializedName("buildIsFirst")
        private String buildIsFirst;
        @SerializedName("buildIsLastest")
        private String buildIsLastest;
        @SerializedName("buildFileKey")
        private String buildFileKey;
        @SerializedName("buildFileName")
        private String buildFileName;
        @SerializedName("buildFileSize")
        private String buildFileSize;
        @SerializedName("buildName")
        private String buildName;
        @SerializedName("buildVersion")
        private String buildVersion;
        @SerializedName("buildVersionNo")
        private String buildVersionNo;
        @SerializedName("buildBuildVersion")
        private String buildBuildVersion;
        @SerializedName("buildIdentifier")
        private String buildIdentifier;
        @SerializedName("buildIcon")
        private String buildIcon;
        @SerializedName("buildDescription")
        private String buildDescription;
        @SerializedName("buildUpdateDescription")
        private String buildUpdateDescription;
        @SerializedName("buildScreenshots")
        private String buildScreenshots;
        @SerializedName("buildShortcutUrl")
        private String buildShortcutUrl;
        @SerializedName("buildCreated")
        private String buildCreated;
        @SerializedName("buildUpdated")
        private String buildUpdated;
        @SerializedName("buildQRCodeURL")
        private String buildQRCodeURL;


        public String getBuildKey() {
            return buildKey;
        }

        public void setBuildKey(String buildKey) {
            this.buildKey = buildKey;
        }

        public String getBuildType() {
            return buildType;
        }

        public void setBuildType(String buildType) {
            this.buildType = buildType;
        }

        public String getBuildIsFirst() {
            return buildIsFirst;
        }

        public void setBuildIsFirst(String buildIsFirst) {
            this.buildIsFirst = buildIsFirst;
        }

        public String getBuildIsLastest() {
            return buildIsLastest;
        }

        public void setBuildIsLastest(String buildIsLastest) {
            this.buildIsLastest = buildIsLastest;
        }

        public String getBuildFileKey() {
            return buildFileKey;
        }

        public void setBuildFileKey(String buildFileKey) {
            this.buildFileKey = buildFileKey;
        }

        public String getBuildFileName() {
            return buildFileName;
        }

        public void setBuildFileName(String buildFileName) {
            this.buildFileName = buildFileName;
        }

        public String getBuildFileSize() {
            return buildFileSize;
        }

        public void setBuildFileSize(String buildFileSize) {
            this.buildFileSize = buildFileSize;
        }

        public String getBuildName() {
            return buildName;
        }

        public void setBuildName(String buildName) {
            this.buildName = buildName;
        }

        public String getBuildVersion() {
            return buildVersion;
        }

        public void setBuildVersion(String buildVersion) {
            this.buildVersion = buildVersion;
        }

        public String getBuildVersionNo() {
            return buildVersionNo;
        }

        public void setBuildVersionNo(String buildVersionNo) {
            this.buildVersionNo = buildVersionNo;
        }

        public String getBuildBuildVersion() {
            return buildBuildVersion;
        }

        public void setBuildBuildVersion(String buildBuildVersion) {
            this.buildBuildVersion = buildBuildVersion;
        }

        public String getBuildIdentifier() {
            return buildIdentifier;
        }

        public void setBuildIdentifier(String buildIdentifier) {
            this.buildIdentifier = buildIdentifier;
        }

        public String getBuildIcon() {
            return buildIcon;
        }

        public void setBuildIcon(String buildIcon) {
            this.buildIcon = buildIcon;
        }

        public String getBuildDescription() {
            return buildDescription;
        }

        public void setBuildDescription(String buildDescription) {
            this.buildDescription = buildDescription;
        }

        public String getBuildUpdateDescription() {
            return buildUpdateDescription;
        }

        public void setBuildUpdateDescription(String buildUpdateDescription) {
            this.buildUpdateDescription = buildUpdateDescription;
        }

        public String getBuildScreenshots() {
            return buildScreenshots;
        }

        public void setBuildScreenshots(String buildScreenshots) {
            this.buildScreenshots = buildScreenshots;
        }

        public String getBuildShortcutUrl() {
            return buildShortcutUrl;
        }

        public void setBuildShortcutUrl(String buildShortcutUrl) {
            this.buildShortcutUrl = buildShortcutUrl;
        }

        public String getBuildCreated() {
            return buildCreated;
        }

        public void setBuildCreated(String buildCreated) {
            this.buildCreated = buildCreated;
        }

        public String getBuildUpdated() {
            return buildUpdated;
        }

        public void setBuildUpdated(String buildUpdated) {
            this.buildUpdated = buildUpdated;
        }

        public String getBuildQRCodeURL() {
            return buildQRCodeURL;
        }

        public void setBuildQRCodeURL(String buildQRCodeURL) {
            this.buildQRCodeURL = buildQRCodeURL;
        }
    }
}
