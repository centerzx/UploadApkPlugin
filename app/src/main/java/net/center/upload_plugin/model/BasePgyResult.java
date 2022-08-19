package net.center.upload_plugin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by center
 * Time:2022-08-20.
 * Desc:
 */
 public class BasePgyResult {

    @SerializedName("code")
    private Integer code;
    @SerializedName("message")
    private String message;


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
}
