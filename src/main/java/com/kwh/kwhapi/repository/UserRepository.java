package com.kwh.kwhapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kwh.kwhapi.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
