package com.universidad.enviosservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServicioCalculoEnvioTest {

    private final ServicioCalculoEnvio servicio = new ServicioCalculoEnvio();

    @Test
    void calculaEnvioEstandarPesoBajo() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "REGULAR", false);
        assertEquals(50.0, costo, 0.01);
    }

    @Test
    void aplicaDescuentoClientePremium() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "PREMIUM", false);
        assertEquals(45.0, costo, 0.01);
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
        "ESTANDAR,      50.0",
        "EXPRESS,       156.0",
        "OVERNIGHT,     220.0",
        "INTERNACIONAL, 230.0"
    })
    void delegaEnLaEstrategiaDelMetodoDeEnvio(String metodoEnvio, double esperado) {
        double costo = servicio.calcularCosto(metodoEnvio, 3.0, 100.0, "REGULAR", false);
        assertEquals(esperado, costo, 0.01);
    }

    @Test
    void aplicaRecargoPorFragilidad() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "REGULAR", true);
        assertEquals(57.5, costo, 0.01);
    }

    @Test
    void aplicaDescuentoClienteCorporativo() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "CORPORATIVO", false);
        assertEquals(42.5, costo, 0.01);
    }

    @Test
    void aplicaFragilidadAntesDelDescuentoPorCliente() {
        // (50 * 1.15) * 0.9: el orden de los ajustes transversales importa
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "PREMIUM", true);
        assertEquals(51.75, costo, 0.01);
    }

    @Test
    void rechazaMetodoDeEnvioNoSoportado() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
            () -> servicio.calcularCosto("AEREO", 3.0, 100.0, "REGULAR", false));
        assertEquals("Metodo de envio no soportado: AEREO", error.getMessage());
    }
}
