package com.gao.spotify_recommendation.controller;

import com.gao.spotify_recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.model_objects.specification.Track;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/top-tracks/pretty/{userId}")
    public ResponseEntity<?> getPrettyUserTopTracks(@PathVariable String userId) {
        try {
            List<Track> tracks = recommendationService.getUserTopTracks(userId);

            if (tracks.isEmpty()) {
                return ResponseEntity.status(404).body("No top tracks found.");
            }

            List<Map<String, Object>> simplifiedOutput = tracks.stream().map(track -> {
                Map<String, Object> trackInfo = new HashMap<>();
                trackInfo.put("name", track.getName());
                trackInfo.put("uri", track.getUri());
                trackInfo.put("artists", Arrays.stream(track.getArtists())
                        .map(artist -> artist.getName())
                        .collect(Collectors.toList()));
                return trackInfo;
            }).collect(Collectors.toList());

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String prettyJson = mapper.writeValueAsString(simplifiedOutput);

            return ResponseEntity.ok(prettyJson);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching top tracks: " + e.getMessage());
        }
    }
}
