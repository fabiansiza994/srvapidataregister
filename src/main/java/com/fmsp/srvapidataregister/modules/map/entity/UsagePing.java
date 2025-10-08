package com.fmsp.srvapidataregister.modules.map.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
public class UsagePing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double lat;
    private Double lng;

    private String ip;
    private String city;
    private String country;

    private String tz;
    private String platform;
    private String appVersion;
    private Long userId;

    private Instant createdAt = Instant.now();
}
