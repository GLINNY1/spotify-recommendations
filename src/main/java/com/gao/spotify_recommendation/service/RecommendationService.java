package com.gao.spotify_recommendation.service;

import com.gao.spotify_recommendation.entity.UserDetails;
import com.gao.spotify_recommendation.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final SpotifyApi spotifyApi;
    private final UserDetailsRepository userDetailsRepository;

    public List<Track> getUserTopTracks(String userId) throws Exception {
        UserDetails userDetails = userDetailsRepository.findByRefId(userId);
        if (userDetails == null) {
            throw new Exception("User not found in database: " + userId);
        }

        spotifyApi.setAccessToken(userDetails.getAccessToken());

        try {
            GetUsersTopTracksRequest request = spotifyApi.getUsersTopTracks()
                    .limit(10)
                    .build();

            Paging<Track> topTracksPaging = request.execute();
            return Arrays.asList(topTracksPaging.getItems());

        } catch (SpotifyWebApiException e) {
            if (e.getMessage().contains("The access token expired")) {
                System.out.println("Access token expired for user: " + userId);
                refreshAccessToken(userDetails);
                return getUserTopTracks(userId);  // Retry after refreshing
            } else {
                throw e;
            }
        }
    }

    private void refreshAccessToken(UserDetails userDetails) throws IOException, SpotifyWebApiException, ParseException {
        AuthorizationCodeRefreshRequest refreshRequest = spotifyApi.authorizationCodeRefresh()
                .refresh_token(userDetails.getRefreshToken())
                .build();

        try {
            var refreshResponse = refreshRequest.execute(); // Throws ParseException
            String newAccessToken = refreshResponse.getAccessToken();

            // Update SpotifyApi and database with new access token
            spotifyApi.setAccessToken(newAccessToken);
            userDetails.setAccessToken(newAccessToken);
            userDetailsRepository.save(userDetails);

            System.out.println("Access token refreshed for user: " + userDetails.getRefId());

        } catch (IOException | SpotifyWebApiException e) {
            System.err.println("Error refreshing token: " + e.getMessage());
            throw e;
        }
    }
}
