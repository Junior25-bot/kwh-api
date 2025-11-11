package com.kwh.kwhapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kwh.kwhapi.model.Tarifa;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
}
