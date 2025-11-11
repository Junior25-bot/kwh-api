package com.kwh.kwhapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kwh.kwhapi.model.Tarifa;

import java.time.LocalDate;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    @Query("SELECT t FROM Tarifa t WHERE :fecha BETWEEN t.fechaInicio AND t.fechaFin")
    Tarifa findTarifaVigente(@Param("fecha") LocalDate fecha);
}

