package com.lucas.createurlshortner.model;

import lombok.Data;

@Data
public class UrlData {

    private String originalUrl;
    private Long expiratedTime;

    public UrlData(String originalUrl) {}
    public UrlData(String originalUrl, Long expiratedTime) {
        this.originalUrl = originalUrl;
        this.expiratedTime = expiratedTime;
    }

}
