package com.example.musicrecommender.SpotifyAPI;

import java.util.Base64;

import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

public class SpotifyAPI {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyAPI.class);

    public SpotifyAuthResponse getAccessToken() {
        logger.info("Getting access token");
        String clientID = System.getProperty("SPOTIFY_CLIENT_ID");
        String clientSecret = System.getProperty("SPOTIFY_CLIENT_SECRET");

        String authHeader = "Basic " + Base64.getEncoder().encodeToString((clientID+":"+clientSecret).getBytes()); 
        LinkedMultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<String, String>();
        bodyMap.add("grant_type", "client_credentials");
    
        SpotifyAuthResponse response = WebClient.create("https://accounts.spotify.com")
                .post()
                .uri("/api/token")
                .headers(h -> {
                    h.add(HttpHeaders.AUTHORIZATION, authHeader);
                    h.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
                })
                .bodyValue(bodyMap)
                .retrieve()
                .bodyToMono(SpotifyAuthResponse.class)
                .block();

        logger.info("Access token acquired");
        return response;
    }
}

