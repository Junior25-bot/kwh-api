package com.kwh.kwhapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwh.kwhapi.model.Consumo;
import com.kwh.kwhapi.model.Tarifa;
import com.kwh.kwhapi.service.ConsumoService;
import com.kwh.kwhapi.repository.TarifaRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsumoController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva filtros de seguridad
public class ConsumoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsumoService consumoService;

    @MockBean
    private TarifaRepository tarifaRepository;

    @Test
    public void testCalcularConsumoEndpoint() throws Exception {
        // Entrada simulada
        Consumo entrada = new Consumo();
        entrada.setConsumo(100.0);

        // Tarifa simulada
        Tarifa tarifaMock = new Tarifa();
        tarifaMock.setValorUnitario(0.5);

        // Salida esperada
        Consumo salida = new Consumo();
        salida.setConsumo(100.0);
        salida.setTarifa(0.5);
        salida.setTotal(50.0);
        salida.setFecha(LocalDate.now());

        // Simulaciones
        Mockito.when(tarifaRepository.findTarifaVigente(Mockito.any(LocalDate.class)))
               .thenReturn(tarifaMock);

        Mockito.when(consumoService.calcularYGuardar(Mockito.any(Consumo.class)))
               .thenReturn(salida);

        // Ejecución del endpoint
        mockMvc.perform(post("/api/consumo/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(50.0))
                .andExpect(jsonPath("$.consumo").value(100.0))
                .andExpect(jsonPath("$.tarifa").value(0.5));
    }
}


