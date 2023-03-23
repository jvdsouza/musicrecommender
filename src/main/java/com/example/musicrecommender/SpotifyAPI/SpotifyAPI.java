package com.example.musicrecommender.SpotifyAPI;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

public class SpotifyAPI {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyAPI.class);
    private final String spotifyAPIURL = "https://api.spotify.com/v1/";
    private long accessTokenExpiryEpoch;
    private String accessToken; 

    public SpotifyAPI() {
        refreshAccessToken();
    }

    private SpotifyAuthResponse getAccessToken() {
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

    private void refreshAccessToken() {
        if(System.currentTimeMillis() > accessTokenExpiryEpoch) {
            SpotifyAuthResponse authResponse = getAccessToken();
            this.accessTokenExpiryEpoch = System.currentTimeMillis() + (authResponse.expires_in * 1000);
            this.accessToken = "Bearer " + authResponse.access_token;
        }
    }

    private LinkedHashMap querySearchAPI(String query) {
        refreshAccessToken();
        String urlQuery = spotifyAPIURL + "search?" + query;
        return WebClient
            .create(urlQuery)
            .get()
            .headers(h -> {
                h.add(HttpHeaders.AUTHORIZATION, this.accessToken);
                h.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            })
            .retrieve()
            .bodyToMono(LinkedHashMap.class)
            .block();
    }

    public LinkedHashMap getTrack(String trackName) {
        trackName.replace(" ", "%20");
        HashMap<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("track", trackName);
        String queryString = "q=track:" + trackName + "&type=track";
        LinkedHashMap queryResult = querySearchAPI(queryString);
        LinkedHashMap tracks = (LinkedHashMap) queryResult.get("tracks");
        ArrayList items = (ArrayList) tracks.get("items");
        return (LinkedHashMap) items.get(0);
    }
}

