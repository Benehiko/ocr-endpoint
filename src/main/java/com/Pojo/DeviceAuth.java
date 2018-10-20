package com.Pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceAuth {

    @JsonProperty("mac")
    private String mac;

    @JsonProperty("password")
    private String password;

    @JsonProperty("userid")
    private int userid;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
