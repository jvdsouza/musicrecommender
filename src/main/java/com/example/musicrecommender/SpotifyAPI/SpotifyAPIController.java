package com.example.musicrecommender.SpotifyAPI;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class SpotifyAPIController {
    
    @GetMapping("/")
    public SpotifyAuthRecord getAccessToken() {
        SpotifyAPI spotifyApi = new SpotifyAPI();
        SpotifyAuthResponse authResponse = spotifyApi.getAccessToken();
        return new SpotifyAuthRecord(authResponse.access_token, authResponse.token_type, authResponse.expires_in);
    }
}
