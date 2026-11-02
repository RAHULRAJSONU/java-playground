package io.github.rahulrajsonu.systemdesign.ratelimiter;

import java.util.Map;
import java.util.UUID;

public class ClientService {

    private static final Map<UUID, SubscriptionTier> clients = Map.of(
            UUID.fromString("862261e9-0253-42c4-95d3-76e769d48bd8"), SubscriptionTier.FREE,
            UUID.fromString("dcd23d1c-3c4d-472e-808a-6f9b136a9b9b"), SubscriptionTier.PREMIUM,
            UUID.fromString("71692a59-fd55-479b-adbc-50dd450faf9e"), SubscriptionTier.ENTERPRISES
    );

    private static final Map<SubscriptionTier, RateLimitConfig> rateLimitConfigs = Map.of(
       SubscriptionTier.FREE, new RateLimitConfig(5, 2.0),
        SubscriptionTier.PREMIUM, new RateLimitConfig(100, 20.0),
        SubscriptionTier.ENTERPRISES, new RateLimitConfig(1000, 200.0)
    );

    public static RateLimitConfig getClientTier(UUID clientId){
        SubscriptionTier tier = clients.getOrDefault(clientId, null);
        if(tier == null){throw new RuntimeException("Client not found");}
        return rateLimitConfigs.get(tier);
    }
}
