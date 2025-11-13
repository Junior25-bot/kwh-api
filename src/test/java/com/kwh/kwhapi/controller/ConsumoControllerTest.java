package com.kwh.kwhapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwh.kwhapi.config.TestSecurityConfig;
import com.kwh.kwhapi.model.Consumo;
import com.kwh.kwhapi.model.Tarifa;
import com.kwh.kwhapi.repository.TarifaRepository;
import com.kwh.kwhapi.service.ConsumoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsumoController.class)
@Import(TestSecurityConfig.class)
public class ConsumoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsumoService consumoService;

    @MockBean
    private TarifaRepository tarifaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCalcularConsumoEndpoint() throws Exception {
        // Datos de entrada
        Consumo entrada = new Consumo();
        entrada.setConsumo(100.0);
        entrada.setTarifa(0.5);

        // Tarifa simulada
        Tarifa tarifa = new Tarifa();
        tarifa.setValorUnitario(0.5);

        // Simulamos el comportamiento del repositorio
        Mockito.when(tarifaRepository.findTarifaVigente(Mockito.any(LocalDate.class)))
                .thenReturn(tarifa);

        // Respuesta simulada del servicio
        Consumo salida = new Consumo();
        salida.setConsumo(100.0);
        salida.setTarifa(0.5);
        salida.setTotal(50.0);
        salida.setFecha(LocalDate.now());

        Mockito.when(consumoService.calcularYGuardar(Mockito.any(Consumo.class)))
                .thenReturn(salida);

        // Ejecutamos el POST
        mockMvc.perform(post("/api/consumo/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(50.0))
                .andExpect(jsonPath("$.consumo").value(100.0))
                .andExpect(jsonPath("$.tarifa").value(0.5));
    }
}
