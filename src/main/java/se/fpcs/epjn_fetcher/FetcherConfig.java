package se.fpcs.epjn_fetcher;

import org.springframework.stereotype.Component;

@Component
public class FetcherConfig {

    public FetcherConfig() {
        // ensure present
        getEnvOrThrow("GOOGLE_APPLICATION_CREDENTIALS");
    }

    public String getProjectId() {
        return getEnvOrThrow("GCP_PROJECT_ID");
    }

    public String getLocationId() {
        return getEnvOrThrow("GCP_LOCATION_ID");
    }

    public String getQueueId() {
        return getEnvOrThrow("GCP_QUEUE_ID");
    }

    public String getTargetUrl() {
        return getEnvOrThrow("GCP_TARGET_URL");
    }

    private String getEnvOrThrow(String key) {
        String val = System.getenv(key);
        if (val == null || val.isBlank()) {
            throw new IllegalStateException("Missing required env var: " + key);
        }
        return val;
    }
}
