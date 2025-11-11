package com.kwh.kwhapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwh.kwhapi.config.TestSecurityConfig;
import com.kwh.kwhapi.model.Consumo;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCalcularConsumoEndpoint() throws Exception {
        Consumo entrada = new Consumo();
        entrada.setConsumo(100.0);
        entrada.setTarifa(0.5);

        Consumo salida = new Consumo();
        salida.setConsumo(100.0);
        salida.setTarifa(0.5);
        salida.setTotal(50.0);
        salida.setFecha(LocalDate.now());

        Mockito.when(consumoService.calcularYGuardar(Mockito.any(Consumo.class))).thenReturn(salida);

        mockMvc.perform(post("/api/consumo/calcular")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(50.0))
                .andExpect(jsonPath("$.consumo").value(100.0))
                .andExpect(jsonPath("$.tarifa").value(0.5));
    }
}
