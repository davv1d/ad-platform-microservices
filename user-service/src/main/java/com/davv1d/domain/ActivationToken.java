package com.davv1d.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "ACTIVATION_TOKEN")
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
    private String token;


    public ActivationToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public boolean isCorrect(String activationCode) {
        return this.token.equals(activationCode);
    }
}
