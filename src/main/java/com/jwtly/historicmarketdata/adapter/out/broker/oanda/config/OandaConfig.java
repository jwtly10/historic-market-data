package com.jwtly.historicmarketdata.adapter.out.broker.oanda.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jwtly.historicmarketdata.adapter.out.broker.oanda.OandaHistoricDataAdapter;
import com.jwtly.historicmarketdata.adapter.out.broker.oanda.client.OandaClient;
import com.jwtly.historicmarketdata.domain.port.out.HistoricDataPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class OandaConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public OandaClient oandaClient(
            @Value("${oanda.api-url}") String apiKey,
            @Value("${oanda.api-key}") String apiUrl
    ) {
        return new OandaClient(apiUrl, apiKey, httpClient(), objectMapper());
    }

    @Bean
    @Qualifier("oandaHistoricDataAdapter")
    public HistoricDataPort oandaHistoricDataAdapter(
            OandaClient oandaClient
    ) {
        return new OandaHistoricDataAdapter(oandaClient);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}