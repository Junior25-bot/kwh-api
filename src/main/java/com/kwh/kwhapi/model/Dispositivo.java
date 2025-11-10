package com.kwh.kwhapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;    // Nombre del electrodoméstico
    private double potencia;  // Potencia en kW
    private String categoria; // Ejemplo: "Iluminación", "Cocina", "Climatización"

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPotencia() {
        return potencia;
    }

    public void setPotencia(double potencia) {
        this.potencia = potencia;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // Constructor vacío (ya lo tienes implícitamente)
public Dispositivo() {}

// Constructor con parámetros
public Dispositivo(String nombre, double potencia, String categoria) {
    this.nombre = nombre;
    this.potencia = potencia;
    this.categoria = categoria;
}

}

