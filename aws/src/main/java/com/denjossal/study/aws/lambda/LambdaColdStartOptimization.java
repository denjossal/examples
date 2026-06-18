package com.denjossal.study.aws.lambda;

import java.util.*;

/**
 * Lambda cold start optimization patterns.
 *
 * Key strategies:
 * 1. Minimize deployment package size (remove unused deps)
 * 2. Initialize heavy resources OUTSIDE the handler (class-level)
 * 3. Use SnapStart (Java) — snapshot after init, restore on invoke
 * 4. Keep functions warm with provisioned concurrency or scheduled pings
 * 5. GraalVM native image for sub-100ms starts
 *
 * This class demonstrates the initialization pattern: heavy setup
 * happens once (cold start) and is reused across warm invocations.
 */
public class LambdaColdStartOptimization {

    // Simulates expensive initialization (DB connection, SDK client, config load)
    private final Map<String, String> configCache;
    private final long initTimeMs;

    public LambdaColdStartOptimization() {
        long start = System.nanoTime();
        this.configCache = loadConfiguration();
        this.initTimeMs = (System.nanoTime() - start) / 1_000_000;
    }

    /**
     * The actual handler — should be fast since init is done.
     */
    public Map<String, Object> handleRequest(Map<String, String> input) {
        long start = System.nanoTime();

        String action = input.getOrDefault("action", "default");
        String result = processAction(action);

        long handlerTimeMs = (System.nanoTime() - start) / 1_000_000;

        return Map.of(
                "statusCode", 200,
                "body", result,
                "metrics", Map.of(
                        "initTimeMs", initTimeMs,
                        "handlerTimeMs", handlerTimeMs,
                        "cachedConfigSize", configCache.size()
                )
        );
    }

    private String processAction(String action) {
        return switch (action) {
            case "greet" -> "Hello from Lambda! Config loaded: " + configCache.size() + " entries";
            case "config" -> configCache.toString();
            default -> "Unknown action: " + action;
        };
    }

    private Map<String, String> loadConfiguration() {
        // Simulates loading config from Parameter Store / Secrets Manager
        try { Thread.sleep(5); } catch (InterruptedException _) {}
        return Map.of(
                "DB_URL", "jdbc:postgresql://prod-db:5432/app",
                "CACHE_TTL", "300",
                "FEATURE_FLAG_X", "enabled"
        );
    }

    public long getInitTimeMs() {
        return initTimeMs;
    }
}
