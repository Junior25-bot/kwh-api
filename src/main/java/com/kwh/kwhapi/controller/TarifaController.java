package com.kwh.kwhapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kwh.kwhapi.model.Tarifa;
import com.kwh.kwhapi.repository.TarifaRepository;

@RestController
@RequestMapping("/tarifas")
public class TarifaController {

    @Autowired
    private TarifaRepository tarifaRepository;

    // Listar todas las tarifas
    @GetMapping("/listar")
    public List<Tarifa> listarTarifas() {
        return tarifaRepository.findAll();
    }

    // Agregar una nueva tarifa
    @PostMapping("/agregar")
    public Tarifa agregarTarifa(@RequestBody Tarifa tarifa) {
        return tarifaRepository.save(tarifa);
    }

    // Buscar tarifa por ID
    @GetMapping("/{id}")
    public Tarifa obtenerTarifaPorId(@PathVariable Long id) {
        return tarifaRepository.findById(id).orElse(null);
    }
}

