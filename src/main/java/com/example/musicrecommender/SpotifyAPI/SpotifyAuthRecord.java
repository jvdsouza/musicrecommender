package com.example.musicrecommender.SpotifyAPI;

public record SpotifyAuthRecord(String access_token, String token_type, long expires_in){};