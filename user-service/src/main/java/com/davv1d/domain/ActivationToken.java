package com.davv1d.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ACTIVATION_TOKENS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ActivationToken {
    @Id
    @Column(name = "USER_ID")
    private String id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
    @Column(name = "TOKEN", length = 40, unique = true, nullable = false)
    private String token;


    public ActivationToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public boolean isCorrect(String activationCode) {
        return this.token.equals(activationCode);
    }
}
