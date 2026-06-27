package com.staynest.repository;

import com.staynest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserRepository - the Repository Pattern in action.
 *
 * WHY THIS IS JUST AN INTERFACE (no implementation we write ourselves):
 * Spring Data JPA generates the implementation for us AT RUNTIME using a
 * dynamic proxy. By extending JpaRepository<User, Long>, we instantly get
 * save(), findById(), findAll(), deleteById(), and more - without writing
 * a single line of SQL or boilerplate DAO code.
 *
 * Spring Data JPA can also derive queries from METHOD NAMES alone:
 * "findByEmail" is parsed as "SELECT * FROM users WHERE email = ?" -
 * Spring builds this query just by reading the method signature.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Used during login: Spring Security needs to look a user up by
     * their email (our "username" field) to check their password.
     * Returns Optional<User> instead of User directly, forcing the
     * caller to explicitly handle the "user not found" case instead
     * of risking a NullPointerException.
     */
    Optional<User> findByEmail(String email);

    /**
     * Used during registration, to check "is this email already taken?"
     * before we try to save - giving a friendly error instead of letting
     * the database throw a low-level constraint violation exception.
     */
    boolean existsByEmail(String email);
}
