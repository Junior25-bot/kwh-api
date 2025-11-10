package com.kwh.kwhapi.service;

import com.kwh.kwhapi.model.Consumo;
import com.kwh.kwhapi.repository.ConsumoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConsumoServiceTest {

    @Mock
    private ConsumoRepository consumoRepository;

    @InjectMocks
    private ConsumoService consumoService;

    public ConsumoServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalcularYGuardar() {
        // Arrange
        Consumo entrada = new Consumo();
        entrada.setConsumo(100.0);
        entrada.setTarifa(0.5);

        Consumo esperado = new Consumo();
        esperado.setConsumo(100.0);
        esperado.setTarifa(0.5);
        esperado.setTotal(50.0);
        esperado.setFecha(LocalDate.now());

        when(consumoRepository.save(any(Consumo.class))).thenReturn(esperado);

        // Act
        Consumo resultado = consumoService.calcularYGuardar(entrada);
        System.out.println("Total calculado: " + resultado.getTotal());
        System.out.println("Fecha asignada: " + resultado.getFecha());

        // Assert
        assertEquals(50.0, resultado.getTotal());
        assertEquals(LocalDate.now(), resultado.getFecha());
        verify(consumoRepository, times(1)).save(any(Consumo.class));
    }
    @Test
    public void testCalcularYGuardarConConsumoNegativo() {
        Consumo entrada = new Consumo();
        entrada.setConsumo(-10.0);
        entrada.setTarifa(0.5);

        assertThrows(IllegalArgumentException.class, () -> {
            consumoService.calcularYGuardar(entrada);
        });
    }

   @Test
public void testCalcularYGuardarConTarifaCero() {
    Consumo entrada = new Consumo();
    entrada.setConsumo(100.0);
    entrada.setTarifa(0.0);

    when(consumoRepository.save(any(Consumo.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Consumo resultado = consumoService.calcularYGuardar(entrada);
    assertEquals(0.0, resultado.getTotal());
}

}



