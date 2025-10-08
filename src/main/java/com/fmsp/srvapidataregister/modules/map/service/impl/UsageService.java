package com.fmsp.srvapidataregister.modules.map.service.impl;

import com.fmsp.srvapidataregister.modules.map.entity.UsagePing;
import com.fmsp.srvapidataregister.modules.map.repository.UsagePingRepository;
import com.fmsp.srvapidataregister.modules.map.sto.UsagePoint;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsageService {
    private final UsagePingRepository repo;

    public UsageService(UsagePingRepository repo) {
        this.repo = repo;
    }

    public void ping(UsagePing ping, HttpServletRequest req) {
        // IP del request
        String xfwd = req.getHeader("X-Forwarded-For");
        ping.setIp(xfwd != null ? xfwd.split(",")[0].trim() : req.getRemoteAddr());

        // Si lat/lng viene null, podrías intentar resolver ciudad/país por IP (GeoIP)
        // Aquí lo dejamos vacío para mantener el ejemplo simple.
        repo.save(ping);
    }

    public List<UsagePoint> list() {
        return repo.findAll().stream()
                .filter(p -> p.getLat() != null && p.getLng() != null)
                .map(p -> new UsagePoint(
                        p.getLat(), p.getLng(), p.getCity(), p.getCountry(),
                        p.getCreatedAt() != null ? p.getCreatedAt().toString() : null
                ))
                .toList();
    }
}
