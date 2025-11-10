package com.kwh.kwhapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.kwh.kwhapi.model.Dispositivo;
import com.kwh.kwhapi.repository.DispositivoRepository;

@Component
public class Dataloader implements CommandLineRunner {

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica si ya hay dispositivos para no duplicarlos
        if(dispositivoRepository.count() == 0) {
            
            dispositivoRepository.save(new Dispositivo("Bombillo LED 10W", 0.01, "Iluminación"));
            dispositivoRepository.save(new Dispositivo("Ventilador", 0.05, "Climatización"));
            dispositivoRepository.save(new Dispositivo("Televisor LED 32\"", 0.08, "Entretenimiento"));
            dispositivoRepository.save(new Dispositivo("Microondas", 1.2, "Cocina"));
            dispositivoRepository.save(new Dispositivo("Refrigerador", 0.15, "Cocina"));
            dispositivoRepository.save(new Dispositivo("Lavadora", 0.5, "Lavado"));
            dispositivoRepository.save(new Dispositivo("Secadora de ropa", 2.0, "Lavado"));
            dispositivoRepository.save(new Dispositivo("Plancha", 1.0, "Cocina"));
            dispositivoRepository.save(new Dispositivo("Computadora de escritorio", 0.2, "Entretenimiento"));
            dispositivoRepository.save(new Dispositivo("Laptop", 0.05, "Entretenimiento"));
            dispositivoRepository.save(new Dispositivo("Aire acondicionado 1 ton", 1.0, "Climatización"));
            dispositivoRepository.save(new Dispositivo("Horno eléctrico", 1.5, "Cocina"));
            dispositivoRepository.save(new Dispositivo("Cafetera eléctrica", 1.0, "Cocina"));
            dispositivoRepository.save(new Dispositivo("Tostadora", 0.8, "Cocina"));
            dispositivoRepository.save(new Dispositivo("Hervidor eléctrico", 1.5, "Cocina"));
            dispositivoRepository.save(new Dispositivo("Router Wi-Fi", 0.02, "Electrónica"));
            dispositivoRepository.save(new Dispositivo("Aspiradora", 1.0, "Limpieza"));
            dispositivoRepository.save(new Dispositivo("Secador de cabello", 1.2, "Cuidado personal"));
            dispositivoRepository.save(new Dispositivo("Batidora", 0.3, "Cocina"));
            dispositivoRepository.save(new Dispositivo("Calefactor eléctrico", 1.5, "Climatización"));
        }
    }
}
