package com.universidad.enviosservice.estrategia;

public class EnvioOvernightStrategy implements EstrategiaEnvio {

    private static final TarifaPorTramos TARIFA = new TarifaPorTramos(2.0, 20, 2.5, 3.0);

    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        if (distanciaKm > 300) {
            throw new IllegalArgumentException("OVERNIGHT no disponible para mas de 300 km");
        }
        return TARIFA.costoBase(pesoKg, distanciaKm);
    }
}
