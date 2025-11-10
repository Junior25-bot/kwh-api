package com.kwh.kwhapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kwh.kwhapi.model.Dispositivo;
import com.kwh.kwhapi.repository.DispositivoRepository;

@RestController
@RequestMapping("/dispositivos")
public class DispositivoController {

    @Autowired
    private DispositivoRepository dispositivoRepository;

    // Guardar un dispositivo
    @PostMapping("/agregar")
    public Dispositivo agregarDispositivo(@RequestBody Dispositivo dispositivo) {
        return dispositivoRepository.save(dispositivo);
    }

    // Listar todos los dispositivos
    @GetMapping("/listar")
    public List<Dispositivo> listarDispositivos() {
        return dispositivoRepository.findAll();
    }
}
