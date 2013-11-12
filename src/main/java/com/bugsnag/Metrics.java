package com.bugsnag;

import org.json.JSONObject;

import com.bugsnag.http.HttpClient;
import com.bugsnag.http.NetworkException;

import com.bugsnag.utils.JSONUtils;

public class Metrics {
    private Configuration config;
    private Diagnostics diagnostics;

    public Metrics(Configuration config, Diagnostics diagnostics) {
        this.config = config;
        this.diagnostics = diagnostics;
    }

    public void deliver() throws NetworkException {
        String url = config.getMetricsEndpoint();
        try {
            HttpClient.post(url, this.toString(), "application/json");
        } catch(java.io.UnsupportedEncodingException e) {
            config.logger.warn("Bugsnag unable to send metrics", e);
        }
    }

    public JSONObject toJSON() {
        JSONObject metrics = diagnostics.getMetrics();

        JSONUtils.safePut(metrics, "apiKey", config.apiKey);

        return metrics;
    }

    public String toString() {
        return toJSON().toString();
    }
}