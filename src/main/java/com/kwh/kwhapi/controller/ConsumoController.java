package com.kwh.kwhapi.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired; // import para el nuevo método
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kwh.kwhapi.model.Consumo;
import com.kwh.kwhapi.model.Tarifa;
import com.kwh.kwhapi.repository.TarifaRepository;
import com.kwh.kwhapi.service.ConsumoService;

@RestController
@RequestMapping("/api/consumo")
public class ConsumoController {

    @Autowired
    private TarifaRepository tarifaRepository;
    @Autowired
private ConsumoService consumoService;



    //metodo para calcular el consumo
   @PostMapping("/calcular")
public ResponseEntity<?> calcular(@RequestBody Consumo datos) {
    try {
        LocalDate hoy = LocalDate.now();
        Tarifa tarifa = tarifaRepository.findTarifaVigente(hoy);

        if (tarifa == null) {
            return ResponseEntity.badRequest().body("No hay tarifa vigente para la fecha actual.");
        }

        datos.setTarifa(tarifa.getValorUnitario());
        datos.setFecha(hoy); // si tienes un campo fecha en Consumo

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

    @GetMapping("/historial/fecha")
public ResponseEntity<List<Consumo>> historialPorFecha(@RequestParam String fecha) {
    LocalDate fechaParseada = LocalDate.parse(fecha);
    List<Consumo> historial = consumoService.obtenerPorFecha(fechaParseada);
    return ResponseEntity.ok(historial);
}

}


