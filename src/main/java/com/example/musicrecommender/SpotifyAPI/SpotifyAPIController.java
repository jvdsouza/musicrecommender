package com.example.musicrecommender.SpotifyAPI;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class SpotifyAPIController {
    private final SpotifyAPI spotifyAPI; 
    
    private SpotifyAPIController() {
        spotifyAPI = new SpotifyAPI();
    }
    
    @GetMapping("/track")
    public Object getAccessToken(@RequestParam(value="trackName", defaultValue="Glimpse Of Us") String trackName) {
        // SpotifyAuthResponse authResponse = spotifyApi.getAccessToken();
        return spotifyAPI.getTrack(trackName);
    }
}
