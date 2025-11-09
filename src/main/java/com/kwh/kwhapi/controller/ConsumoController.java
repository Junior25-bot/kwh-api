package com.kwh.kwhapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping; // import para el nuevo método
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kwh.kwhapi.model.Consumo;
import com.kwh.kwhapi.service.ConsumoService;

import java.util.List;

@RestController
@RequestMapping("/api/consumo")
public class ConsumoController {

    @Autowired
    private ConsumoService consumoService;

    @PostMapping("/calcular")
    public ResponseEntity<?> calcular(@RequestBody Consumo datos) {
        try {
            Consumo resultado = consumoService.calcularYGuardar(datos);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // metodo para obtener el historial
    @GetMapping("/historial")
    public ResponseEntity<List<Consumo>> obtenerHistorial() {
        List<Consumo> historial = consumoService.obtenerTodos();
        return ResponseEntity.ok(historial);
    }
}


