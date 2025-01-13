package com.matheushdas.taskflowingapi.persistence;

import com.matheushdas.taskflowingapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT t FROM User t WHERE t.email = ?1 OR t.username = ?1")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
}
