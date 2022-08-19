package net.center.upload_plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by center
 * 2021-09-03.
 * 蒲公英上传成功后的数据model
 * <p>
 * buildKey	String	Build Key是唯一标识应用的索引ID
 * buildType	Integer	应用类型（1:iOS; 2:Android）
 * buildIsFirst	Integer	是否是第一个App（1:是; 2:否）
 * buildIsLastest	Integer	是否是最新版（1:是; 2:否）
 * buildFileSize	Integer	App 文件大小
 * buildName	String	应用名称
 * buildVersion	String	版本号, 默认为1.0 (是应用向用户宣传时候用到的标识，例如：1.1、8.2.1等。)
 * buildVersionNo	String	上传包的版本编号，默认为1 (即编译的版本号，一般来说，编译一次会变动一次这个版本号, 在 Android 上叫 Version Code。对于 iOS 来说，是字符串类型；对于 Android 来说是一个整数。例如：1001，28等。)
 * buildBuildVersion	Integer	蒲公英生成的用于区分历史版本的build号
 * buildIdentifier	String	应用程序包名，iOS为BundleId，Android为包名
 * buildIcon	String	应用的Icon图标key，访问地址为 https://www.pgyer.com/image/view/app_icons/[应用的Icon图标key]
 * buildDescription	String	应用介绍
 * buildUpdateDescription	String	应用更新说明
 * buildScreenShots	String	应用截图的key，获取地址为 https://www.pgyer.com/image/view/app_screenshots/[应用截图的key]
 * buildShortcutUrl	String	应用短链接
 * buildQRCodeURL	String	应用二维码地址
 * buildCreated	String	应用上传时间
 * buildUpdated	String	应用更新时间
 */
public class PgyUploadResult extends BasePgyResult {

    @SerializedName("data")
    private DataDTO data;

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
