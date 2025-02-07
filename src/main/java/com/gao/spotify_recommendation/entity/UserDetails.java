package com.gao.spotify_recommendation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name = "user_details")
@Getter
@Setter
public class UserDetails implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "access_token", length = 512)
    private String accessToken;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "ref_id", unique = true)
    private String refId;

    @Column(name = "display_name")
    private String displayName;
}
