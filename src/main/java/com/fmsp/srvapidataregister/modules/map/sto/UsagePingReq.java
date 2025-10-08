package com.fmsp.srvapidataregister.modules.map.sto;

public record UsagePingReq(
        Double lat, Double lng, Long userId, String appVersion, String tz, String platform
) {}