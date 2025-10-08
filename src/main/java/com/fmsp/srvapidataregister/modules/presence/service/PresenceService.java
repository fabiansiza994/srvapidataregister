package com.fmsp.srvapidataregister.modules.presence.service;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
public class PresenceService {
    private final StringRedisTemplate redis;
    private static final String KEY_FMT = "presence:user:%s"; // presence:user:{userId}
    private static final Duration TTL = Duration.ofSeconds(120); // 2 min “online”

    public PresenceService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @PostConstruct
    public void testRedis() {
        try {
            redis.opsForValue().set("test:key", "Hola Redis");
            System.out.println("Valor Redis = " + redis.opsForValue().get("test:key"));
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo conectar a Redis en startup: " + e.getMessage());
        }
    }


    public void ping(Long userId, String username, String nombre, String apellido) {
        String key = KEY_FMT.formatted(userId);
        String payload = String.format(
                "{\"id\":%d,\"usuario\":\"%s\",\"nombre\":\"%s\",\"apellido\":\"%s\",\"ts\":%d}",
                userId, safe(username), safe(nombre), safe(apellido), System.currentTimeMillis());
        redis.opsForValue().set(key, payload, TTL);
    }

    public List<String> listRawOnline() {
        var connection = Objects.requireNonNull(redis.getConnectionFactory()).getConnection();
        try (connection) {
            var scan = connection.scan(org.springframework.data.redis.core.ScanOptions.scanOptions()
                    .match("presence:user:*")
                    .count(500)
                    .build());
            List<byte[]> vals = new java.util.ArrayList<>();
            scan.forEachRemaining(key -> {
                byte[] v = connection.stringCommands().get(key);
                if (v != null) vals.add(v);
            });
            return vals.stream().map(String::new).toList();
        }
    }


    private static String safe(String s) {
        return s == null ? "" : s;
    }
}