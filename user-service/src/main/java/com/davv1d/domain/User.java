package com.davv1d.domain;

import com.davv1d.event.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.util.Optional;

import static com.davv1d.domain.UserStatus.*;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor
public class User extends AbstractAggregateRoot<User> {
    @Id
    private String userId;
    private String email;
    private String password;
    private UserRole role;
    private UserStatus status;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    @Getter(AccessLevel.NONE)
    private PasswordReminderToken passwordReminderToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    @Getter(AccessLevel.NONE)
    private ActivationToken activationToken;


    public Optional<PasswordReminderToken> getPasswordReminderToken() {
        return Optional.ofNullable(passwordReminderToken);
    }

    public Optional<ActivationToken> getActivationToken() {
        return Optional.ofNullable(activationToken);
    }

    public User(String userId, String email, String password, UserRole role, String activationCode) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = INITIALIZED;
        this.activationToken = new ActivationToken(this, activationCode);
        this.registerEvent(new UserCreated(userId, email, role.toString(), status.toString(), activationCode));
    }

    public void activate(String activationCode) {
        if (isStatus(ACTIVATED)) throw new IllegalStateException("User is activated");
        if (isStatus(DEACTIVATED)) throw new IllegalStateException("User is deactivated");
        if (!this.activationToken.isCorrect(activationCode))
            throw new IllegalStateException("Activation code is incorrect");
        this.status = ACTIVATED;
        removeActivationToken();
        this.registerEvent(new UserActivated(this.userId));
    }

    public void deactivate() {
        if (isStatus(DEACTIVATED))
            throw new IllegalStateException("User is deactivated");
        status = DEACTIVATED;
        this.registerEvent(new UserDeactivated(this.userId));
    }

    public void changePassword(String newPassword, String token) {
        if (!isStatus(ACTIVATED)) throw new IllegalStateException("User is not activated");
        if (passwordReminderToken == null) throw new IllegalStateException("Not found token");
        if (!passwordReminderToken.isCorrect(token)) throw new IllegalStateException("Incorrect token");
        password = newPassword;
        removePasswordReminderToken();
        registerEvent(new UserPasswordChanged(this.getUserId()));
    }

    public void createPasswordRemindToken(String token) {
        if (!isStatus(ACTIVATED)) throw new IllegalStateException("User is not activated");
        passwordReminderToken = new PasswordReminderToken(this, token);
        registerEvent(new PasswordReminderTokenGenerated(this.userId, token));
    }

    public void removePasswordReminderToken() {
        this.passwordReminderToken = null;
    }

    public void removeActivationToken() {
        this.activationToken = null;
    }

    private boolean isStatus(UserStatus status) {
        return this.status.equals(status);
    }
}
