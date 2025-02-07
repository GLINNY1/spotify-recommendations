package com.gao.spotify_recommendation.service;

import com.gao.spotify_recommendation.entity.UserDetails;
import com.gao.spotify_recommendation.repository.UserDetailsRepository;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.User;

@Service
public class UserProfileService {

    private final UserDetailsRepository userDetailsRepository;

    public UserProfileService(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    public UserDetails insertOrUpdateUserDetails(User user, String accessToken, String refreshToken) {
        UserDetails userDetails = userDetailsRepository.findByRefId(user.getId());
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setRefId(user.getId());
        }
        userDetails.setAccessToken(accessToken);
        userDetails.setRefreshToken(refreshToken);
        userDetails.setDisplayName(user.getDisplayName());
        // Set other user details as needed
        return userDetailsRepository.save(userDetails);
    }
}
