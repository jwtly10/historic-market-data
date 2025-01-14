package com.jwtly.historicmarketdata.adapter.out.broker.oanda.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwtly.historicmarketdata.adapter.out.broker.oanda.model.CandlestickResponse;
import com.jwtly.historicmarketdata.domain.exception.BrokerRequestException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Implements <a href="https://developer.oanda.com/rest-live-v20/pricing-ep/">/instruments/{instrument}/candles</a> endpoint
 */
public class OandaClient {
    private final String apiKey;
    private final String apiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OandaClient(String apiKey, String apiUrl, HttpClient httpClient, ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public CandlestickResponse fetchCandles(String instrument, String granularity, Instant from, Instant to) {
        HttpRequest request = buildRequest(buildUri(instrument, granularity, from, to));

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), CandlestickResponse.class);
            }

            throw new BrokerRequestException(
                    "Oanda API error",
                    response.statusCode(),
                    response.body(),
                    null
            );
        } catch (IOException | InterruptedException e) {
            throw new BrokerRequestException(
                    "Failed to fetch candles",
                    500,
                    e.getMessage(),
                    e
            );
        }
    }

    private URI buildUri(String instrument, String granularity, Instant from, Instant to) {
        return URI.create(String.format("%s/v3/instruments/%s/candles?granularity=%s&from=%s&to=%s",
                apiUrl,
                URLEncoder.encode(instrument, StandardCharsets.UTF_8),
                URLEncoder.encode(granularity, StandardCharsets.UTF_8),
                URLEncoder.encode(DateTimeFormatter.ISO_INSTANT.format(from), StandardCharsets.UTF_8),
                URLEncoder.encode(DateTimeFormatter.ISO_INSTANT.format(to), StandardCharsets.UTF_8)
        ));
    }

    private HttpRequest buildRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();
    }
}

