package com.gao.spotify_recommendation.repository;

import com.gao.spotify_recommendation.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    UserDetails findByRefId(String refId);
}
