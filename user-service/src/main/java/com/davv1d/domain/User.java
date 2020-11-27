package com.davv1d.domain;

import com.davv1d.event.*;
import com.davv1d.validation.Result;
import com.davv1d.validation.Validator;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.util.Optional;

import static com.davv1d.domain.UserStatus.*;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Where(clause = "DELETED = false")
public class User extends AbstractAggregateRoot<User> {
    @Id
    @Column(name = "USER_ID", length = 40, unique = true, nullable = false)
    private String userId;
    @Column(name = "NAME", length = 50, unique = true, nullable = false)
    private String name;
    @Column(name = "EMAIL", length = 50, unique = true, nullable = false)
    private String email;
    @Column(name = "PASSWORD", nullable = false)
    private String password;
    @Column(name = "ROLE", length = 20, nullable = false)
    private UserRole role;
    @Column(name = "STATUS", length = 20, nullable = false)
    private UserStatus status;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.PRIVATE)
    private PasswordReminderToken passwordReminderToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    @Getter(AccessLevel.NONE)
    private ActivationToken activationToken;

    @Column(name = "DELETED")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean deleted = false;

    public User(String userId, String name, String email, String password, UserRole role, String activationCode) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = INITIALIZED;
        this.activationToken = new ActivationToken(this, activationCode);
        this.registerEvent(new UserCreated(userId, email, role.toString(), status.toString(), activationCode));
    }

    private <T> User(String userId, String name, String email, String password, UserRole role, UserStatus status, PasswordReminderToken passwordReminderToken, ActivationToken activationToken, boolean deleted, T event) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.passwordReminderToken = passwordReminderToken;
        this.activationToken = activationToken;
        this.deleted = deleted;
        this.registerEvent(event);
    }

    public Result<User> activate(String activationCode) {
        return new Validator.Builder<User>()
                .value(this)
                .hardCondition(u -> u.isStatus(ACTIVATED), "User is activated")
                .hardCondition(u -> u.isStatus(DEACTIVATED), "User is deactivated")
//                .hardCondition(u -> !u.activationToken.isCorrect(activationCode), "Activation code is incorrect") // unnecessary
                .successAction(user -> {
                    UserActivated userActivated = new UserActivated(userId);
                    return new User(userId, name, email, password, role, ACTIVATED, passwordReminderToken, null, deleted, userActivated);
                })
                .build()
                .getResult();
    }

    public Result<User> deactivate() {
        return new Validator.Builder<User>()
                .value(this)
                .hardCondition(u -> isStatus(DEACTIVATED), "User is deactivated")
                .successAction(u -> {
                    UserDeactivated userDeactivated = new UserDeactivated(userId);
                    return new User(userId, name, email, password, role, DEACTIVATED, passwordReminderToken, activationToken, deleted, userDeactivated);
                })
                .build()
                .getResult();
    }

    public Result<User> changePassword(String newPassword, String token) {
        return new Validator.Builder<User>()
                .value(this)
                .hardCondition(u -> !isStatus(ACTIVATED), "User is not activated")
                .hardCondition(u -> passwordReminderToken == null, "Not found token")
                .hardCondition(u -> !passwordReminderToken.isCorrect(token), "Incorrect token")
                .successAction(u -> {
                    UserPasswordChanged userPasswordChanged = new UserPasswordChanged(userId);
                    return new User(userId, name, email, newPassword, role, status, null, activationToken, deleted, userPasswordChanged);
                })
                .build()
                .getResult();
    }

    public Result<User> createPasswordRemindToken(String token) {
        return new Validator.Builder<User>()
                .value(this)
                .hardCondition(user -> !user.isStatus(ACTIVATED), "User is not activated")
                .hardCondition(user -> user.getPasswordReminderToken().isPresent(), "Reminder token is created")
                .successAction(user -> {
                    PasswordReminderTokenGenerated tokenGenerated = new PasswordReminderTokenGenerated(userId, token);
                    User user1 = new User(userId, name, email, password, role, status, passwordReminderToken, activationToken, deleted, tokenGenerated);
                    PasswordReminderToken reminderToken = new PasswordReminderToken(user1, token);
                    user1.setPasswordReminderToken(reminderToken);
                    return user1;
                })
                .build()
                .getResult();
    }

    public User delete() {
        return new User(userId, name, email, password, role, status, passwordReminderToken, activationToken, true);
    }

    public User removePasswordReminderToken() {
        return new User(userId, name, email, password, role, status, null, activationToken, deleted);
    }

    private boolean isStatus(UserStatus status) {
        return this.status.equals(status);
    }

    public Optional<ActivationToken> getActivationToken() {
        return Optional.ofNullable(activationToken);
    }

    public Optional<PasswordReminderToken> getPasswordReminderToken() {
        return Optional.ofNullable(passwordReminderToken);
    }
}
