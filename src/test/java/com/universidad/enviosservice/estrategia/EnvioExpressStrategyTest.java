package com.universidad.enviosservice.estrategia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvioExpressStrategyTest {

    private final EnvioExpressStrategy estrategia = new EnvioExpressStrategy();

    @ParameterizedTest(name = "{0} kg, {1} km -> {2}")
    @CsvSource({
        "3.0,  100.0, 156.0",   // pesoKg <= 5, con el factor express de 1.3
        "5.0,  100.0, 156.0",   // limite 5 kg: ambas formulas coinciden (tarifa continua, sin salto de precio)
        "8.0,  100.0, 161.85",  // 5 < pesoKg <= 20
        "20.0, 100.0, 185.25",  // limite 20 kg: ambas formulas coinciden (tarifa continua, sin salto de precio)
        "25.0, 100.0, 198.25"   // pesoKg > 20
    })
    void calculaCostoPorTramoDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @ParameterizedTest(name = "{1} km -> {2}")
    @CsvSource({
        "3.0, 500.0, 780.0",    // limite: 500 km exactos no tienen recargo
        "3.0, 600.0, 968.5"     // distanciaKm > 500: recargo de 25 antes del factor 1.3
    })
    void aplicaRecargoSoloPorEncimaDe500Km(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
