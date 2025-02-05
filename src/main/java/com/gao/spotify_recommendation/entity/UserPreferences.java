package com.gao.spotify_recommendation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spotifyId; // Spotify user ID

    private String accessToken;
    private String refreshToken;

    @ElementCollection
    private List<String> favoriteArtistIds = new ArrayList<>();

    @ElementCollection
    private List<String> favoriteTrackIds = new ArrayList<>();

    @ElementCollection
    private List<String> preferredGenres = new ArrayList<>();
}