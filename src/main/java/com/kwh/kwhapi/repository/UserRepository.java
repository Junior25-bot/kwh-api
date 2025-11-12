package com.kwh.kwhapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kwh.kwhapi.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  // Buscar usuario por nombre de usuario (para login)
  Optional<User> findByUsername(String username);

   // Verificar si ya existe un usuario
    boolean existsByUsername(String username);
}
