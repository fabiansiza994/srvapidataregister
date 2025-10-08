package com.fmsp.srvapidataregister.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoadDataConfig {

    @Value("${front.url}")
    private String frontUrl;

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }
}
