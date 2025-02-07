package com.gao.spotify_recommendation.repository;

import com.gao.spotify_recommendation.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {
    UserDetails findByRefId(String refId);
}
