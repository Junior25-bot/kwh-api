package com.kwh.kwhapi.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kwh.kwhapi.model.Consumo;
import com.kwh.kwhapi.repository.ConsumoRepository;


@Service
public class ConsumoService {

    @Autowired
    private ConsumoRepository consumoRepository;

    // Método para calcular y guardar
    public Consumo calcularYGuardar(Consumo datos) {
        if (datos.getConsumo() <= 0) {
            throw new IllegalArgumentException("El consumo debe ser mayor a cero.");
        }

        double total = datos.getConsumo() * datos.getTarifa();
        datos.setTotal(total);

        return consumoRepository.save(datos);
    }

    // Método para obtener historial
    public List<Consumo> obtenerTodos() {
        return consumoRepository.findAll();
    }

    public List<Consumo> obtenerPorFecha(LocalDate fecha) {
    return consumoRepository.findByFecha(fecha);
}

}




