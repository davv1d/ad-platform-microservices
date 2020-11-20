package com.davv1d.repository;

import com.davv1d.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByActivationToken_Token(String activationCode);
    Optional<User> findByEmail(String email);
}
