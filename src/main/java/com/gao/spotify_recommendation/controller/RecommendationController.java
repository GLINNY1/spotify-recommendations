package com.gao.spotify_recommendation.controller;

import com.gao.spotify_recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import java.util.stream.Collectors;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/top-tracks/{userId}")
    public ResponseEntity<?> getUserTopTracks(@PathVariable String userId) {
        try {
            List<TrackSimplified> simplifiedTracks = recommendationService.getUserTopTracks(userId);
            List<Track> tracks = simplifiedTracks.stream()
                    .map(trackSimplified -> new Track.Builder()
                            .setId(trackSimplified.getId())
                            .setName(trackSimplified.getName())
                            .setArtists(trackSimplified.getArtists())
                            .setAlbum(null) // or handle appropriately if album data is needed
                            .setDiscNumber(trackSimplified.getDiscNumber())
                            .setDurationMs(trackSimplified.getDurationMs())
                            .setExplicit(trackSimplified.getIsExplicit())
                            .setHref(trackSimplified.getHref())
                            .setIsPlayable(trackSimplified.getIsPlayable())
                            .setPreviewUrl(trackSimplified.getPreviewUrl())
                            .setTrackNumber(trackSimplified.getTrackNumber())
                            .setUri(trackSimplified.getUri())
                            .build())
                    .collect(Collectors.toList());
            if (tracks.isEmpty()) {
                return ResponseEntity.status(404).body("No top tracks found.");
            }
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching top tracks: " + e.getMessage());
        }
    }

    @GetMapping("/top-artists/{userId}")
    public ResponseEntity<?> getUserTopArtists(@PathVariable String userId) {
        try {
            List<String> artists = recommendationService.getUserTopArtists(userId);
            if (artists.isEmpty()) {
                return ResponseEntity.status(404).body("No top artists found.");
            }
            return ResponseEntity.ok(artists);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching top artists: " + e.getMessage());
        }
    }
}