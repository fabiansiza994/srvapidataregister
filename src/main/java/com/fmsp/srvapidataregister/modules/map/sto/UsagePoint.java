package com.fmsp.srvapidataregister.modules.map.sto;

public record UsagePoint(
        double lat, double lng, String city, String country, String ts
) {}