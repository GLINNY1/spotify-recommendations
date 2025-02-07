package com.gao.spotify_recommendation.controller;

import com.gao.spotify_recommendation.entity.UserDetails;
import com.gao.spotify_recommendation.repository.UserDetailsRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;


@RestController
@Controller
public class SpotifyAuthController {

    private final SpotifyApi spotifyApi;
    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public SpotifyAuthController(SpotifyApi spotifyApi, UserDetailsRepository userDetailsRepository) {
        this.spotifyApi = spotifyApi;
        this.userDetailsRepository = userDetailsRepository;
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope("user-library-read user-top-read playlist-read-private")
                .show_dialog(true)
                .build();
        URI uri = authorizationCodeUriRequest.execute();
        response.sendRedirect(uri.toString());
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code, HttpServletResponse response) {
        try {
            AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
            AuthorizationCodeCredentials credentials = authorizationCodeRequest.execute();

            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());

            // Fetch user details from Spotify
            User user = spotifyApi.getCurrentUsersProfile().build().execute();
            System.out.println("User logged in: " + user.getId());

            // Save user in database
            UserDetails userDetails = userDetailsRepository.findByRefId(user.getId());
            if (userDetails == null) {
                userDetails = new UserDetails();
                userDetails.setRefId(user.getId());
            }
            userDetails.setAccessToken(credentials.getAccessToken());
            userDetails.setRefreshToken(credentials.getRefreshToken());
            userDetails.setDisplayName(user.getDisplayName());

            userDetailsRepository.save(userDetails);
            System.out.println("User " + user.getId() + " stored in DB.");

            // Redirect to frontend or valid page
            response.sendRedirect("http://localhost:3000/dashboard?userId=" + user.getId());

        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
            try {
                response.sendRedirect("http://localhost:3000/error");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
