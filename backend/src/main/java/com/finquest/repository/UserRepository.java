package com.finquest.repository;

import com.finquest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// JpaRepository<Entity, PrimaryKeyType> provides built-in CRUD methods:
// save(), findById(), findAll(), deleteById(), count(), etc.
// No implementation needed — Spring Data generates it at runtime.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used during login to look up the account by email
    // Spring Data generates: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

<<<<<<< HEAD
// Used during registration to reject duplicate emails before attempting insert
    boolean existsByEmail(String email);

    // Used during password reset to look up the account that owns a reset code
    Optional<User> findByResetCode(String resetCode);
=======
    // Used during registration to reject duplicate emails before attempting insert
    boolean existsByEmail(String email);
>>>>>>> 348c16528166ec8e809d2b70a1061f3f9b6aa577
}
