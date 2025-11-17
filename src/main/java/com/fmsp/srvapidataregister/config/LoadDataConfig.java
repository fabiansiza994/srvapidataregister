package com.fmsp.srvapidataregister.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoadDataConfig {

    @Value("${front.url}")
    private String frontUrl;

    @Value("${recover.url}")
    private String recoveryUrl;

    public String getRecoveryUrl() {
        return recoveryUrl;
    }
    public void setRecoveryUrl(String recoveryUrl) {
        this.recoveryUrl = recoveryUrl;
    }
    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }
}
