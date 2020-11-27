package com.davv1d.domain;

import com.davv1d.event.PasswordReminderTokenGenerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;

@Entity
@Table(name = "PASSWORD_REMINDER_TOKENS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PasswordReminderToken extends AbstractAggregateRoot<PasswordReminderToken> {
    @Id
    @Column(name = "USER_ID")
    private String id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
    @Column(name = "TOKEN", length = 40, unique = true, nullable = false)
    private String token;

    public PasswordReminderToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.registerEvent(new PasswordReminderTokenGenerated(user.getUserId(), token));
    }

    public boolean isCorrect(String reminderToken) {
        return this.token.equals(reminderToken);
    }
}
