package com.kwh.kwhapi.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kwh.kwhapi.model.Consumo;
import com.kwh.kwhapi.repository.ConsumoRepository; // Import necesario para el nuevo método

@Service
public class ConsumoService {

    @Autowired
    private ConsumoRepository consumoRepository;

    public Consumo calcularYGuardar(Consumo datos) {
        if (datos.getConsumo() < 0 || datos.getTarifa() < 0) {
            throw new IllegalArgumentException("Consumo y tarifa deben ser positivos.");
        }

        double total = datos.getConsumo() * datos.getTarifa();
        datos.setTotal(total);
        datos.setFecha(LocalDate.now());
        return consumoRepository.save(datos);
    }

    // método para consultar el historial
    public List<Consumo> obtenerTodos() {
        return consumoRepository.findAll();
    }
}



