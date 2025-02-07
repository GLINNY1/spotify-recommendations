package com.gao.spotify_recommendation.controller;

import com.gao.spotify_recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getRecommendations(@PathVariable String userId) {
        try {
            List<TrackSimplified> tracks = recommendationService.getRecommendations(userId);
            if (tracks.isEmpty()) {
                return ResponseEntity.status(404).body("No recommendations found.");
            }
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching recommendations: " + e.getMessage());
        }
    }
}
