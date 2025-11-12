package com.kwh.kwhapi.repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kwh.kwhapi.model.Consumo;

@Repository
public interface ConsumoRepository extends JpaRepository<Consumo, Long> {
  List<Consumo> findByFecha(LocalDate fecha);


  List<Consumo> findByFechaBetween(LocalDate inicio, LocalDate fin);


}
