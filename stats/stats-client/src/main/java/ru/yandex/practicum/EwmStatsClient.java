package ru.yandex.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.yaml.snakeyaml.util.UriEncoder.encode;

@Service
@Slf4j
public class EwmStatsClient {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final String application;
    private final String statsServiceUri;

    public EwmStatsClient(@Value("${app.name:ewm-main-service}") String application,
                          @Value("${app.stats.uri:http://localhost:9090}") String statsServiceUri) {
        this.application = application;
        this.statsServiceUri = statsServiceUri;

        this.restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(5000))
                .build();
    }

    public void hit(HttpServletRequest servletRequest) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(application)
                .ip(servletRequest.getRemoteAddr())
                .uri(servletRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        try {
            restTemplate.postForEntity(statsServiceUri + "/hit", endpointHitDto, Void.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.warn(e.getMessage());
        }
    }

    public List<EndpointStatsDto> stats(LocalDateTime start, LocalDateTime end, boolean unique, List<String> uris) {
        String query = toQueryString(start, end, unique, uris);

        try {
            ResponseEntity<List<EndpointStatsDto>> response = restTemplate.exchange(
                    query,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<EndpointStatsDto>>() {
                    }
            );

            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.warn(e.getMessage());
        }

        return Collections.emptyList();
    }

    private String toQueryString(LocalDateTime start, LocalDateTime end, boolean unique, List<String> uris) {
        String startStr = encode(DTF.format(start));
        String endStr = encode(DTF.format(end));

        String query = String.format("%s/stats?start=&%s&end=%s&unique=%b", statsServiceUri, startStr, endStr, unique);

        if (uris != null && !uris.isEmpty()) {
            query += "&uris=" + String.join(",", uris);
        }

        return query;
    }
}
