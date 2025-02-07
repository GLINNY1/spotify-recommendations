package com.gao.spotify_recommendation.service;

import com.gao.spotify_recommendation.entity.UserDetails;
import com.gao.spotify_recommendation.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;

import java.util.Arrays;
import java.util.List;

@Service
public class RecommendationService {

    private final SpotifyApi spotifyApi;
    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public RecommendationService(SpotifyApi spotifyApi, UserDetailsRepository userDetailsRepository) {
        this.spotifyApi = spotifyApi;
        this.userDetailsRepository = userDetailsRepository;
    }

    public List<TrackSimplified> getRecommendations(String userId) throws Exception {
        UserDetails user = userDetailsRepository.findByRefId(userId);
        if (user == null) {
            throw new Exception("User not found in database: " + userId);
        }

        spotifyApi.setAccessToken(user.getAccessToken());

        GetUsersTopTracksRequest topTracksRequest = spotifyApi.getUsersTopTracks()
                .limit(5)
                .time_range("medium_term")
                .build();
        Track[] topTracks = topTracksRequest.execute().getItems();
        if (topTracks.length == 0) {
            throw new Exception("No top tracks found.");
        }

        String seedTrack = topTracks[0].getId();
        GetRecommendationsRequest recommendationsRequest = spotifyApi.getRecommendations()
                .seed_tracks(seedTrack)
                .limit(10)
                .build();

        return Arrays.asList(recommendationsRequest.execute().getTracks());
    }
}
